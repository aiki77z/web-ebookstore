package com.ebookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 个人统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalStatisticsDto {
    
    private Long totalBooks; // 总购书数量
    private BigDecimal totalAmount; // 总消费金额
    private Long totalOrders; // 总订单数
    private List<BookPurchaseDto> bookDetails; // 每本书的购买详情
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookPurchaseDto {
        private Long bookId;
        private String bookTitle;
        private String author;
        private String cover;
        private Long quantity; // 购买数量
        private BigDecimal totalAmount; // 该书总消费
        private BigDecimal averagePrice; // 平均购买价格
    }
} 