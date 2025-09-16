package io.github.seed.common.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import io.github.seed.common.component.MybatisFlexInsertListener;
import io.github.seed.common.component.MybatisFlexUpdateListener;
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
        MybatisFlexInsertListener mybatisFlexInsertListener = new MybatisFlexInsertListener();
        MybatisFlexUpdateListener mybatisFlexUpdateListener = new MybatisFlexUpdateListener();
        globalConfig.registerInsertListener(mybatisFlexInsertListener, BaseEntity.class);
        log.info("Mybatis-Flex配置插入监听器：{}", mybatisFlexInsertListener);
        globalConfig.registerUpdateListener(mybatisFlexUpdateListener, BaseEntity.class);
        log.info("Mybatis-Flex配置修改监听器：{}", mybatisFlexUpdateListener);
    }
}
