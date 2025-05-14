import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Layout } from 'antd';
import { CartProvider } from './contexts/CartContext';
import LayoutComponent from './components/Layout';
import Home from './pages/Home';
import BookDetailPage from './pages/BookDetailPage';
import CartPage from './pages/CartPage';
import PersonalPage from './pages/PersonalPage';
import OrderPage from './pages/OrderPage';

function App() {
  return (
      <CartProvider>
        <Router>
          <Layout style={{ minHeight: '100vh' }}>
            <LayoutComponent>
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/book/:id" element={<BookDetailPage />} />
                <Route path="/cart" element={<CartPage />} />
                <Route path="/personal" element={<PersonalPage />} />
                <Route path="/orders" element={<OrderPage />} />
              </Routes>
            </LayoutComponent>
          </Layout>
        </Router>
      </CartProvider>
  );
}

export default App;
