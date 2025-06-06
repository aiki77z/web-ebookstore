package com.ebookstore.interceptor;

import com.ebookstore.dto.UserInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员权限拦截器
 * 检查用户是否有管理员权限
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {
    
    private static final String SESSION_USER_KEY = "currentUser";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        // 处理OPTIONS请求（CORS预检请求）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 检查Session中是否有用户信息
        HttpSession session = request.getSession(false);
        if (session != null) {
            UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute(SESSION_USER_KEY);
            if (userInfo != null && "ADMIN".equals(userInfo.getRole())) {
                return true;
            }
        }
        
        // 没有管理员权限，返回403错误
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "需要管理员权限");
        
        response.getWriter().write(objectMapper.writeValueAsString(result));
        return false;
    }
} 