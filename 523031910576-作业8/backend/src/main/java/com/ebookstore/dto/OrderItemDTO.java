package com.ebookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long bookId;
    private String title;
    private String author;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private String date;
} 