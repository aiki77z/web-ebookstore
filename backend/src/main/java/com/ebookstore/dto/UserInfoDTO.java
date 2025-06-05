package com.ebookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String username;
    private String role;
} 