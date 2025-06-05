import React, { useState, useEffect } from 'react';
import { Layout, Menu, Avatar, Dropdown, Button, message } from 'antd';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { UserOutlined, LogoutOutlined, SettingOutlined, BookOutlined } from '@ant-design/icons';
import authService from '../services/authService';

const { Sider, Content, Header } = Layout;

export default function LayoutComponent({ children }) {
    const [userInfo, setUserInfo] = useState(null);
    const [selectedKeys, setSelectedKeys] = useState(['1']);
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        // 获取用户信息
        const user = authService.getCurrentUser();
        setUserInfo(user);

        // 根据当前路径设置选中的菜单项
        const path = location.pathname;
        if (path === '/') setSelectedKeys(['1']);
        else if (path === '/cart') setSelectedKeys(['2']);
        else if (path === '/personal') setSelectedKeys(['3']);
        else if (path === '/orders') setSelectedKeys(['4']);
        else if (path === '/admin/books') setSelectedKeys(['5']);
    }, [location.pathname]);

    // 处理登出
    const handleLogout = async () => {
        try {
            await authService.logout();
            message.success('已成功登出');
        } catch (error) {
            console.error('登出失败:', error);
            message.error('登出失败');
        }
    };

    // 用户下拉菜单
    const userMenu = (
        <Menu>
            <Menu.Item key="profile" icon={<UserOutlined />}>
                <Link to="/personal">个人信息</Link>
            </Menu.Item>
            <Menu.Item key="orders" icon={<SettingOutlined />}>
                <Link to="/orders">我的订单</Link>
            </Menu.Item>
            {userInfo && userInfo.role === 'ADMIN' && (
                <Menu.Item key="admin" icon={<BookOutlined />}>
                    <Link to="/admin/books">书籍管理</Link>
                </Menu.Item>
            )}
            <Menu.Divider />
            <Menu.Item key="logout" icon={<LogoutOutlined />} onClick={handleLogout}>
                登出
            </Menu.Item>
        </Menu>
    );

    return (
        <Layout style={{ minHeight: '100vh' }}>
            {/* 顶部导航 */}
            <Header style={{ 
                background: '#fff', 
                padding: '0 24px', 
                borderBottom: '1px solid #f0f0f0',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
            }}>
                <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#1890ff' }}>
                    在线书店
                </div>
                
                <div style={{ display: 'flex', alignItems: 'center' }}>
                    {userInfo ? (
                        <Dropdown overlay={userMenu} placement="bottomRight">
                            <div style={{ cursor: 'pointer', display: 'flex', alignItems: 'center' }}>
                                <Avatar size="small" icon={<UserOutlined />} style={{ marginRight: 8 }} />
                                <span>{userInfo.name || userInfo.username}</span>
                                {userInfo.role === 'ADMIN' && (
                                    <span style={{ 
                                        marginLeft: 8, 
                                        padding: '2px 6px', 
                                        background: '#f50', 
                                        color: 'white', 
                                        fontSize: '12px',
                                        borderRadius: '4px'
                                    }}>
                                        管理员
                                    </span>
                                )}
                            </div>
                        </Dropdown>
                    ) : (
                        <Button type="primary" onClick={() => navigate('/login')}>
                            登录
                        </Button>
                    )}
                </div>
            </Header>

            <Layout>
                {/* 侧边菜单 */}
                <Sider width={200} style={{ background: '#fff' }}>
                    <Menu
                        mode="inline"
                        selectedKeys={selectedKeys}
                        style={{ height: '100%', borderRight: 0 }}
                    >
                        <Menu.Item key="1" icon={<BookOutlined />}>
                            <Link to="/">首页</Link>
                        </Menu.Item>
                        <Menu.Item key="2" icon={<UserOutlined />}>
                            <Link to="/cart">购物车</Link>
                        </Menu.Item>
                        <Menu.Item key="3" icon={<UserOutlined />}>
                            <Link to="/personal">个人信息</Link>
                        </Menu.Item>
                        <Menu.Item key="4" icon={<SettingOutlined />}>
                            <Link to="/orders">我的订单</Link>
                        </Menu.Item>
                        
                        {/* 管理员菜单 */}
                        {userInfo && userInfo.role === 'ADMIN' && (
                            <>
                                <Menu.Divider />
                                <Menu.SubMenu key="admin" icon={<BookOutlined />} title="管理员功能">
                                    <Menu.Item key="5">
                                        <Link to="/admin/books">书籍管理</Link>
                                    </Menu.Item>
                                </Menu.SubMenu>
                            </>
                        )}
                    </Menu>
                </Sider>

                {/* 主内容区 */}
                <Layout style={{ padding: '0 24px 24px' }}>
                    <Content
                        style={{
                            padding: 24,
                            margin: 0,
                            minHeight: 280,
                            background: '#fff',
                        }}
                    >
                        {children}
                    </Content>
                </Layout>
            </Layout>
        </Layout>
    );
}