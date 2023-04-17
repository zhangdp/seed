package com.zhangdp.seed.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 2023/4/14 sa-token配置
 *
 * @author zhangdp
 * @since
 */
@Configuration
public class SaTokenConfigurer implements WebMvcConfigurer {

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                // 全局忽略登陆校验的路径
                .excludePathPatterns("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/druid/**");
    }

}
