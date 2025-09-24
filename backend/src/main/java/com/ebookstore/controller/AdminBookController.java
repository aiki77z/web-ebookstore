package com.ebookstore.controller;

import com.ebookstore.entity.Book;
import com.ebookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    
    @Autowired//自动注入BookService实例，用于处理与书籍相关的业务逻辑
    private BookService bookService;
    
    /**
     * 获取所有书籍（分页）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            PageRequest pageRequest = PageRequest.of(page, size);//分页请求对象 page：页码，size：每页大小
            Page<Book> bookPage = bookService.getAllBooksForAdmin(pageRequest);//函数依赖 获取管理员视图的图书
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", bookPage.getContent());//当前页数据
            response.put("total", bookPage.getTotalElements());//总记录数
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", bookPage.getTotalPages());//总页数
            
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
            Book book = bookService.getBookEntityById(id);//获取图书实体
            
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
     * 添加新书籍 请求json->Book实体->数据库保存->响应json
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addBook(@Valid @RequestBody Book book) {
        try {
            Book savedBook = bookService.saveBook(book);//函数依赖 保存图书实体
            
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
     * 删除书籍（软删除）或恢复书籍
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> toggleBookStatus(@PathVariable Long id) {
        try {
            // 先获取书籍当前状态
            Book book = bookService.getBookEntityById(id);
            boolean success;
            String message;
            
            if (book.getDeleted()) {
                // 书籍已删除，执行恢复操作
                success = bookService.restoreBook(id);
                message = success ? "书籍恢复成功" : "恢复失败";
            } else {
                // 书籍未删除，执行删除操作
                success = bookService.softDeleteBook(id);
                message = success ? "书籍删除成功" : "删除失败";
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", message);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败：" + e.getMessage());
            
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
    
    /**
     * 更新书籍库存
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> updateBookStock(
            @PathVariable Long id, 
            @RequestParam Integer stock) {
        
        try {
            boolean success = bookService.updateStock(id, stock);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "库存更新成功");
            } else {
                response.put("success", false);
                response.put("message", "库存更新失败");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新库存失败：" + e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 检查书籍库存
     */
    @GetMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> checkBookStock(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Integer quantity) {
        
        try {
            boolean available = bookService.checkStock(id, quantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("available", available);
            response.put("message", available ? "库存充足" : "库存不足");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "检查库存失败：" + e.getMessage());
            
            return ResponseEntity.ok(response);
        }
    }
} 