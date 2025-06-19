import React, { useState, useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import { Spin } from 'antd';
import authService from '../services/authService';

/**
 * 路由保护组件 控制前端页面访问权限
 */
const ProtectedRoute = ({ children, requireAdmin = false }) => {
    const [loading, setLoading] = useState(true);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [userInfo, setUserInfo] = useState(null);

    useEffect(() => {
        const checkAuth = async () => {
            try {
                // 首先检查本地存储
                if (authService.isAuthenticated()) {
                    const user = authService.getCurrentUser();
                    setUserInfo(user);
                    setIsAuthenticated(true);
                    
                    // 异步验证服务器状态，但不阻塞渲染
                    authService.checkLoginStatus().then(result => {
                        if (!result.success) {
                            // 服务器验证失败，重定向到登录页
                            setIsAuthenticated(false);
                            setUserInfo(null);
                        }
                    }).catch(() => {
                        // 网络错误时保持当前状态
                        console.warn('无法验证服务器登录状态，使用本地缓存');
                    });
                } else {
                    setIsAuthenticated(false);
                    setUserInfo(null);
                }
            } catch (error) {
                console.error('检查认证状态失败:', error);
                setIsAuthenticated(false);
                setUserInfo(null);
            } finally {
                setLoading(false);
            }
        };

        checkAuth();
    }, []);

    // 显示加载状态
    if (loading) {
        return (
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                minHeight: '100vh'
            }}>
                <Spin size="large" tip="正在验证登录状态..." />
            </div>
        );
    }

    // 未登录，重定向到登录页
    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    // 需要管理员权限但用户不是管理员
    if (requireAdmin && userInfo && userInfo.role !== 'ADMIN') {
        return (
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                minHeight: '100vh',
                flexDirection: 'column'
            }}>
                <h2>权限不足</h2>
                <p>您没有访问此页面的权限</p>
            </div>
        );
    }

    // 认证通过，渲染子组件
    return children;
};

export default ProtectedRoute; 