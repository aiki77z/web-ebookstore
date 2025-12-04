package com.ebookstore.controller;

import com.ebookstore.service.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据迁移控制器
 * 提供数据迁移相关的API端点
 */
@RestController
@RequestMapping("/api/migration")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MigrationController {
    
    @Autowired
    private MigrationService migrationService;
    
    /**
     * 将MySQL中的书籍数据迁移到MongoDB
     */
    @PostMapping("/mongodb")
    public ResponseEntity<Map<String, Object>> migrateToMongoDB() {
        Map<String, Object> response = new HashMap<>();
        try {
            int count = migrationService.migrateBooksToMongoDB();
            response.put("success", true);
            response.put("message", "数据迁移成功");
            response.put("migratedCount", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("MongoDB迁移失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "数据迁移失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 为现有书籍批量添加示例标签
     */
    @PostMapping("/tags")
    public ResponseEntity<Map<String, Object>> addSampleTags() {
        Map<String, Object> response = new HashMap<>();
        try {
            migrationService.addSampleTagsToBooks();
            response.put("success", true);
            response.put("message", "标签添加成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("标签添加失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "标签添加失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 执行完整迁移（MongoDB + 标签）
     */
    @PostMapping("/all")
    public ResponseEntity<Map<String, Object>> migrateAll() {
        Map<String, Object> response = new HashMap<>();
        try {
            int mongoCount = migrationService.migrateBooksToMongoDB();
            migrationService.addSampleTagsToBooks();
            
            response.put("success", true);
            response.put("message", "完整迁移成功");
            response.put("mongoDBCount", mongoCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("完整迁移失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "完整迁移失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}

