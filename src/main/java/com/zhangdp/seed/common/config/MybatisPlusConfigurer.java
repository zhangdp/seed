package com.zhangdp.seed.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * 2023/4/3 mybatis-plus 自定义配置
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class MybatisPlusConfigurer {

    /**
     * 自动填充配置
     *
     * @return
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                if (log.isTraceEnabled()) {
                    log.trace("填充createTime字段：{}", now);
                }
                this.strictInsertFill(metaObject, "update", LocalDateTime.class, now);
                if (log.isTraceEnabled()) {
                    log.trace("填充update字段：{}", now);
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                if (log.isTraceEnabled()) {
                    log.trace("填充updateTime字段：{}", now);
                }
            }
        };
    }
}
