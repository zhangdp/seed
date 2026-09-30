package io.github.seed.manager;

import cn.hutool.v7.core.collection.CollUtil;
import io.github.seed.common.constant.EventConst;
import io.github.seed.event.data.ServiceEvent;
import io.github.seed.entity.sys.Permission;
import io.github.seed.entity.sys.Role;
import io.github.seed.module.security.component.RolePermissionProvider;
import io.github.seed.module.security.data.RolePermissionGrantedAuthority;
import io.github.seed.module.security.data.SecurityConst;
import io.github.seed.service.sys.PermissionService;
import io.github.seed.service.sys.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 角色权限缓存：jwt只带角色编码，权限映射留在各节点内存里，请求鉴权不再查库也不查redis
 * <br>启动时全量预热——不预热的话冷启动期间权限是空的，请求会直接403
 * <br>同步方式与jti黑名单一致：只广播版本号，落后就全量重拉，避免丢消息导致节点间长期不一致
 * <br>权限变更后调用{@link #publishChange()}通知各节点；改动用户角色属于换令牌的事，
 * 走{@code TokenService}把该用户踢下线
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RolePermissionManager implements RolePermissionProvider, InitializingBean {

    /**
     * 兜底对齐间隔
     */
    private static final Duration ALIGN_INTERVAL = Duration.ofMinutes(1);

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisMessageListenerContainer listenerContainer;
    private final TaskScheduler taskScheduler;

    /**
     * 角色授权标识 -> 该角色拥有的授权列表（含角色自身）
     */
    private final Map<String, List<RolePermissionGrantedAuthority>> cache = new ConcurrentHashMap<>();
    /**
     * 内存数据版本号
     */
    private volatile long version = -1L;

    @Override
    public void afterPropertiesSet() {
        this.reload();
        MessageListenerAdapter adapter = new MessageListenerAdapter(this, "onVersionChanged");
        adapter.setSerializer(RedisSerializer.string());
        adapter.afterPropertiesSet();
        this.listenerContainer.addMessageListener(adapter, new ChannelTopic(SecurityConst.REDIS_ROLE_PERMISSION_CHANNEL));
        this.taskScheduler.scheduleAtFixedRate(this::reloadIfStale, ALIGN_INTERVAL);
        log.info("角色权限缓存已预热，角色数：{}，版本号：{}", this.cache.size(), this.version);
    }

    @Override
    public List<RolePermissionGrantedAuthority> listAuthorities(Collection<String> roleCodes) {
        List<RolePermissionGrantedAuthority> authorities = new ArrayList<>();
        if (CollUtil.isEmpty(roleCodes)) {
            return authorities;
        }
        for (String roleCode : roleCodes) {
            String authority = RolePermissionGrantedAuthority.roleAuthority(roleCode);
            if (authority == null) {
                continue;
            }
            // 缓存里没有的角色按需补一次，新增角色后不必等其他节点广播
            List<RolePermissionGrantedAuthority> list = this.cache.computeIfAbsent(authority, this::loadRole);
            authorities.addAll(list);
        }
        return authorities;
    }

    /**
     * 权限或角色数据发生变更：递增版本号并广播，各节点收到后全量重拉
     *
     * @param event 业务事件，由权限、角色的写操作通过{@code @PublishEvent}发出
     */
    @EventListener(condition = "#event.type == '" + EventConst.ROLE_PERMISSION_CHANGE + "'")
    public void onRolePermissionChanged(ServiceEvent event) {
        this.publishChange();
    }

    /**
     * 权限或角色权限关系发生变更后调用：递增版本号并广播，各节点收到后全量重拉
     */
    public void publishChange() {
        long newVersion = this.incrVersion();
        this.version = newVersion;
        this.reload();
        this.stringRedisTemplate.convertAndSend(SecurityConst.REDIS_ROLE_PERMISSION_CHANNEL, String.valueOf(newVersion));
        log.info("角色权限变更已广播，版本号：{}，角色数：{}", newVersion, this.cache.size());
    }

    /**
     * redis变更通知
     *
     * @param message 版本号字符串
     */
    public void onVersionChanged(String message) {
        if (this.parseVersion(message) > this.version) {
            this.reload();
        }
    }

    /**
     * 版本号落后时重拉，供定时任务兜底
     */
    public void reloadIfStale() {
        if (this.readVersion() > this.version) {
            this.reload();
        }
    }

    /**
     * 全量重拉角色与权限
     */
    public void reload() {
        List<Role> roles = this.roleService.listAll();
        Map<String, List<RolePermissionGrantedAuthority>> fresh = new HashMap<>();
        if (CollUtil.isNotEmpty(roles)) {
            for (Role role : roles) {
                String authority = RolePermissionGrantedAuthority.roleAuthority(role.getCode());
                if (authority == null) {
                    continue;
                }
                fresh.put(authority, this.loadRole(authority, role));
            }
        }
        this.cache.clear();
        this.cache.putAll(fresh);
        this.version = this.readVersion();
        log.info("角色权限缓存已刷新，角色数：{}，授权总数：{}，版本号：{}", this.cache.size(),
                fresh.values().stream().mapToInt(List::size).sum(), this.version);
    }

    /**
     * 加载单个角色的权限，用于按需补齐
     *
     * @param authority 角色授权标识
     * @return 该角色的授权列表
     */
    private List<RolePermissionGrantedAuthority> loadRole(String authority) {
        Role role = this.roleService.getByCode(authority.startsWith(SecurityConst.ROLE_PREFIX)
                ? authority.substring(SecurityConst.ROLE_PREFIX.length()) : authority);
        return this.loadRole(authority, role);
    }

    /**
     * 组装角色的授权列表：角色自身 + 该角色拥有的权限
     *
     * @param authority 角色授权标识
     * @param role      角色，为空时只返回角色自身
     * @return 授权列表
     */
    private List<RolePermissionGrantedAuthority> loadRole(String authority, Role role) {
        List<RolePermissionGrantedAuthority> list = new ArrayList<>();
        if (role == null) {
            list.add(new RolePermissionGrantedAuthority(authority, RolePermissionGrantedAuthority.AuthorityType.ROLE, null));
            return list;
        }
        list.add(new RolePermissionGrantedAuthority(authority, RolePermissionGrantedAuthority.AuthorityType.ROLE, role.getId()));
        List<Permission> permissions = this.permissionService.listRoleResources(role.getId());
        if (CollUtil.isNotEmpty(permissions)) {
            for (Permission permission : permissions) {
                String code = RolePermissionGrantedAuthority.permissionAuthority(permission.getCode());
                if (code != null) {
                    list.add(new RolePermissionGrantedAuthority(code, RolePermissionGrantedAuthority.AuthorityType.PERMISSION, permission.getId()));
                }
            }
        }
        return list;
    }

    /**
     * 版本号自增
     *
     * @return 自增后的版本号
     */
    private long incrVersion() {
        Long v = this.stringRedisTemplate.opsForValue().increment(SecurityConst.REDIS_ROLE_PERMISSION_VERSION);
        return v == null ? System.currentTimeMillis() : v;
    }

    /**
     * 读取redis版本号
     *
     * @return 版本号，key不存在时为0
     */
    private long readVersion() {
        return this.parseVersion(this.stringRedisTemplate.opsForValue().get(SecurityConst.REDIS_ROLE_PERMISSION_VERSION));
    }

    /**
     * 解析版本号
     *
     * @param value 版本号字符串
     * @return 版本号，空或非数字时为0
     */
    private long parseVersion(String value) {
        if (value == null || value.isBlank()) {
            return 0L;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

}
