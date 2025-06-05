package com.ebookstore.service.impl;

import com.ebookstore.dto.UserDTO;
import com.ebookstore.entity.User;
import com.ebookstore.repository.UserRepository;
import com.ebookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ebookstore.dto.UserInfoDTO;
import com.ebookstore.service.AuthService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpSession;

import javax.persistence.EntityNotFoundException;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;
    

    @Override
    public User getCurrentUser() {
        try {
            // 获取当前HTTP会话
            ServletRequestAttributes attr = (ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(false);

            if (session == null) {
                throw new RuntimeException("用户未登录");
            }

            // 通过AuthService获取当前登录用户信息
            UserInfoDTO currentUserInfo = authService.getCurrentUser(session);
            if (currentUserInfo == null) {
                throw new RuntimeException("用户未登录");
            }

            // 根据用户ID获取用户实体
            User user = userRepository.findById(currentUserInfo.getId())
                    .orElseThrow(() -> new EntityNotFoundException("用户不存在"));

            return user;
        } catch (Exception e) {
            throw new RuntimeException("获取当前用户失败: " + e.getMessage());
        }
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