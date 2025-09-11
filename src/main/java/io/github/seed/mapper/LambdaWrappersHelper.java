package io.github.seed.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

/**
 * LambdaWrappers帮助类
 *
 * @author zhangdp
 * @since 2025/9/11
 */
public interface LambdaWrappersHelper<T> {

    /**
     * 获取查询的LambdaQueryWrapper
     *
     * @return
     */
    default LambdaQueryWrapper<T> lambdaQueryWrapper() {
        return Wrappers.lambdaQuery();
    }

    /**
     * 获取修改的LambdaUpdateWrapper
     *
     * @return
     */
    default LambdaUpdateWrapper<T> lambdaUpdateWrapper() {
        return Wrappers.lambdaUpdate();
    }
}
