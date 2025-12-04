package com.ebookstore.controller;

import com.ebookstore.dto.TagDTO;
import com.ebookstore.entity.neo4j.Tag;
import com.ebookstore.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 标签控制器
 * 提供标签相关的API
 */
@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    /**
     * 获取所有标签
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTags() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Tag> tags = tagService.getAllTags();
            // 转换为DTO，避免循环引用问题
            List<TagDTO> tagDTOs = tags.stream()
                    .map(tag -> new TagDTO(tag.getName(), tag.getDescription()))
                    .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("data", tagDTOs);
            System.out.println("成功返回 " + tagDTOs.size() + " 个标签");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("获取标签列表失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "获取标签列表失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 根据标签名称查找相关标签（包括2度关联）
     */
    @GetMapping("/related")
    public ResponseEntity<Map<String, Object>> getRelatedTags(@RequestParam String tagName) {
        Map<String, Object> response = new HashMap<>();
        try {
            Set<String> relatedTags = tagService.findRelatedTagNames(tagName);
            response.put("success", true);
            response.put("data", relatedTags);
            response.put("tagName", tagName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("获取相关标签失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "获取相关标签失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 初始化标签图（已废弃，改为手动执行）
     * 请使用 backend/init_neo4j_tags.cypher 脚本在 Neo4J Browser 中手动初始化
     */
    @PostMapping("/initialize")
    public ResponseEntity<Map<String, Object>> initializeTags() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "标签图初始化已改为手动执行。请在 Neo4J Browser 中执行 backend/init_neo4j_tags.cypher 脚本");
        return ResponseEntity.ok(response);
    }
}

