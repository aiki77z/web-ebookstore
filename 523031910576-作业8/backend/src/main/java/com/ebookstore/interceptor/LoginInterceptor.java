package com.ebookstore.interceptor;

import com.ebookstore.dto.UserInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录拦截器
 * 实现Session管理和登录状态检查
 * 除login链接外，其他链接均需检验用户是否登录
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    private static final String SESSION_USER_KEY = "currentUser";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String requestURI = request.getRequestURI();
        
        // 允许的无需登录的路径
        if (isAllowedPath(requestURI)) {
            return true;
        }
        
        // 处理OPTIONS请求（CORS预检请求）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 检查Session中是否有用户信息
        HttpSession session = request.getSession(false);
        if (session != null) {
            UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute(SESSION_USER_KEY);
            if (userInfo != null) {
                return true;
            }
        }
        
        // 未登录，返回401错误
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "请先登录");
        
        response.getWriter().write(objectMapper.writeValueAsString(result));
        return false;
    }
    
    /**
     * 判断是否为允许未登录访问的路径
     */
    private boolean isAllowedPath(String path) {
        return path.startsWith("/api/auth/") ||      // 认证相关路径
               path.startsWith("/api/books") ||      // 书籍浏览（公开访问）
               path.startsWith("/api/tags") ||      // 标签查询（公开访问）
               path.startsWith("/api/migration/") || // 数据迁移API（系统管理功能）
               path.equals("/api/health") ||         // 健康检查
               path.equals("/api/error");            // 错误页面
    }
} 