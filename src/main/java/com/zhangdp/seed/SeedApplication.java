package com.zhangdp.seed;

import com.zhangdp.seed.common.aspect.EnableLogOperate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableLogOperate
public class SeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeedApplication.class, args);
    }

}
