import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Layout, ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { CartProvider } from './contexts/CartContext';
import LayoutComponent from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import Home from './pages/Home';
import BookDetailPage from './pages/BookDetailPage';
import CartPage from './pages/CartPage';
import PersonalPage from './pages/PersonalPage';
import OrderPage from './pages/OrderPage';
import AdminBookPage from './pages/AdminBookPage';

// 受保护的布局组件
const ProtectedLayout = ({ children, requireAdmin = false }) => (
  <ProtectedRoute requireAdmin={requireAdmin}>
    <Layout style={{ minHeight: '100vh' }}>
      <LayoutComponent>
        {children}
      </LayoutComponent>
    </Layout>
  </ProtectedRoute>
);

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <CartProvider>
        <Router>
          <Routes>
            {/* 公开路由 - 登录页面 */}
            <Route path="/login" element={<LoginPage />} />
            
            {/* 受保护的路由 */}
            <Route path="/" element={<ProtectedLayout><Home /></ProtectedLayout>} />
            <Route path="/book/:id" element={<ProtectedLayout><BookDetailPage /></ProtectedLayout>} />
            <Route path="/cart" element={<ProtectedLayout><CartPage /></ProtectedLayout>} />
            <Route path="/personal" element={<ProtectedLayout><PersonalPage /></ProtectedLayout>} />
            <Route path="/orders" element={<ProtectedLayout><OrderPage /></ProtectedLayout>} />
            
            {/* 管理员专用路由 */}
            <Route path="/admin/books" element={<ProtectedLayout requireAdmin={true}><AdminBookPage /></ProtectedLayout>} />
            
            {/* 404页面 */}
            <Route path="*" element={
              <div style={{ 
                padding: 50, 
                textAlign: 'center', 
                minHeight: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: 18
              }}>
                页面未找到 - 404
              </div>
            } />
          </Routes>
        </Router>
      </CartProvider>
    </ConfigProvider>
  );
}

export default App;
