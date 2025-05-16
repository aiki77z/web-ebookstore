package com.ebookstore.controller;

import com.ebookstore.dto.UserDTO;
import com.ebookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/info")
    public ResponseEntity<UserDTO> getUserInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }
    
    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUserInfo(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUserInfo(userDTO));
    }
} 