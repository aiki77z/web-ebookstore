package com.ebookstore.service;

import com.ebookstore.dto.OrderDTO;
import com.ebookstore.dto.OrderItemDTO;


import java.util.List;
import java.util.Map;

public interface OrderService {
    
    List<OrderItemDTO> getOrders();
    
    OrderDTO getOrderById(Long id);
    
    List<OrderItemDTO> createOrder(List<Long> cartItemIds);

    // OrderService.java - 添加新方法
    List<OrderItemDTO> createDirectOrder(List<Map<String, Object>> items);
} 