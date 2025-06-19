package com.ebookstore.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户列表DTO - 用于管理员查看用户信息
 */
@Data
public class UserListDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    // 构造函数
    public UserListDTO() {}
    
    public UserListDTO(Long id, String username, String name, String email, 
                      String phone, String role, Boolean active, 
                      LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }
} 