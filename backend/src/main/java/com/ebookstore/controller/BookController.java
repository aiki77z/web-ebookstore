package com.ebookstore.controller;

import com.ebookstore.dto.BookDTO;
import com.ebookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 书籍控制器
 * 处理书籍相关的公开API请求
 */
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 获取所有书籍
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<BookDTO> books = bookService.getAllBooks();
            response.put("success", true);
            response.put("data", books);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("获取书籍列表失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "获取书籍列表失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据ID获取书籍详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            BookDTO book = bookService.getBookById(id);
            response.put("success", true);
            response.put("data", book);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("获取书籍详情失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "获取书籍详情失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 搜索书籍
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBooks(@RequestParam String query) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<BookDTO> books = bookService.searchBooks(query);
            response.put("success", true);
            response.put("data", books);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("搜索书籍失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "搜索书籍失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 