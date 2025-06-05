import React, { createContext, useState, useCallback, useEffect } from 'react';
import { message } from 'antd';
import { cartApi, orderApi } from '../services/api';

export const CartContext = createContext();

export function CartProvider({ children }) {
  const [cart, setCart] = useState([]);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);

  // 获取购物车数据
  const fetchCart = useCallback(async () => {
    try {
      setLoading(true);
      const response = await cartApi.getCart();
      
      if (response && response.success) {
        setCart(response.data || []);
      } else {
        throw new Error(response.message || '获取购物车失败');
      }
    } catch (err) {
      console.error('获取购物车失败:', err);
      message.error(err.message || '获取购物车失败，请稍后再试');
    } finally {
      setLoading(false);
    }
  }, []);

  // 获取订单数据
  const fetchOrders = useCallback(async () => {
    try {
      setLoading(true);
      const response = await orderApi.getOrders();
      
      if (response && response.success) {
        setOrders(response.data || []);
      } else {
        throw new Error(response.message || '获取订单列表失败');
      }
    } catch (err) {
      console.error('获取订单列表失败:', err);
      message.error(err.message || '获取订单列表失败，请稍后再试');
    } finally {
      setLoading(false);
    }
  }, []);

  // 添加到购物车
  const addToCart = useCallback(async (book, quantity = 1) => {
    try {
      setLoading(true);
      const response = await cartApi.addToCart({
        bookId: book.id,
        quantity: quantity
      });
      
      if (response && response.success) {
        await fetchCart(); // 重新获取购物车数据
        return response.data;
      } else {
        throw new Error(response.message || '添加到购物车失败');
      }
    } catch (err) {
      console.error('添加到购物车失败:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [fetchCart]);

  // 从购物车移除
  const removeFromCart = useCallback(async (cartItemId) => {
    try {
      setLoading(true);
      const response = await cartApi.removeFromCart(cartItemId);
      
      if (response && response.success) {
        await fetchCart(); // 重新获取购物车数据
        return response.data;
      } else {
        throw new Error(response.message || '从购物车移除失败');
      }
    } catch (err) {
      console.error('从购物车移除失败:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [fetchCart]);

  // 更新购物车商品数量
  const updateCartItemQuantity = useCallback(async (cartItemId, quantity) => {
    try {
      setLoading(true);
      const response = await cartApi.updateCartItemQuantity(cartItemId, quantity);
      
      if (response && response.success) {
        await fetchCart(); // 重新获取购物车数据
        return response.data;
      } else {
        throw new Error(response.message || '更新购物车数量失败');
      }
    } catch (err) {
      console.error('更新购物车数量失败:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [fetchCart]);

  // 直接购买
  const directPurchase = useCallback(async (book, quantity = 1) => {
    try {
      setLoading(true);
      const response = await orderApi.createOrder({
        directBuy: true,
        items: [{
          bookId: book.id,
          quantity: quantity
        }]
      });
      
      if (response && response.success) {
        await fetchOrders(); // 重新获取订单列表
        return response.data;
      } else {
        throw new Error(response.message || '购买失败');
      }
    } catch (err) {
      console.error('购买失败:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [fetchOrders]);

  // 从购物车结算
  const checkoutCart = useCallback(async () => {
    try {
      setLoading(true);
      const response = await orderApi.createOrder({
        directBuy: false,
        items: cart.map(item => ({
          bookId: item.book.id,
          quantity: item.quantity
        }))
      });
      
      if (response && response.success) {
        await Promise.all([
          fetchCart(),   // 重新获取购物车数据
          fetchOrders()  // 重新获取订单列表
        ]);
        return response.data;
      } else {
        throw new Error(response.message || '结算失败');
      }
    } catch (err) {
      console.error('结算失败:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [cart, fetchCart, fetchOrders]);

  // 初始化时获取购物车和订单数据
  useEffect(() => {
    fetchCart();
    fetchOrders();
  }, [fetchCart, fetchOrders]);

  const value = {
    cart,
    orders,
    loading,
    fetchCart,
    fetchOrders,
    addToCart,
    removeFromCart,
    updateCartItemQuantity,
    directPurchase,
    checkoutCart
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
}
