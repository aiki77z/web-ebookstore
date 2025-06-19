package com.ebookstore.repository;

import com.ebookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByName(String name);
    
    Boolean existsByEmail(String email);//注册时检查邮箱是不是已经被注册
    
    @Query("SELECT u FROM User u JOIN u.orders o WHERE o.orderDate BETWEEN :startDate AND :endDate GROUP BY u ORDER BY SUM(o.totalAmount) DESC")
    List<User> findTopSpendersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 