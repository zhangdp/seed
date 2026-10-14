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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色权限缓存：jwt只带角色编码，权限映射留在各节点内存里，请求鉴权不再查库也不查redis
 * <br>启动时全量预热——不预热的话冷启动期间权限是空的，请求会直接403
 * <br>同步方式与jti黑名单一致：只广播"变了"这件事，收到就全量重拉；不做版本号比对，
 * pub/sub断线期间的通知必然丢失，靠下一次任何节点的变更通知顺带补齐，也可以调{@link #reload()}立刻对齐。
 * 定时兜底不放在模块里：需要时由应用侧{@code RolePermissionRefreshTask}按{@code app.security}配置调本方法
 * <br>缓存用不可变map整体替换：先clear再putAll会留下一段"什么权限都没有"的窗口，正好撞上403
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

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisMessageListenerContainer listenerContainer;

    /**
     * 角色授权标识 -> 该角色拥有的授权列表（含角色自身）
     */
    private volatile Map<String, List<RolePermissionGrantedAuthority>> cache = Map.of();

    @Override
    public void afterPropertiesSet() {
        // 先挂监听再预热：反过来的话，预热与挂监听之间那几毫秒发出的通知收不到，本节点会漏掉
        MessageListenerAdapter adapter = new MessageListenerAdapter(this, "onChanged");
        adapter.setSerializer(RedisSerializer.string());
        adapter.afterPropertiesSet();
        this.listenerContainer.addMessageListener(adapter, new ChannelTopic(SecurityConst.REDIS_ROLE_PERMISSION_CHANNEL));
        this.reload();
        log.info("角色权限缓存已预热，角色数：{}", this.cache.size());
    }

    @Override
    public List<RolePermissionGrantedAuthority> listAuthorities(Collection<String> roleCodes) {
        List<RolePermissionGrantedAuthority> authorities = new ArrayList<>();
        if (CollUtil.isEmpty(roleCodes)) {
            return authorities;
        }
        Map<String, List<RolePermissionGrantedAuthority>> snapshot = this.cache;
        for (String roleCode : roleCodes) {
            String authority = RolePermissionGrantedAuthority.roleAuthority(roleCode);
            if (authority == null) {
                continue;
            }
            List<RolePermissionGrantedAuthority> list = snapshot.get(authority);
            // 缓存里没有的角色按需补一次：新建角色后不必等广播，别的节点漏了通知也能自愈
            if (list == null) {
                list = this.loadAndCache(authority);
            }
            authorities.addAll(list);
        }
        return authorities;
    }

    /**
     * 权限或角色数据发生变更：重拉本节点并广播，各节点收到后各自全量重拉
     *
     * @param event 业务事件，由权限、角色的写操作通过{@code @PublishEvent}发出
     */
    @EventListener(condition = "#event.type == '" + EventConst.ROLE_PERMISSION_CHANGE + "'")
    public void onRolePermissionChanged(ServiceEvent event) {
        this.publishChange();
    }

    /**
     * 权限或角色权限关系发生变更后调用：重拉本节点并广播
     */
    public void publishChange() {
        this.reload();
        this.stringRedisTemplate.convertAndSend(SecurityConst.REDIS_ROLE_PERMISSION_CHANNEL, SecurityConst.REDIS_CHANGE_SIGNAL);
        log.info("角色权限变更已广播，角色数：{}", this.cache.size());
    }

    /**
     * 收到变更通知：直接全量重拉，不做差异比对
     *
     * @param message 通知内容，不使用
     */
    public void onChanged(String message) {
        this.reload();
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
        Map<String, List<RolePermissionGrantedAuthority>> before = this.cache;
        this.cache = Map.copyOf(fresh);
        // 定时刷新多数前后一致，只在真正变化时记info，否则高频刷新会把日志刷满
        if (before.equals(this.cache)) {
            log.debug("角色权限缓存刷新完成，无变化，角色数：{}", fresh.size());
        } else {
            log.info("角色权限缓存已刷新，角色数：{} -> {}，授权总数：{}", before.size(), fresh.size(),
                    fresh.values().stream().mapToInt(List::size).sum());
        }
    }

    /**
     * 加载单个角色并放进缓存，用于按需补齐
     *
     * @param authority 角色授权标识
     * @return 该角色的授权列表
     */
    private List<RolePermissionGrantedAuthority> loadAndCache(String authority) {
        List<RolePermissionGrantedAuthority> list = this.loadRole(authority);
        Map<String, List<RolePermissionGrantedAuthority>> merged = new HashMap<>(this.cache);
        merged.put(authority, List.copyOf(list));
        this.cache = Map.copyOf(merged);
        return list;
    }

    /**
     * 加载单个角色
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
}
