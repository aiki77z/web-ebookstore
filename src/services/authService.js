/**
 * 认证服务
 * 处理用户登录、注册、登出等功能
 */

const API_BASE_URL = 'http://localhost:8080/api';

class AuthService {
    
    // 用户登录
    async login(loginData) {
        console.log('开始登录请求，数据:', loginData);
        
        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include', // 包含Cookie
                body: JSON.stringify(loginData)
            });
            
            console.log('收到响应，状态码:', response.status);
            console.log('响应头:', response.headers);
            
            if (!response.ok) {
                console.error('响应状态不正常:', response.status);
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const result = await response.json();
            console.log('解析响应结果:', result);
            
            if (result.success) {
                console.log('登录成功，用户信息:', result.userInfo);
                // 登录成功，存储用户信息到localStorage
                localStorage.setItem('userInfo', JSON.stringify(result.userInfo));
                localStorage.setItem('isLoggedIn', 'true');
                return { success: true, userInfo: result.userInfo };
            } else {
                console.log('登录失败，错误信息:', result.message);
                return { success: false, message: result.message };
            }
        } catch (error) {
            console.error('登录请求异常:', error);
            return { success: false, message: '网络错误，请稍后重试' };
        }
    }
    
    // 用户注册
    async register(registerData) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(registerData)
            });
            
            const result = await response.json();
            
            if (result.success) {
                // 注册成功，存储用户信息
                localStorage.setItem('userInfo', JSON.stringify(result.userInfo));
                localStorage.setItem('isLoggedIn', 'true');
                return { success: true, userInfo: result.userInfo };
            } else {
                return { success: false, message: result.message };
            }
        } catch (error) {
            console.error('注册请求失败:', error);
            return { success: false, message: '网络错误，请稍后重试' };
        }
    }
    
    // 用户登出
    async logout() {
        try {
            await fetch(`${API_BASE_URL}/auth/logout`, {
                method: 'POST',
                credentials: 'include'
            });
        } catch (error) {
            console.error('登出请求失败:', error);
        } finally {
            // 清除本地存储
            localStorage.removeItem('userInfo');
            localStorage.removeItem('isLoggedIn');
            window.location.href = '/login';
        }
    }
    
    // 检查登录状态
    async checkLoginStatus() {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/status`, {
                method: 'GET',
                credentials: 'include'
            });
            
            const result = await response.json();
            
            if (result.isLoggedIn) {
                // 更新本地存储
                localStorage.setItem('userInfo', JSON.stringify(result.userInfo));
                localStorage.setItem('isLoggedIn', 'true');
                return { success: true, userInfo: result.userInfo };
            } else {
                // 清除本地存储
                localStorage.removeItem('userInfo');
                localStorage.removeItem('isLoggedIn');
                return { success: false };
            }
        } catch (error) {
            console.error('检查登录状态失败:', error);
            // 网络错误时，检查本地存储
            const isLoggedIn = localStorage.getItem('isLoggedIn');
            const userInfo = localStorage.getItem('userInfo');
            
            if (isLoggedIn && userInfo) {
                return { success: true, userInfo: JSON.parse(userInfo) };
            } else {
                return { success: false };
            }
        }
    }
    
    // 获取当前用户信息（从本地存储）
    getCurrentUser() {
        const userInfo = localStorage.getItem('userInfo');
        const isLoggedIn = localStorage.getItem('isLoggedIn');
        
        if (isLoggedIn && userInfo) {
            return JSON.parse(userInfo);
        }
        return null;
    }
    
    // 设置当前用户信息（更新本地存储）
    setCurrentUser(userInfo) {
        if (userInfo) {
            localStorage.setItem('userInfo', JSON.stringify(userInfo));
            localStorage.setItem('isLoggedIn', 'true');
        } else {
            localStorage.removeItem('userInfo');
            localStorage.removeItem('isLoggedIn');
        }
    }
    
    // 检查是否已登录（从本地存储）
    isAuthenticated() {
        return localStorage.getItem('isLoggedIn') === 'true';
    }
    
    // 检查是否为管理员
    isAdmin() {
        const userInfo = this.getCurrentUser();
        return userInfo && userInfo.role === 'ADMIN';
    }
}

export default new AuthService(); 