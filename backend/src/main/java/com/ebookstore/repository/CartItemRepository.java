package com.ebookstore.repository;

import com.ebookstore.entity.CartItem;
import com.ebookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * 根据用户ID查找购物车项
     */
    List<CartItem> findByUserId(Long userId);
    
    /**
     * 根据用户ID和书籍ID查找购物车项
     */
    Optional<CartItem> findByUserIdAndBookId(Long userId, Long bookId);
    
    /**
     * 根据用户对象和书籍ID查找购物车项
     */
    Optional<CartItem> findByUserAndBookId(User user, Long bookId);
    
    /**
     * 删除用户的所有购物车项
     */
    void deleteByUserId(Long userId);
    
    /**
     * 查找用户购物车项（兼容原有方法）
     */
    List<CartItem> findByUser(User user);
    
    /**
     * 查找用户的选中/未选中的购物车项
     */
    List<CartItem> findByUserAndSelected(User user, Boolean selected);
} 