package com.ebookstore.service;

import com.ebookstore.dto.UserDTO;
import com.ebookstore.entity.User;

public interface UserService {
    
    User getCurrentUser(); // 获取当前登录用户(简化版，实际应通过认证后获取)
    
    UserDTO getUserInfo();
    
    UserDTO updateUserInfo(UserDTO userDTO);
} 