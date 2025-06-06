package com.ebookstore.controller;

import com.ebookstore.entity.Book;
import com.ebookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员书籍管理控制器
 * 实现管理员对书籍的增删改查功能
 */
@RestController
@RequestMapping("/api/admin/books")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AdminBookController {
    
    @Autowired
    private BookService bookService;
    
    /**
     * 获取所有书籍（分页）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            List<Book> books = bookService.getAllBooksForAdmin();
            
            // 简单分页实现
            int start = page * size;
            int end = Math.min(start + size, books.size());
            
            List<Book> pagedBooks = books.subList(start, end);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pagedBooks);
            response.put("total", books.size());
            response.put("page", page);
            response.put("size", size);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
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
        try {
            Book book = bookService.getBookEntityById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", book);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取书籍详情失败：" + e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 添加新书籍
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addBook(@Valid @RequestBody Book book) {
        try {
            Book savedBook = bookService.saveBook(book);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "书籍添加成功");
            response.put("data", savedBook);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "添加书籍失败：" + e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 更新书籍信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBook(
            @PathVariable Long id, 
            @Valid @RequestBody Book book) {
        
        try {
            book.setId(id); // 确保ID正确
            Book updatedBook = bookService.saveBook(book);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "书籍更新成功");
            response.put("data", updatedBook);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新书籍失败：" + e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 删除书籍
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "书籍删除成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除书籍失败：" + e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 搜索书籍
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBooks(@RequestParam String keyword) {
        try {
            List<Book> books = bookService.searchBooksForAdmin(keyword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", books);
            response.put("total", books.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "搜索书籍失败：" + e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
} 