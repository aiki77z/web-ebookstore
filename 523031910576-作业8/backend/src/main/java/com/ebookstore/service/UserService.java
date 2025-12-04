package com.ebookstore.service;

import com.ebookstore.dto.UserDTO;
import com.ebookstore.dto.UserListDTO;
import com.ebookstore.entity.User;

import java.util.List;

public interface UserService {
    
    User getUserById(Long id);
    
    List<UserListDTO> getAllUsers();
    
    void toggleUserStatus(Long userId);
    
    List<UserListDTO> getTopSpenders(String startDate, String endDate);
    
    User getCurrentUser(); // 获取当前登录用户(简化版，实际应通过认证后获取)
    
    UserDTO getUserInfo();
    
    UserDTO updateUserInfo(UserDTO userDTO);
} 