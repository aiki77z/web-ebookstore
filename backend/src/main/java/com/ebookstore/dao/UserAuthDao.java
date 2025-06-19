package com.ebookstore.dao;

import com.ebookstore.entity.UserAuth;

import java.util.Optional;

/**
 * 用户认证数据访问对象接口
 * 体现接口与实现分离的设计原则
 * DAO负责数据持久化操作 操作实体对象 与数据库表结构相对应
 * DTO负责数据传输和展示 操作视图对象 与数据库表结构无关 展示所需数据 可以组合/屏蔽
 * Repository无需实现 通过方法名自动生成查询 直接与实体类对应 不用编写实现类
 * DAO提供更复杂的数据访问操作 可以组合多个Repository的操作 包含特定业务逻辑 可以使用EntityManager进行复杂查询
 *
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