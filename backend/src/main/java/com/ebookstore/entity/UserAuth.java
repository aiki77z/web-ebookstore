package com.ebookstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户认证信息实体类
 * 与User实体分离，符合数据库设计范式要求
 */
@Entity
@Table(name = "user_auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, length = 255)
    private String passwordHash; // BCrypt加密后的密码
    
    @Column(nullable = false, length = 20)
    private String role = "USER"; // 角色：USER, ADMIN
    
    @Column(nullable = false)
    private Boolean active = true; // 账户是否激活
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    // 与User实体一对一关联
    @OneToOne(mappedBy = "userAuth", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 