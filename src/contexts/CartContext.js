import React, { createContext, useState, useCallback, useEffect } from 'react';
import { message } from 'antd';
import { cartApi, orderApi } from '../services/api';
import websocketService from '../services/websocketService';
import { useAuth } from './AuthContext';

export const CartContext = createContext();

export function CartProvider({ children }) {
  const [cart, setCart] = useState([]);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const { user } = useAuth();


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
      const response = await orderApi.createOrderAsync({
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

  // 从购物车结算选中的商品
  const checkoutCart = useCallback(async (selectedItems) => {
    try {
      setLoading(true);

      if (!selectedItems || selectedItems.length === 0) {
        throw new Error('请选择要购买的商品');
      }

      // 使用购物车项ID来创建订单
      const cartItemIds = selectedItems.map(item => item.id);

      const response = await orderApi.createOrderAsync({
        directBuy: false,
        cartItemIds: cartItemIds
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
  }, [fetchCart, fetchOrders]);

  // 初始化时获取购物车和订单数据
  useEffect(() => {
    fetchCart();
    fetchOrders();
  }, [fetchCart, fetchOrders]);

  // WebSocket 连接管理
  useEffect(() => {
    if (user && user.id) {
      console.log('[CartContext] 用户已登录，建立 WebSocket 连接, userId:', user.id);

      // 连接 WebSocket 并设置订单结果回调
      websocketService.connect(user.id, (result) => {
        console.log('[CartContext] 收到订单处理结果:', result);

        if (result.success) {
          message.success(result.message || '订单创建成功！');

          // 刷新订单列表
          fetchOrders();

          // 如果是从购物车结算，刷新购物车
          if (!result.directBuy) {
            fetchCart();
          }
        } else {
          message.error(result.message || '订单创建失败');
        }
      });

      // 组件卸载或用户登出时断开连接
      return () => {
        console.log('[CartContext] 断开 WebSocket 连接');
        websocketService.disconnect();
      };
    }
  }, [user, fetchOrders, fetchCart]);

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
