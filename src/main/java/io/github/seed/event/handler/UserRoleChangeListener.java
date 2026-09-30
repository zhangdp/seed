package io.github.seed.event.handler;

import io.github.seed.common.constant.EventConst;
import io.github.seed.entity.sys.UserRole;
import io.github.seed.event.data.ServiceEvent;
import io.github.seed.module.security.component.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户角色变化监听器
 * <br>令牌里的角色是登录那一刻的快照，改了用户的角色后旧令牌仍带着旧角色，
 * 因此把该用户已签发的令牌全部作废，逼其重新登录拿新角色；
 * 只改角色对应的权限不需要踢人，那个走角色权限缓存刷新
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRoleChangeListener {

    private final SecurityService securityService;

    /**
     * 作废被改角色用户的全部令牌
     *
     * @param event 业务事件，参数可能是用户id、用户角色关联或其集合
     */
    @EventListener(condition = "#event.type == '" + EventConst.USER_ROLE_CHANGE + "'")
    public void onUserRoleChanged(ServiceEvent event) {
        for (Long userId : this.resolveUserIds(event)) {
            if (userId == null) {
                continue;
            }
            int count = this.securityService.kickUser(userId);
            log.info("用户角色变化已作废其令牌，userId={}，数量={}", userId, count);
        }
    }

    /**
     * 从事件参数里取用户id
     *
     * @param event 业务事件
     * @return 用户id列表，取不到时为空
     */
    private List<Long> resolveUserIds(ServiceEvent event) {
        List<Long> userIds = new ArrayList<>();
        if (!event.hasParam()) {
            return userIds;
        }
        for (Object value : event.getParams().values()) {
            this.collectUserIds(value, userIds);
        }
        return userIds;
    }

    /**
     * 收集参数里的用户id：用户id本身、用户角色关联、以及它们的集合
     *
     * @param value   事件参数
     * @param userIds 收集结果
     */
    @SuppressWarnings("unchecked")
    private void collectUserIds(Object value, List<Long> userIds) {
        if (value == null) {
            return;
        }
        if (value instanceof Long userId) {
            userIds.add(userId);
        } else if (value instanceof UserRole userRole) {
            userIds.add(userRole.getUserId());
        } else if (value instanceof Collection<?> coll) {
            for (Object item : coll) {
                this.collectUserIds(item, userIds);
            }
        }
    }

}
