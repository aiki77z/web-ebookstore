package com.ebookstore.service.impl;

import com.ebookstore.dto.CartItemDTO;
import com.ebookstore.dto.OrderDTO;
import com.ebookstore.dto.OrderItemDTO;
import com.ebookstore.entity.CartItem;
import com.ebookstore.entity.Order;
import com.ebookstore.entity.OrderItem;
import com.ebookstore.entity.User;
import com.ebookstore.entity.Book;
import com.ebookstore.repository.BookRepository;
import com.ebookstore.repository.CartItemRepository;
import com.ebookstore.repository.OrderRepository;
import com.ebookstore.service.OrderService;
import com.ebookstore.service.UserService;
import com.ebookstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserService userService;
    
    @Override
    public List<CartItemDTO> getCartItems() {
        User user = userService.getCurrentUser();
        return cartItemRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public CartItemDTO addToCart(Long bookId, Integer quantity) {
        User user = userService.getCurrentUser();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + bookId + " 的书籍"));
        
        // 查询购物车中是否已存在该商品
        CartItem cartItem = cartItemRepository.findByUserAndBookId(user, bookId)
                .orElse(null);
        
        if (cartItem != null) {
            // 已存在则更新数量
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
    public void removeFromCart(Long cartItemId) {
        User user = userService.getCurrentUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + cartItemId + " 的购物车项目"));
        
        // 安全检查：确保只能操作自己的购物车
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new SecurityException("无权操作此购物车项目");
        }
        
        cartItemRepository.delete(cartItem);
    }
    
    @Override
    public CartItemDTO updateCartItemQuantity(Long cartItemId, Integer quantity) {
        User user = userService.getCurrentUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + cartItemId + " 的购物车项目"));
        
        // 安全检查
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new SecurityException("无权操作此购物车项目");
        }
        
        cartItem.setQuantity(quantity);
        cartItem = cartItemRepository.save(cartItem);
        
        return convertToDTO(cartItem);
    }
    
    @Override
    public void toggleCartItemSelection(Long cartItemId) {
        User user = userService.getCurrentUser();
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + cartItemId + " 的购物车项目"));
        
        // 安全检查
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new SecurityException("无权操作此购物车项目");
        }
        
        cartItem.setSelected(!cartItem.getSelected());
        cartItemRepository.save(cartItem);
    }
    
    private CartItemDTO convertToDTO(CartItem cartItem) {
        Book book = cartItem.getBook();
        return new CartItemDTO(
                cartItem.getId(),
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getCover(),
                cartItem.getQuantity(),
                cartItem.getSelected()
        );
    }
} 