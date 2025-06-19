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
@Component//标记为Spring组件，以便Spring能够管理它
public class AdminInterceptor implements HandlerInterceptor {
    
    private static final String SESSION_USER_KEY = "currentUser";//Session中存储用户信息的键
    private final ObjectMapper objectMapper = new ObjectMapper();//用于将Java对象转换为JSON字符串
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        // 处理OPTIONS请求（CORS预检请求）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;//放行OPTIONS请求，允许跨域请求
        }
        
        // 检查Session中是否有用户信息
        HttpSession session = request.getSession(false);
        if (session != null) {
            UserInfoDTO userInfo = (UserInfoDTO) session.getAttribute(SESSION_USER_KEY);
            if (userInfo != null && "ADMIN".equals(userInfo.getRole())) {
                return true;//用户是管理员，放行请求
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