package io.github.seed.common.config;

import com.mybatisflex.core.FlexGlobalConfig;
import io.github.seed.common.component.MybatisFlexInsertListener;
import io.github.seed.common.component.MybatisFlexUpdateListener;
import io.github.seed.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis-Flex配置
 * todo 需要更优雅的配置方式
 *
 * @author zhangdp
 * @since 2025/9/16
 */
@Slf4j
@Configuration
public class MyBatisFlexConfiguration {

    public MyBatisFlexConfiguration() {
        MybatisFlexInsertListener mybatisFlexInsertListener = new MybatisFlexInsertListener();
        MybatisFlexUpdateListener mybatisFlexUpdateListener = new MybatisFlexUpdateListener();
        FlexGlobalConfig config = FlexGlobalConfig.getDefaultConfig();
        // 设置BaseEntity类启用
        config.registerInsertListener(mybatisFlexInsertListener, BaseEntity.class);
        log.info("Mybatis-Flex配置插入监听器：{}", mybatisFlexInsertListener);
        config.registerUpdateListener(mybatisFlexUpdateListener, BaseEntity.class);
        log.info("Mybatis-Flex配置修改监听器：{}", mybatisFlexUpdateListener);
    }
}
