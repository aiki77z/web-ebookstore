package com.ebookstore.service.impl;

import com.ebookstore.dto.UserDTO;
import com.ebookstore.entity.User;
import com.ebookstore.repository.UserRepository;
import com.ebookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // 简化版，实际项目应使用Spring Security处理认证并获取当前用户
    @Override
    public User getCurrentUser() {
        // 为了演示，固定使用ID为1的用户
        return userRepository.findById(1L)
                .orElseGet(() -> {
                    // 如果不存在则创建默认用户
                    User defaultUser = new User();
                    defaultUser.setName("TOM");
                    defaultUser.setEmail("cat@qq.com");
                    defaultUser.setAddress("上海市闵行区");
                    return userRepository.save(defaultUser);
                });
    }
    
    @Override
    public UserDTO getUserInfo() {
        User user = getCurrentUser();
        return convertToDTO(user);
    }
    
    @Override
    public UserDTO updateUserInfo(UserDTO userDTO) {
        User user = getCurrentUser();
        
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setAddress(userDTO.getAddress());
        
        user = userRepository.save(user);
        return convertToDTO(user);
    }
    
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAddress()
        );
    }
} 