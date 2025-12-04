package com.ebookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private UserInfoDTO user;
    private List<OrderItemDTO> orderItems;
    
    // 构造函数用于简单订单信息（用户查看自己的订单时）
    public OrderDTO(Long id, LocalDateTime orderDate, BigDecimal totalAmount, String status, List<OrderItemDTO> orderItems) {
        this.id = id;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderItems = orderItems;
    }
} 