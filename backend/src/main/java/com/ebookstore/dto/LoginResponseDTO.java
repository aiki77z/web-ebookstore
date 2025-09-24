package com.ebookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    
    private Boolean success;
    private String message;
    private UserInfoDTO userInfo;
    
    public static LoginResponseDTO success(UserInfoDTO userInfo) {
        return new LoginResponseDTO(true, "登录成功", userInfo);
    }
    
    public static LoginResponseDTO failure(String message) {
        return new LoginResponseDTO(false, message, null);
    }
} 