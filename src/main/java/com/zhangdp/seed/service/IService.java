package com.zhangdp.seed.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

/**
 * 2023/5/17 service层接口类
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface IService {

    /**
     * 返回mybatis-plus LambdaQueryWrapper
     *
     * @param tClass
     * @param <T>
     * @return
     */
    default <T> LambdaQueryWrapper<T> lambdaQuery(Class<T> tClass) {
        return Wrappers.lambdaQuery(tClass);
    }

    /**
     * 返回mybatis-plus LambdaUpdateWrapper
     *
     * @param tClass
     * @param <T>
     * @return
     */
    default <T> LambdaUpdateWrapper<T> lambdaUpdate(Class<T> tClass) {
        return Wrappers.lambdaUpdate(tClass);
    }
}
