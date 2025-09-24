package com.ebookstore.service;

import com.ebookstore.dto.LoginDTO;
import com.ebookstore.dto.LoginResponseDTO;
import com.ebookstore.dto.RegisterDTO;
import com.ebookstore.dto.UserInfoDTO;

import javax.servlet.http.HttpSession;

/**
 * 认证服务接口
 * 体现接口与实现分离的设计原则
 */
public interface AuthService {
    
    /**
     * 用户登录
     * 验证用户名密码，使用BCrypt确保密码安全
     */
    LoginResponseDTO login(LoginDTO loginDTO, HttpSession session);
    
    /**
     * 用户注册
     * 使用BCrypt加密密码存储
     */
    LoginResponseDTO register(RegisterDTO registerDTO);
    
    /**
     * 用户退出登录
     */
    void logout(HttpSession session);
    
    /**
     * 检查用户登录状态
     */
    UserInfoDTO getCurrentUser(HttpSession session);
    
    /**
     * 验证密码安全性
     */
    Boolean validatePassword(String rawPassword, String encodedPassword);
    
    /**
     * 加密密码
     */
    String encodePassword(String rawPassword);
} 