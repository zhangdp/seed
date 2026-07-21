package io.github.seed.common.component;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import io.github.seed.common.constant.Const;
import io.github.seed.common.security.SecurityUtils;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.entity.BaseEntity;
import io.github.seed.entity.BaseLogicAuditableEntity;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 2025/12/11 mybatis-flex 自动填充时间监听器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class MybatisFlexAutoFillListener implements InsertListener, UpdateListener {

    @Override
    public void onInsert(Object entity) {
        // 自动设置创建时间
        if (entity instanceof BaseEntity b && b.getCreatedAt() == null) {
            LocalDateTime now = LocalDateTime.now();
            b.setCreatedAt(now);
            // 初始化时修改时间即为创建时间
            b.setUpdatedAt(now);
            if (log.isTraceEnabled()) {
                log.trace("Insert时自动设置创建时间，entity: {}, time: {}", entity.getClass().getName(), now);
            }
        }
        // 自动设置创建者
        if (entity instanceof BaseLogicAuditableEntity b && b.getCreatedBy() == null) {
            LoginUser user = SecurityUtils.getLoginUser();
            if (user != null) {
                b.setCreatedBy(user.getId());
                // 初始化时修改者默认为创建者
                b.setUpdatedBy(user.getId());
                if (log.isTraceEnabled()) {
                    log.trace("Insert时自动设置创建者，entity: {}, user: {}", entity.getClass().getName(), user.simpleString());
                }
            }
        }
        // 自动设置逻辑删除字段
        if (entity instanceof BaseLogicAuditableEntity b && b.getDeleted() == null) {
            b.setDeleted(Const.NO_FALSE);
            if (log.isTraceEnabled()) {
                log.trace("Insert时自动设置逻辑删除，entity: {}", entity.getClass().getName());
            }
        }
    }

    @Override
    public void onUpdate(Object entity) {
        // 设置最后更新时间
        if (entity instanceof BaseEntity b && b.getUpdatedAt() == null) {
            LocalDateTime now = LocalDateTime.now();
            b.setUpdatedAt(now);
            if (log.isTraceEnabled()) {
                log.trace("Update时自动设置修改时间，entity: {}, time: {}", entity.getClass().getName(), now);
            }
        }
        // 设置最后修改者
        if (entity instanceof BaseLogicAuditableEntity b && b.getUpdatedBy() == null) {
            LoginUser user = SecurityUtils.getLoginUser();
            if (user != null) {
                b.setUpdatedBy(user.getId());
                if (log.isTraceEnabled()) {
                    log.trace("Insert时自动设置修改者，entity: {}, user: {}", entity.getClass().getName(), user.simpleString());
                }
            }
        }
    }
}
