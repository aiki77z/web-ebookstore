package com.ebookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户消费统计DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserConsumptionStatisticsDto {
    private Long userId;
    private String userName;
    private String email;
    private Long totalOrders; // 总订单数
    private Long totalBooks; // 总购书数量
    private BigDecimal totalConsumption; // 总消费金额
    private BigDecimal averageOrderValue; // 平均订单价值
}