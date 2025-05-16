package com.ebookstore.controller;

import com.ebookstore.dto.CartItemDTO;
import com.ebookstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCartItems() {
        try {
            List<CartItemDTO> items = cartService.getCartItems();
            return ResponseEntity.ok(items.isEmpty() ? new ArrayList<>() : items);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>()); // 返回空数组而不是错误
        }
    }

    
    @PostMapping("/add")
    public ResponseEntity<CartItemDTO> addToCart(@RequestBody Map<String, Object> payload) {
        Long bookId = Long.parseLong(payload.get("bookId").toString());
        Integer quantity = Integer.parseInt(payload.get("quantity").toString());
        return ResponseEntity.ok(cartService.addToCart(bookId, quantity));
    }
    
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/update")
    public ResponseEntity<CartItemDTO> updateCartItemQuantity(@RequestBody Map<String, Object> payload) {
        Long cartItemId = Long.parseLong(payload.get("cartItemId").toString());
        Integer quantity = Integer.parseInt(payload.get("quantity").toString());
        return ResponseEntity.ok(cartService.updateCartItemQuantity(cartItemId, quantity));
    }
    
    @PutMapping("/toggle/{cartItemId}")
    public ResponseEntity<Void> toggleCartItemSelection(@PathVariable Long cartItemId) {
        cartService.toggleCartItemSelection(cartItemId);
        return ResponseEntity.ok().build();
    }
} 