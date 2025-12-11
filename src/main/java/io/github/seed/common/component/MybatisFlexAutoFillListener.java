package io.github.seed.common.component;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import io.github.seed.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 2025/12/11 mybatis-flex 自动填充监听器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class MybatisFlexAutoFillListener implements InsertListener, UpdateListener {

    @Override
    public void onInsert(Object entity) {
        // 自动设置创建时间
        if (entity instanceof BaseEntity<?> b && b.getCreatedAt() == null) {
            LocalDateTime now = LocalDateTime.now();
            b.setCreatedAt(now);
            b.setUpdatedAt(now);
            if (log.isTraceEnabled()) {
                log.trace("Insert时自动设置创建时间，entity: {}, time: {}", entity.getClass().getName(), now);
            }
        }
    }

    @Override
    public void onUpdate(Object entity) {
        // 设置最后更新时间
        if (entity instanceof BaseEntity<?> b && b.getUpdatedAt() == null) {
            LocalDateTime now = LocalDateTime.now();
            b.setUpdatedAt(now);
            if (log.isTraceEnabled()) {
                log.trace("Update时自动设置修改时间，entity: {}, time: {}", entity.getClass().getName(), now);
            }
        }
    }
}
