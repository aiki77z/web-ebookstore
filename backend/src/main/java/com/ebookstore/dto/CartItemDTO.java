package com.ebookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long bookId;
    private String title;
    private String author;
    private BigDecimal price;
    private String cover;
    private Integer quantity;
    private Boolean selected;
} 