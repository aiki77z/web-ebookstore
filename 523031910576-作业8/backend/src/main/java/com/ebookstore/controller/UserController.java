package com.ebookstore.controller;

import com.ebookstore.dto.UserDTO;
import com.ebookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        try {
            UserDTO userInfo = userService.getUserInfo();//函数依赖 获取用户信息
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", userInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取用户信息失败：" + e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
    
    @PutMapping("/update")//更新用户信息 请求json->UserDTO->User实体->UserDTO->响应json
    public ResponseEntity<Map<String, Object>> updateUserInfo(@RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUserInfo(userDTO);//函数依赖 更新用户信息
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedUser);
            response.put("message", "用户信息更新成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新用户信息失败：" + e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
} 