package com.ebookstore.service;

import com.ebookstore.dto.CartItemDTO;
import com.ebookstore.entity.CartItem;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    /**
     * 获取当前用户的购物车列表
     */
    List<CartItemDTO> getCartItems();
    
    /**
     * 添加商品到当前用户购物车
     */
    CartItemDTO addToCart(Long bookId, Integer quantity);
    
    /**
     * 从购物车移除商品
     */
    void removeFromCart(Long itemId, Long userId);
    
    /**
     * 更新购物车商品数量
     */
    void updateCartItemQuantity(Long itemId, Integer quantity, Long userId);
    
    /**
     * 切换购物车项的选中状态
     */
    void toggleCartItemSelection(Long cartItemId);
    
    /**
     * 根据书籍ID清理所有用户购物车中的该书籍
     * 用于书籍软删除时自动清理
     */
    int cleanCartByBookId(Long bookId);
} 