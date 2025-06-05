package com.ebookstore.repository;

import com.ebookstore.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户认证信息Repository
 * 使用Spring JPA进行数据库访问
 */
@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    
    /**
     * 根据用户名查找认证信息
     */
    Optional<UserAuth> findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     */
    Boolean existsByUsername(String username);
} 