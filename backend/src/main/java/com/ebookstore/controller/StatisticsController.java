package com.ebookstore.controller;

import com.ebookstore.dto.BookSalesStatisticsDto;
import com.ebookstore.dto.PersonalStatisticsDto;
import com.ebookstore.dto.UserConsumptionStatisticsDto;
import com.ebookstore.dto.UserInfoDTO;
import com.ebookstore.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取书籍销量统计（热销榜）- 管理员功能
     */
    @GetMapping("/books")
    public ResponseEntity<?> getBookSalesStatistics(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            HttpSession session) {
        
        try {
            System.out.println("=== 书籍销量统计请求 ===");
            System.out.println("开始时间: " + startDate);
            System.out.println("结束时间: " + endDate);
            
            // 检查管理员权限
            UserInfoDTO currentUser = (UserInfoDTO) session.getAttribute("currentUser");
            System.out.println("当前用户: " + (currentUser != null ? currentUser.getUsername() : "null"));
            System.out.println("用户角色: " + (currentUser != null ? currentUser.getRole() : "null"));
            
            if (currentUser == null) {
                return ResponseEntity.status(401).body("用户未登录");
            }
            
            if (!"ADMIN".equals(currentUser.getRole())) {
                return ResponseEntity.status(403).body("只有管理员可以查看书籍销量统计");
            }
            
            List<BookSalesStatisticsDto> statistics = statisticsService.getBookSalesStatistics(startDate, endDate);
            System.out.println("查询到的统计数据数量: " + (statistics != null ? statistics.size() : 0));
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            System.err.println("书籍销量统计异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("获取书籍销量统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户消费统计（消费榜）- 管理员功能
     */
    @GetMapping("/users")
    public ResponseEntity<?> getUserConsumptionStatistics(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            HttpSession session) {
        
        try {
            System.out.println("=== 用户消费统计请求 ===");
            System.out.println("开始时间: " + startDate);
            System.out.println("结束时间: " + endDate);
            
            // 检查管理员权限
            UserInfoDTO currentUser = (UserInfoDTO) session.getAttribute("currentUser");
            System.out.println("当前用户: " + (currentUser != null ? currentUser.getUsername() : "null"));
            System.out.println("用户角色: " + (currentUser != null ? currentUser.getRole() : "null"));
            
            if (currentUser == null) {
                return ResponseEntity.status(401).body("用户未登录");
            }
            
            if (!"ADMIN".equals(currentUser.getRole())) {
                return ResponseEntity.status(403).body("只有管理员可以查看用户消费统计");
            }
            
            List<UserConsumptionStatisticsDto> statistics = statisticsService.getUserConsumptionStatistics(startDate, endDate);
            System.out.println("查询到的用户消费统计数据数量: " + (statistics != null ? statistics.size() : 0));
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            System.err.println("用户消费统计异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("获取用户消费统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取个人购书统计 - 用户功能
     */
    @GetMapping("/personal")
    public ResponseEntity<?> getPersonalStatistics(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            HttpSession session) {
        
        try {
            System.out.println("=== 个人购书统计请求 ===");
            System.out.println("开始时间: " + startDate);
            System.out.println("结束时间: " + endDate);
            
            // 检查用户登录状态
            UserInfoDTO currentUser = (UserInfoDTO) session.getAttribute("currentUser");
            System.out.println("当前用户: " + (currentUser != null ? currentUser.getUsername() : "null"));
            System.out.println("用户ID: " + (currentUser != null ? currentUser.getId() : "null"));
            
            if (currentUser == null) {
                return ResponseEntity.status(401).body("用户未登录");
            }
            
            Long userId = currentUser.getId();
            
            PersonalStatisticsDto statistics = statisticsService.getPersonalStatistics(userId, startDate, endDate);
            System.out.println("个人统计数据: " + statistics);
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            System.err.println("个人统计异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("获取个人统计失败: " + e.getMessage());
        }
    }
} 