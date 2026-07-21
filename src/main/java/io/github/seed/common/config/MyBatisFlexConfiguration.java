package io.github.seed.common.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import io.github.seed.common.component.MybatisFlexAutoFillListener;
import io.github.seed.entity.BaseEntity;
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
        MybatisFlexAutoFillListener autoFillListener = new MybatisFlexAutoFillListener();
        globalConfig.registerInsertListener(autoFillListener, BaseEntity.class);
        log.info("Mybatis-Flex配置插入自动填充监听器：{}", autoFillListener);
        globalConfig.registerUpdateListener(autoFillListener, BaseEntity.class);
        log.info("Mybatis-Flex配置修改自动填充监听器：{}", autoFillListener);
    }
}
