package io.github.seed.common.component;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import io.github.seed.common.security.SecurityUtils;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.entity.BaseEntity;
import io.github.seed.entity.BaseLogicAuditableEntity;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 2025/12/11 mybatis-flex 自动填充操作者监听器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class MybatisFlexFillUserListener implements InsertListener, UpdateListener {

    @Override
    public void onInsert(Object entity) {
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
    }

    @Override
    public void onUpdate(Object entity) {
        // 设置最后者
        if (entity instanceof BaseLogicAuditableEntity b && b.getUpdatedAt() == null) {
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
