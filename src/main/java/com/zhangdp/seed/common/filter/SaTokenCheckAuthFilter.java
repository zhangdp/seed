package com.zhangdp.seed.common.filter;

import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.router.SaRouter;
import com.zhangdp.seed.common.strategy.SaTokenCheckAuthErrorStrategy;
import com.zhangdp.seed.common.strategy.SaTokenCheckAuthStrategy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 2023/5/25 sa-token 认证检查过滤器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class SaTokenCheckAuthFilter extends OncePerRequestFilter {

    /**
     * 拦截路由
     */
    private final List<String> includeList = new ArrayList<>(32);
    /**
     * 放行路由
     */
    private final List<String> excludeList = new ArrayList<>(32);

    /**
     * 检查策略：每次请求执行
     */
    private SaTokenCheckAuthStrategy checkStrategy = req -> log.warn("未配置任何认证检查策略!!!");

    /**
     * 检查异常处理策略：每次[检查]发生异常时执行
     */
    private SaTokenCheckAuthErrorStrategy errorStrategy = (req, res, e) -> {
        log.warn("SaTokenCheckAuthFilter 失败: uri={}, error={}", req.getRequestURI(), e.getLocalizedMessage());
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    };

    /**
     * 添加 [拦截路由]
     *
     * @param paths 路由
     * @return 对象自身
     */
    public SaTokenCheckAuthFilter includePaths(String... paths) {
        includeList.addAll(Arrays.asList(paths));
        return this;
    }

    /**
     * 添加 [放行路由]
     *
     * @param paths 路由
     * @return 对象自身
     */
    public SaTokenCheckAuthFilter excludePaths(String... paths) {
        excludeList.addAll(Arrays.asList(paths));
        return this;
    }

    /**
     * 注册[检查策略]: 每次请求执行
     *
     * @param checkStrategy 检查策略
     * @return 对象自身
     */
    public SaTokenCheckAuthFilter check(@NotNull SaTokenCheckAuthStrategy checkStrategy) {
        this.checkStrategy = checkStrategy;
        return this;
    }

    /**
     * 每次[检查]发生异常时执行此策略
     *
     * @param errorStrategy 异常策略
     * @return 对象自身
     */
    public SaTokenCheckAuthFilter error(@NotNull SaTokenCheckAuthErrorStrategy errorStrategy) {
        this.errorStrategy = errorStrategy;
        return this;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("SaTokenCheckAuthFilter in: {}", request.getRequestURI());
            }
            // 执行全局过滤器
            SaRouter.match(includeList).notMatch(excludeList).check(s -> checkStrategy.run(request));
            // 检查成功，请求放行
            filterChain.doFilter(request, response);
        } catch (StopMatchException e) {
            // 停止检查，请求放行
            filterChain.doFilter(request, response);
        } catch (Throwable e) {
            // 检查出错
            errorStrategy.run(request, response, e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (log.isInfoEnabled()) {
            log.info("SaTokenFilter init: includeList={}, excludeList={}", this.includeList, this.excludeList);
        }
    }
}
