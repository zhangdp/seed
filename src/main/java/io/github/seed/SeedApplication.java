package io.github.seed;

import io.github.seed.common.annotation.EnableLogOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 启动类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableLogOperation
public class SeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeedApplication.class, args);
    }

}
