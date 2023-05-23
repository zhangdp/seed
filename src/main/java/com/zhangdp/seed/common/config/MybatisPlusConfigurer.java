package com.zhangdp.seed.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zhangdp.seed.common.constant.CommonConst;
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
public class MybatisPlusConfigurer {

    /**
     * 字段自动填充配置
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
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
                this.strictInsertFill(metaObject, "status", Integer.class, CommonConst.GOOD);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
