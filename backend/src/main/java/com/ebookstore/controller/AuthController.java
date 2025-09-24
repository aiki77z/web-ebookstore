package com.ebookstore.controller;

import com.ebookstore.dto.*;
import com.ebookstore.service.SessionTimerService;
import com.ebookstore.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、注册、登出等认证相关请求
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")//跨域
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;//依赖注入：自动注入AuthService实例 业务逻辑交给

    @Autowired
    private SessionTimerService sessionTimerService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")//处理发送到/login路径的POST请求
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO, //从请求体中解析出LoginDTO对象
                                                HttpSession session) {//自动注入的http会话对象
        LoginResponseDTO response = authService.login(loginDTO, session);//loginDTO包含了用户输入的用户名和密码，loginResponseDTO包含了登录结果
        
        // 根据登录结果返回不同的HTTP状态码
        if (response.getSuccess()) {
            sessionTimerService.start();
            return ResponseEntity.ok(response);//登录成功返回200
        } else {
            return ResponseEntity.ok(response);//登录失败仍然返回200，但在响应体中包含错误信息
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register") 
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        LoginResponseDTO response = authService.register(registerDTO);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        long elapsedMs = sessionTimerService.stopandGetElapsedTime();

        String username = getCurrentUsername(session); // 需要实现这个方法
        logger.info("用户登出 - 用户名: {}, 会话时长: {} 毫秒 (约 {} 分钟)",
                username, elapsedMs, elapsedMs / 60000.0);
        authService.logout(session);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登出成功");
        response.put("sessionDurationMs", elapsedMs);
        return ResponseEntity.ok(response);
    }

    private String getCurrentUsername(HttpSession session) {
        UserInfoDTO currentUser = authService.getCurrentUser(session);
        return currentUser.getUsername();
    }

    /**
     * 检查登录状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(HttpSession session) {
        UserInfoDTO currentUser = authService.getCurrentUser(session);
        
        Map<String, Object> response = new HashMap<>();
        
        if (currentUser != null) {
            response.put("isLoggedIn", true);
            response.put("userInfo", currentUser);
        } else {
            response.put("isLoggedIn", false);
            response.put("userInfo", null);
        }
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        UserInfoDTO currentUser = authService.getCurrentUser(session);
        
        Map<String, Object> response = new HashMap<>();
        
        if (currentUser != null) {
            response.put("success", true);
            response.put("userInfo", currentUser);
        } else {
            response.put("success", false);
            response.put("message", "用户未登录");
        }
        
        return ResponseEntity.ok(response);
    }
} 