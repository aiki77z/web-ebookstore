package com.ebookstore.service;

import com.ebookstore.dto.OrderDTO;
import com.ebookstore.dto.OrderItemDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {

    List<OrderItemDTO> getOrders();

    OrderDTO getOrderById(Long id);

    List<OrderItemDTO> createOrder(List<Long> cartItemIds);

    // OrderService.java - 添加新方法
    List<OrderItemDTO> createDirectOrder(List<Map<String, Object>> items);

    // 异步场景：显式指定用户
    List<OrderItemDTO> createOrderForUser(Long userId, List<Long> cartItemIds);
    List<OrderItemDTO> createDirectOrderForUser(Long userId, List<Map<String, Object>> items);

    // 用户订单搜索功能
    List<OrderDTO> searchUserOrders(String bookName, LocalDateTime startDate, LocalDateTime endDate);

    // 管理员查看所有订单
    List<OrderDTO> getAllOrders();

    // 管理员订单搜索功能
    List<OrderDTO> searchAllOrders(String bookName, LocalDateTime startDate, LocalDateTime endDate);
} 