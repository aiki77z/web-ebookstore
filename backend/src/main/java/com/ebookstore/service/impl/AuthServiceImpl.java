package com.ebookstore.service.impl;

import com.ebookstore.dao.UserAuthDao;
import com.ebookstore.dao.UserDao;
import com.ebookstore.dto.*;
import com.ebookstore.entity.User;
import com.ebookstore.entity.UserAuth;
import com.ebookstore.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 认证服务实现类
 * 使用BCrypt确保密码安全，通过Spring依赖注入
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserAuthDao userAuthDao;
    
    @Autowired
    private UserDao userDao;
    
    // BCrypt密码编码器，确保密码安全
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // Session中存储用户信息的key
    private static final String SESSION_USER_KEY = "currentUser";
    
    @Override
    @Transactional
    public LoginResponseDTO login(LoginDTO loginDTO, HttpSession session) {
        System.out.println("开始登录处理，用户名: " + loginDTO.getUsername());
        
        try {
            // 根据用户名查找认证信息
            Optional<UserAuth> userAuthOpt = userAuthDao.findByUsername(loginDTO.getUsername());
            
            if (!userAuthOpt.isPresent()) {
                System.out.println("用户不存在: " + loginDTO.getUsername());
                return LoginResponseDTO.failure("用户名或密码错误");
            }
            
            UserAuth userAuth = userAuthOpt.get();
            System.out.println("找到用户: " + userAuth.getUsername() + ", 角色: " + userAuth.getRole());
            
            // 验证密码（使用BCrypt）
            System.out.println("验证密码，输入密码: " + loginDTO.getPassword());
            System.out.println("数据库密码哈希: " + userAuth.getPasswordHash());
            
            boolean passwordMatches = validatePassword(loginDTO.getPassword(), userAuth.getPasswordHash());
            System.out.println("密码验证结果: " + passwordMatches);
            
            if (!passwordMatches) {
                System.out.println("密码错误");
                return LoginResponseDTO.failure("用户名或密码错误");
            }
            
            // 检查账户是否激活
            if (!userAuth.getActive()) {
                System.out.println("账户未激活");
                return LoginResponseDTO.failure("账户已被禁用");
            }
            
            // 更新最后登录时间
            userAuth.setLastLogin(LocalDateTime.now());
            userAuthDao.save(userAuth);
            System.out.println("更新登录时间完成");
            
            // 构建用户信息DTO
            UserInfoDTO userInfo = buildUserInfoDTO(userAuth);
            System.out.println("构建用户信息完成: " + userInfo.getUsername());
            
            // 将用户信息存储到Session中
            session.setAttribute(SESSION_USER_KEY, userInfo);
            System.out.println("Session存储完成");
            
            LoginResponseDTO response = LoginResponseDTO.success(userInfo);
            System.out.println("登录成功，返回响应");
            return response;
            
        } catch (Exception e) {
            System.out.println("登录过程中出现异常: " + e.getMessage());
            e.printStackTrace(); // 添加详细日志
            return LoginResponseDTO.failure("登录失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public LoginResponseDTO register(RegisterDTO registerDTO) {
        try {
            // 检查用户名是否已存在
            if (userAuthDao.existsByUsername(registerDTO.getUsername())) {
                return LoginResponseDTO.failure("用户名已存在");
            }
            
            // 检查邮箱是否已存在
            if (userDao.existsByEmail(registerDTO.getEmail())) {
                return LoginResponseDTO.failure("邮箱已被注册");
            }
            
            // 创建用户认证信息
            UserAuth userAuth = new UserAuth();
            userAuth.setUsername(registerDTO.getUsername());
            userAuth.setPasswordHash(encodePassword(registerDTO.getPassword()));
            userAuth.setRole("USER");
            userAuth.setActive(true);
            
            // 保存认证信息
            userAuth = userAuthDao.save(userAuth);
            
            // 创建用户信息
            User user = new User();
            user.setName(registerDTO.getName());
            user.setEmail(registerDTO.getEmail());
            user.setAddress(registerDTO.getAddress());
            user.setPhone(registerDTO.getPhone());
            user.setUserAuth(userAuth);
            
            // 保存用户信息
            user = userDao.save(user);
            
            // 构建用户信息DTO
            UserInfoDTO userInfo = new UserInfoDTO();
            userInfo.setId(user.getId());
            userInfo.setName(user.getName());
            userInfo.setEmail(user.getEmail());
            userInfo.setAddress(user.getAddress());
            userInfo.setPhone(user.getPhone());
            userInfo.setUsername(userAuth.getUsername());
            userInfo.setRole(userAuth.getRole());
            
            return LoginResponseDTO.success(userInfo);
            
        } catch (Exception e) {
            e.printStackTrace(); // 添加详细日志
            return LoginResponseDTO.failure("注册失败：" + e.getMessage());
        }
    }
    
    @Override
    public void logout(HttpSession session) {
        session.removeAttribute(SESSION_USER_KEY);
        session.invalidate();
    }
    
    @Override
    public UserInfoDTO getCurrentUser(HttpSession session) {
        return (UserInfoDTO) session.getAttribute(SESSION_USER_KEY);
    }
    
    @Override
    public Boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * 构建用户信息DTO
     */
    private UserInfoDTO buildUserInfoDTO(UserAuth userAuth) {
        System.out.println("开始构建用户信息DTO");
        
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setUsername(userAuth.getUsername());
        userInfo.setRole(userAuth.getRole());
        
        // 从数据库加载用户信息
        Optional<User> userOpt = userDao.findByUserAuth(userAuth);
        System.out.println("查询用户信息结果: " + userOpt.isPresent());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userInfo.setId(user.getId());
            userInfo.setName(user.getName());
            userInfo.setEmail(user.getEmail());
            userInfo.setAddress(user.getAddress());
            userInfo.setPhone(user.getPhone());
            System.out.println("用户信息填充完成: " + user.getName());
        } else {
            System.out.println("未找到对应的用户信息");
        }
        
        return userInfo;
    }
} 