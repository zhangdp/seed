package io.github.seed.common.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import io.github.seed.common.component.MybatisFlexFillTimeListener;
import io.github.seed.common.component.MybatisFlexFillUserListener;
import io.github.seed.entity.BaseEntity;
import io.github.seed.entity.BaseLogicAuditableEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis-Flex配置
 *
 * @author zhangdp
 * @since 2025/9/16
 */
@Slf4j
@Configuration
public class MyBatisFlexConfiguration implements MyBatisFlexCustomizer {

    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        MybatisFlexFillTimeListener fillTimeListener = new MybatisFlexFillTimeListener();
        globalConfig.registerInsertListener(fillTimeListener, BaseEntity.class);
        log.info("Mybatis-Flex配置插入监听器：{}", fillTimeListener);
        globalConfig.registerUpdateListener(fillTimeListener, BaseEntity.class);
        log.info("Mybatis-Flex配置修改监听器：{}", fillTimeListener);

        MybatisFlexFillUserListener fillUserListener = new MybatisFlexFillUserListener();
        globalConfig.registerInsertListener(fillUserListener, BaseLogicAuditableEntity.class);
        log.info("Mybatis-Flex配置插入监听器：{}", fillUserListener);
        globalConfig.registerUpdateListener(fillUserListener, BaseLogicAuditableEntity.class);
        log.info("Mybatis-Flex配置修改监听器：{}", fillUserListener);
    }
}
