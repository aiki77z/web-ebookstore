package com.ebookstore.dao.impl;

import com.ebookstore.dao.UserAuthDao;
import com.ebookstore.entity.UserAuth;
import com.ebookstore.repository.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户认证数据访问对象实现类
 * 通过Spring依赖注入使用Repository
 */
@Repository
public class UserAuthDaoImpl implements UserAuthDao {
    
    @Autowired
    private UserAuthRepository userAuthRepository;
    
    @Override
    public Optional<UserAuth> findByUsername(String username) {
        return userAuthRepository.findByUsername(username);
    }
    
    @Override
    public UserAuth save(UserAuth userAuth) {
        return userAuthRepository.save(userAuth);
    }
    
    @Override
    public Optional<UserAuth> findById(Long id) {
        return userAuthRepository.findById(id);
    }
    
    @Override
    public Boolean existsByUsername(String username) {
        return userAuthRepository.existsByUsername(username);
    }
} 