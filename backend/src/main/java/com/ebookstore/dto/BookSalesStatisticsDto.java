package com.ebookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 书籍销量统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSalesStatisticsDto {
    private Long bookId;
    private String bookTitle;
    private String author;
    private String cover;
    private Long totalSales; // 总销量
    private BigDecimal totalRevenue; // 总销售额
    private BigDecimal averagePrice; // 平均价格
}