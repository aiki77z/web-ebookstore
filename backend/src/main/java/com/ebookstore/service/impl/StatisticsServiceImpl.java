package com.ebookstore.service.impl;

import com.ebookstore.dto.BookSalesStatisticsDto;
import com.ebookstore.dto.PersonalStatisticsDto;
import com.ebookstore.dto.UserConsumptionStatisticsDto;
import com.ebookstore.entity.Order;
import com.ebookstore.entity.OrderItem;
import com.ebookstore.repository.OrderRepository;
import com.ebookstore.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public List<BookSalesStatisticsDto> getBookSalesStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        System.out.println("=== StatisticsService.getBookSalesStatistics ===");
        System.out.println("查询时间范围: " + startDate + " 到 " + endDate);
        
        // 获取指定时间范围内的所有订单
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        System.out.println("查询到的订单数量: " + (orders != null ? orders.size() : 0));
        
        if (orders != null && !orders.isEmpty()) {
            System.out.println("订单详情:");
            for (int i = 0; i < Math.min(orders.size(), 3); i++) {
                Order order = orders.get(i);
                System.out.println("  订单" + (i+1) + ": ID=" + order.getId() + 
                                 ", 用户=" + order.getUser().getName() + 
                                 ", 日期=" + order.getOrderDate() + 
                                 ", 金额=" + order.getTotalAmount() +
                                 ", 商品数=" + (order.getOrderItems() != null ? order.getOrderItems().size() : 0));
            }
        }
        
        // 统计每本书的销量和销售额
        Map<Long, BookSalesStatisticsDto> bookStatisticsMap = new HashMap<>();
        
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                Long bookId = item.getBook().getId();
                
                BookSalesStatisticsDto dto = bookStatisticsMap.computeIfAbsent(bookId, k -> {
                    BookSalesStatisticsDto newDto = new BookSalesStatisticsDto();
                    newDto.setBookId(bookId);
                    newDto.setBookTitle(item.getBook().getTitle());
                    newDto.setAuthor(item.getBook().getAuthor());
                    newDto.setCover(item.getBook().getCover());
                    newDto.setTotalSales(0L);
                    newDto.setTotalRevenue(BigDecimal.ZERO);
                    return newDto;
                });
                
                // 累计销量和销售额
                dto.setTotalSales(dto.getTotalSales() + item.getQuantity());
                dto.setTotalRevenue(dto.getTotalRevenue().add(item.getSubtotal()));
            }
        }
        
        // 计算平均价格并排序
        List<BookSalesStatisticsDto> result = bookStatisticsMap.values().stream()
                .peek(dto -> {
                    if (dto.getTotalSales() > 0) {
                        dto.setAveragePrice(dto.getTotalRevenue().divide(
                                BigDecimal.valueOf(dto.getTotalSales()), 2, RoundingMode.HALF_UP));
                    } else {
                        dto.setAveragePrice(BigDecimal.ZERO);
                    }
                })
                .sorted((a, b) -> b.getTotalSales().compareTo(a.getTotalSales())) // 按销量降序
                .collect(Collectors.toList());
        
        return result;
    }
    
    @Override
    public List<UserConsumptionStatisticsDto> getUserConsumptionStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        // 获取指定时间范围内的所有订单
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        
        // 统计每个用户的消费情况
        Map<Long, UserConsumptionStatisticsDto> userStatisticsMap = new HashMap<>();
        
        for (Order order : orders) {
            Long userId = order.getUser().getId();
            
            UserConsumptionStatisticsDto dto = userStatisticsMap.computeIfAbsent(userId, k -> {
                UserConsumptionStatisticsDto newDto = new UserConsumptionStatisticsDto();
                newDto.setUserId(userId);
                newDto.setUserName(order.getUser().getName());
                newDto.setEmail(order.getUser().getEmail());
                newDto.setTotalOrders(0L);
                newDto.setTotalBooks(0L);
                newDto.setTotalConsumption(BigDecimal.ZERO);
                return newDto;
            });
            
            // 累计订单数和消费金额
            dto.setTotalOrders(dto.getTotalOrders() + 1);
            dto.setTotalConsumption(dto.getTotalConsumption().add(order.getTotalAmount()));
            
            // 累计购书数量
            long orderBookCount = order.getOrderItems().stream()
                    .mapToLong(OrderItem::getQuantity)
                    .sum();
            dto.setTotalBooks(dto.getTotalBooks() + orderBookCount);
        }
        
        // 计算平均订单价值并排序
        List<UserConsumptionStatisticsDto> result = userStatisticsMap.values().stream()
                .peek(dto -> {
                    if (dto.getTotalOrders() > 0) {
                        dto.setAverageOrderValue(dto.getTotalConsumption().divide(
                                BigDecimal.valueOf(dto.getTotalOrders()), 2, RoundingMode.HALF_UP));
                    } else {
                        dto.setAverageOrderValue(BigDecimal.ZERO);
                    }
                })
                .sorted((a, b) -> b.getTotalConsumption().compareTo(a.getTotalConsumption())) // 按消费金额降序
                .collect(Collectors.toList());
        
        return result;
    }
    
    @Override
    public PersonalStatisticsDto getPersonalStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        // 获取指定用户在指定时间范围内的所有订单
        List<Order> orders = orderRepository.findByUserIdAndOrderDateBetween(userId, startDate, endDate);
        
        PersonalStatisticsDto result = new PersonalStatisticsDto();
        result.setTotalOrders((long) orders.size());
        result.setTotalBooks(0L);
        result.setTotalAmount(BigDecimal.ZERO);
        
        // 统计每本书的购买情况
        Map<Long, PersonalStatisticsDto.BookPurchaseDto> bookPurchaseMap = new HashMap<>();
        
        for (Order order : orders) {
            result.setTotalAmount(result.getTotalAmount().add(order.getTotalAmount()));
            
            for (OrderItem item : order.getOrderItems()) {
                Long bookId = item.getBook().getId();
                
                PersonalStatisticsDto.BookPurchaseDto bookDto = bookPurchaseMap.computeIfAbsent(bookId, k -> {
                    PersonalStatisticsDto.BookPurchaseDto newDto = new PersonalStatisticsDto.BookPurchaseDto();
                    newDto.setBookId(bookId);
                    newDto.setBookTitle(item.getBook().getTitle());
                    newDto.setAuthor(item.getBook().getAuthor());
                    newDto.setCover(item.getBook().getCover());
                    newDto.setQuantity(0L);
                    newDto.setTotalAmount(BigDecimal.ZERO);
                    return newDto;
                });
                
                // 累计该书的购买数量和金额
                bookDto.setQuantity(bookDto.getQuantity() + item.getQuantity());
                bookDto.setTotalAmount(bookDto.getTotalAmount().add(item.getSubtotal()));
                
                // 累计总购书数量
                result.setTotalBooks(result.getTotalBooks() + item.getQuantity());
            }
        }
        
        // 计算每本书的平均购买价格
        List<PersonalStatisticsDto.BookPurchaseDto> bookDetails = bookPurchaseMap.values().stream()
                .peek(dto -> {
                    if (dto.getQuantity() > 0) {
                        dto.setAveragePrice(dto.getTotalAmount().divide(
                                BigDecimal.valueOf(dto.getQuantity()), 2, RoundingMode.HALF_UP));
                    } else {
                        dto.setAveragePrice(BigDecimal.ZERO);
                    }
                })
                .sorted((a, b) -> b.getQuantity().compareTo(a.getQuantity())) // 按购买数量降序
                .collect(Collectors.toList());
        
        result.setBookDetails(bookDetails);
        
        return result;
    }
} 