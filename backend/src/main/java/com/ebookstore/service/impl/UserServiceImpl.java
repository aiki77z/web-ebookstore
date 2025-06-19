package com.ebookstore.service.impl;

import com.ebookstore.dto.UserDTO;
import com.ebookstore.dto.UserListDTO;
import com.ebookstore.entity.User;
import com.ebookstore.entity.UserAuth;
import com.ebookstore.repository.UserRepository;
import com.ebookstore.repository.UserAuthRepository;
import com.ebookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ebookstore.dto.UserInfoDTO;
import com.ebookstore.service.AuthService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpSession;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private AuthService authService;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserListDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        UserAuth userAuth = userAuthRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        userAuth.setActive(!userAuth.getActive());
        userAuthRepository.save(userAuth);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserListDTO> getTopSpenders(String startDate, String endDate) {
        // 这里需要根据订单统计用户消费，暂时返回空列表
        // 在完整实现中，需要关联订单表进行统计
        return getAllUsers();
    }

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
    @Transactional
    public UserDTO updateUserInfo(UserDTO userDTO) {
        User user = getCurrentUser();
        if (user == null) {
            throw new EntityNotFoundException("Current user not found");
        }

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());

        user = userRepository.save(user);
        return convertToUserDTO(user);
    }
    
    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        return dto;
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress()
        );
    }

    private UserListDTO convertToUserListDTO(User user) {
        UserAuth userAuth = user.getUserAuth();
        return new UserListDTO(
                user.getId(),
                userAuth.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                userAuth.getRole(),
                userAuth.getActive(),
                userAuth.getCreatedAt(),
                userAuth.getLastLogin()
        );
    }
} 