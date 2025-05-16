package com.ebookstore.service;

import com.ebookstore.dto.CartItemDTO;

import java.util.List;

public interface CartService {
    
    List<CartItemDTO> getCartItems();
    
    CartItemDTO addToCart(Long bookId, Integer quantity);
    
    void removeFromCart(Long cartItemId);
    
    CartItemDTO updateCartItemQuantity(Long cartItemId, Integer quantity);
    
    void toggleCartItemSelection(Long cartItemId);
} 