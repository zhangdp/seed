package io.github.seed.common.component;

import com.mybatisflex.annotation.UpdateListener;
import io.github.seed.entity.BaseEntity;

import java.time.LocalDateTime;

/**
 * MybatisFlex修改监听
 *
 * @author zhangdp
 * @since 2025/9/16
 */
public class MybatisFlexUpdateListener implements UpdateListener {

    @Override
    public void onUpdate(Object entity) {
        // 设置最后更新时间
        if (entity instanceof BaseEntity<?> b) {
            b.setLastModifiedDate(LocalDateTime.now());
        }
    }
}
