package com.ebookstore.controller;

import com.ebookstore.dto.UserListDTO;
import com.ebookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserListDTO>> getAllUsers() {
        List<UserListDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(@PathVariable Long userId) {
        try {
            userService.toggleUserStatus(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户状态更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新用户状态失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<UserListDTO>> getTopSpenders(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<UserListDTO> topSpenders = userService.getTopSpenders(startDate, endDate);
        return ResponseEntity.ok(topSpenders);
    }
} 