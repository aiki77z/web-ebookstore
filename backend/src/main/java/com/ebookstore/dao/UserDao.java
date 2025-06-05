package com.ebookstore.dao;

import com.ebookstore.entity.User;
import com.ebookstore.entity.UserAuth;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问对象接口
 * 体现接口与实现分离的设计原则
 */
public interface UserDao {
    
    /**
     * 根据ID查找用户
     */
    Optional<User> findById(Long id);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户认证查找用户
     */
    Optional<User> findByUserAuth(UserAuth userAuth);
    
    /**
     * 根据姓名查找用户
     */
    Optional<User> findByName(String name);
    
    /**
     * 保存用户
     */
    User save(User user);
    
    /**
     * 删除用户
     */
    void deleteById(Long id);
    
    /**
     * 查找所有用户（管理员功能）
     */
    List<User> findAll();
    
    /**
     * 检查邮箱是否存在
     */
    Boolean existsByEmail(String email);
} 