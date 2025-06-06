package com.ebookstore.config;

import com.ebookstore.interceptor.AdminInterceptor;
import com.ebookstore.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 注册拦截器，配置CORS
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private LoginInterceptor loginInterceptor;
    
    @Autowired
    private AdminInterceptor adminInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册登录拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**") // 只对API路径生效
                .excludePathPatterns(
                        "/api/auth/**",  // 排除认证相关路径
                        "/api/health",   // 排除健康检查
                        "/error"         // 排除错误页面
                );
        
        // 注册管理员权限拦截器
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**"); // 只对管理员API路径生效
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置CORS，允许前端跨域访问
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // 允许Cookie
                .maxAge(3600);
    }
} 