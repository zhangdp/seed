package io.github.seed.common.component;

import com.mybatisflex.annotation.InsertListener;
import io.github.seed.entity.BaseEntity;

import java.time.LocalDateTime;

/**
 * MybatisFlex 插入监听
 *
 * @author zhangdp
 * @since 2025/9/16
 */
public class MybatisFlexInsertListener implements InsertListener {

    @Override
    public void onInsert(Object entity) {
        // 自动设置创建时间
        if (entity instanceof BaseEntity<?> b) {
            b.setCreatedDate(LocalDateTime.now());
            b.setLastModifiedDate(b.getCreatedDate());
        }
    }
}
