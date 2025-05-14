import React from 'react';
import { Layout, Menu } from 'antd';
import { Link } from 'react-router-dom';
import Header from './Header';

const { Sider, Content } = Layout;

export default function LayoutComponent({ children }) {
    return (
        <Layout>
            <Sider width={200} style={{ background: '#fff' }}>
                <Menu
                    mode="inline"
                    defaultSelectedKeys={['1']}
                    style={{ height: '100%', borderRight: 0 }}
                >
                    <Menu.Item key="1">
                        <Link to="/">首页</Link>
                    </Menu.Item>
                    <Menu.Item key="2">
                        <Link to="/cart">购物车</Link>
                    </Menu.Item>
                    <Menu.Item key="3">
                        <Link to="/personal">个人信息</Link>
                    </Menu.Item>
                    <Menu.Item key="4">
                        <Link to="/orders">我的订单</Link>
                    </Menu.Item>
                </Menu>
            </Sider>
            <Layout style={{ padding: '0 24px 24px' }}>
                <Header />
                <Content
                    style={{
                        padding: 24,//内边距
                        margin: 0,//外边距
                        minHeight: 280,//最小高度
                        background: '#fff',
                    }}
                >
                    {children}
                </Content>
            </Layout>
        </Layout>
    );
}