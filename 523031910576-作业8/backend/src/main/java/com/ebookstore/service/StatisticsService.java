package com.ebookstore.service;

import com.ebookstore.dto.BookSalesStatisticsDto;
import com.ebookstore.dto.PersonalStatisticsDto;
import com.ebookstore.dto.UserConsumptionStatisticsDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取书籍销量统计（热销榜）
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 书籍销量统计列表，按销量降序排列
     */
    List<BookSalesStatisticsDto> getBookSalesStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取用户消费统计（消费榜）
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 用户消费统计列表，按消费金额降序排列
     */
    List<UserConsumptionStatisticsDto> getUserConsumptionStatistics(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 获取个人购书统计
     * @param userId 用户ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 个人购书统计
     */
    PersonalStatisticsDto getPersonalStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate);
} 