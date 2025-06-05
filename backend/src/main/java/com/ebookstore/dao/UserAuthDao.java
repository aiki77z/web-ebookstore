package com.ebookstore.dao;

import com.ebookstore.entity.UserAuth;

import java.util.Optional;

/**
 * 用户认证数据访问对象接口
 * 体现接口与实现分离的设计原则
 */
public interface UserAuthDao {
    
    /**
     * 根据用户名查找认证信息
     */
    Optional<UserAuth> findByUsername(String username);
    
    /**
     * 保存用户认证信息
     */
    UserAuth save(UserAuth userAuth);
    
    /**
     * 根据ID查找认证信息
     */
    Optional<UserAuth> findById(Long id);
    
    /**
     * 检查用户名是否存在
     */
    Boolean existsByUsername(String username);
} 