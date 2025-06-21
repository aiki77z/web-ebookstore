package com.ebookstore.service.impl;

import com.ebookstore.dto.CartItemDTO;
import com.ebookstore.entity.CartItem;
import com.ebookstore.entity.User;
import com.ebookstore.entity.Book;
import com.ebookstore.repository.BookRepository;
import com.ebookstore.repository.CartItemRepository;
import com.ebookstore.service.CartService;
import com.ebookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements CartService {//多个repository协同操作数据库
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserService userService;
    
    @Override
    public List<CartItemDTO> getCartItems() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new SecurityException("用户未登录");
        }
        return cartItemRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CartItemDTO addToCart(Long bookId, Integer quantity) {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new SecurityException("用户未登录");
        }
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("书籍不存在"));
        
        // 查询购物车中是否已存在该商品
        Optional<CartItem> existingItem = cartItemRepository.findByUserAndBookId(user, bookId);
        
        CartItem cartItem;
        if (existingItem.isPresent()) {
            // 已存在则更新数量
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // 不存在则新增
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setBook(book);
            cartItem.setQuantity(quantity);
            cartItem.setSelected(false);
        }
        
        cartItem = cartItemRepository.save(cartItem);
        return convertToDTO(cartItem);
    }
    
    @Override
    @Transactional
    public void removeFromCart(Long itemId, Long userId) {
        User user = userService.getCurrentUser();
        if (user == null || !user.getId().equals(userId)) {
            throw new SecurityException("无权限操作");
        }
        
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("购物车项不存在"));
        
        // 验证该购物车项属于当前用户
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new SecurityException("无权限操作该购物车项");
        }
        
        cartItemRepository.delete(cartItem);
    }
    
    @Override
    @Transactional
    public void updateCartItemQuantity(Long itemId, Integer quantity, Long userId) {
        User user = userService.getCurrentUser();
        if (user == null || !user.getId().equals(userId)) {
            throw new SecurityException("无权限操作");
        }
        
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("购物车项不存在"));
        
        // 验证该购物车项属于当前用户
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new SecurityException("无权限操作该购物车项");
        }
        
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }
    
    @Override
    @Transactional
    public void toggleCartItemSelection(Long cartItemId) {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new SecurityException("用户未登录");
        }
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("购物车项不存在"));
        
        // 验证该购物车项属于当前用户
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new SecurityException("无权限操作该购物车项");
        }
        
        cartItem.setSelected(!cartItem.getSelected());
        cartItemRepository.save(cartItem);
    }
    
    private CartItemDTO convertToDTO(CartItem cartItem) {
        // 重新从数据库获取最新的书籍信息，确保库存数据是最新的
        Book book = bookRepository.findById(cartItem.getBook().getId())
                .orElse(cartItem.getBook());
                
        return new CartItemDTO(
                cartItem.getId(),
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getCover(),
                book.getStatus(),
                book.getStock(),
                book.getIsbn(),
                cartItem.getQuantity(),
                cartItem.getSelected()
        );
    }
} 