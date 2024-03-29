package com.zhangdp.seed.common.constant;

import java.time.Duration;

/**
 * 2023/4/7 缓存相关常量
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface CacheConst {

    /**
     * 默认过期时间：1小时
     */
    Duration TTL = Duration.ofHours(1);
    /**
     * 缓存key前缀
     */
    String PREFIX = "cache";
    /**
     * redis key 文件夹分割
     */
    String SPLIT = "::";
}
