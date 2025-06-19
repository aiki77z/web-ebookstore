package com.ebookstore.controller;

import com.ebookstore.dto.CartItemDTO;
import com.ebookstore.dto.UserInfoDTO;
import com.ebookstore.entity.Book;
import com.ebookstore.entity.CartItem;
import com.ebookstore.entity.User;
import com.ebookstore.service.BookService;
import com.ebookstore.service.CartService;
import com.ebookstore.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CartController {
    
    @Autowired//依赖注入：自动注入CartService实例 业务逻辑交给
    private CartService cartService;
    
    @Autowired//依赖注入：自动注入AuthService实例 业务逻辑交给
    private AuthService authService;
    
    @Autowired//依赖注入：自动注入BookService实例 业务逻辑交给
    private BookService bookService;
    
    /**
     * 获取购物车列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCartItems(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);//获取当前用户
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            List<CartItemDTO> cartItems = cartService.getCartItems();//获取购物车项 函数依赖
            
            response.put("success", true);
            response.put("data", cartItems);
            return ResponseEntity.ok(response);//cartitem实体->cartitemdto->响应json返回给前端

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取购物车失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 添加商品到购物车 book->cartitem->cartitemdto->响应json返回给前端
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestBody Map<String, Object> request, 
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            Long bookId = Long.valueOf(request.get("bookId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            CartItemDTO cartItem = cartService.addToCart(bookId, quantity);//函数依赖
            
            response.put("success", true);
            response.put("message", "添加到购物车成功");
            response.put("data", cartItem);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "添加到购物车失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 从购物车移除商品
     */
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @PathVariable Long itemId, 
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            cartService.removeFromCart(itemId, currentUser.getId());
            
            response.put("success", true);
            response.put("message", "从购物车移除成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "从购物车移除失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update/{itemId}")
    public ResponseEntity<Map<String, Object>> updateCartItemQuantity(
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> request,
            HttpSession session) {
            
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            cartService.updateCartItemQuantity(itemId, quantity, currentUser.getId());//函数依赖 更新数量
            
            response.put("success", true);
            response.put("message", "更新数量成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新数量失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 