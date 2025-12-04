package com.ebookstore.repository;

import com.ebookstore.entity.Order;
import com.ebookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUserOrderByOrderDateDesc(User user);//根据用户查询订单并按照订单日期降序排列
    
    List<Order> findByUser(User user);
    
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByUserAndOrderDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    // 管理员查看所有订单，按日期降序排列
    List<Order> findAllByOrderByOrderDateDesc();
    
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE o.user = :user AND " +
           "(LOWER(oi.book.title) LIKE LOWER(CONCAT('%', :bookName, '%')) OR :bookName IS NULL) AND " +
           "(o.orderDate BETWEEN :startDate AND :endDate OR (:startDate IS NULL AND :endDate IS NULL))")
    List<Order> findByUserAndBookNameAndDateRange(
            @Param("user") User user,
            @Param("bookName") String bookName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE " +
           "(LOWER(oi.book.title) LIKE LOWER(CONCAT('%', :bookName, '%')) OR :bookName IS NULL) AND " +
           "(o.orderDate BETWEEN :startDate AND :endDate OR (:startDate IS NULL AND :endDate IS NULL)) " +
           "ORDER BY o.orderDate DESC")
    List<Order> findByBookNameAndDateRange(
            @Param("bookName") String bookName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // 根据用户ID和时间范围查询订单
    List<Order> findByUserIdAndOrderDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
} 