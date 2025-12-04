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
    private BookInfo book;
    private Integer quantity;
    private Boolean selected;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookInfo {
        private Long id;
        private String title;
        private String author;
        private BigDecimal price;
        private String cover;
        private String status;
        private Integer stock;
        private String isbn;
    }
    
    // 保持向后兼容的构造函数
    public CartItemDTO(Long id, Long bookId, String title, String author, BigDecimal price, String cover, Integer quantity, Boolean selected) {
        this.id = id;
        this.book = new BookInfo(bookId, title, author, price, cover, null, null, null);
        this.quantity = quantity;
        this.selected = selected;
    }
    
    // 包含完整书籍信息的构造函数
    public CartItemDTO(Long id, Long bookId, String title, String author, BigDecimal price, String cover, String status, Integer stock, String isbn, Integer quantity, Boolean selected) {
        this.id = id;
        this.book = new BookInfo(bookId, title, author, price, cover, status, stock, isbn);
        this.quantity = quantity;
        this.selected = selected;
    }
} 