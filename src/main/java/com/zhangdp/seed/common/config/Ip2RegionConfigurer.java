package com.zhangdp.seed.common.config;

import com.zhangdp.seed.common.component.Ip2RegionSearcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 2023/5/30
 *
 * @author zhangdp
 * @since
 */
@Configuration
public class Ip2RegionConfigurer {

    @Value("${ip2region.file:/Users/peng/Downloads/ip2region.xdb}")
    private String dbPath;

    @Bean
    public Ip2RegionSearcher ip2RegionSearcher() {
        return new Ip2RegionSearcher(this.dbPath);
    }

}
