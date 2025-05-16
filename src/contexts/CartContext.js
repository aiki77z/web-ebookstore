import React, { createContext, useState, useEffect } from 'react';
import { cartApi, orderApi } from '../services/api';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState([]);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // 初始化加载购物车数据
  useEffect(() => {
    fetchCart();
    fetchOrders();
  }, []);

  // 获取购物车数据
  const fetchCart = async () => {
    try {
      setLoading(true);
      const data = await cartApi.getCart();
      setCartItems(data.map(item => ({ ...item, selected: false })));
      setError(null);
    } catch (err) {
      console.error('获取购物车失败:', err);
      setError('获取购物车数据失败');
      // 为了演示，使用本地数据
      setCartItems([]);
    } finally {
      setLoading(false);
    }
  };

  // 获取订单数据
  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await orderApi.getOrders();
      setOrders(data);
      setError(null);
    } catch (err) {
      console.error('获取订单失败:', err);
      setError('获取订单数据失败');
      // 为了演示，使用本地数据
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  // 添加商品到购物车
  const addToCart = async (book, quantity = 1) => {
    try {
      setLoading(true);
      await cartApi.addToCart(book.id, quantity);
      
      // 更新本地购物车数据
      setCartItems(prevItems => {
        const existingItem = prevItems.find(item => item.id === book.id);
        if (existingItem) {
          return prevItems.map(item =>
            item.id === book.id
              ? { ...item, quantity: item.quantity + quantity }
              : item
          );
        }
        return [...prevItems, { ...book, quantity, selected: false }];
      });
      
      setError(null);
    } catch (err) {
      console.error('添加到购物车失败:', err);
      setError('添加到购物车失败');
      
      // 为了演示，直接修改本地状态
      setCartItems(prevItems => {
        const existingItem = prevItems.find(item => item.id === book.id);
        if (existingItem) {
          return prevItems.map(item =>
            item.id === book.id
              ? { ...item, quantity: item.quantity + quantity }
              : item
          );
        }
        return [...prevItems, { ...book, quantity, selected: false }];
      });
    } finally {
      setLoading(false);
    }
  };

  // 从购物车移除商品
  const removeFromCart = async (id) => {
    try {
      setLoading(true);
      await cartApi.removeFromCart(id);
      setCartItems(prevItems => prevItems.filter(item => item.id !== id));
      setError(null);
    } catch (err) {
      console.error('从购物车移除失败:', err);
      setError('从购物车移除商品失败');
      
      // 为了演示，直接修改本地状态
      setCartItems(prevItems => prevItems.filter(item => item.id !== id));
    } finally {
      setLoading(false);
    }
  };

  // 切换选择状态
  const toggleSelect = (id) => {
    setCartItems(prevItems => 
      prevItems.map(item => 
        item.id === id ? { ...item, selected: !item.selected } : item
      )
    );
  };

  // 创建订单
  const addOrder = async (items) => {
    try {
      setLoading(true);
      //如果从详情页直接购买
      if (items[0].bookId === undefined) {
        const buyNowItems = items.map(item => ({
          id: Date.now() + item.id,
          bookId: item.id,
          title: item.title,
          price: item.price,
          quantity: item.quantity
        }));

        await orderApi.createOrder(buyNowItems);
        await fetchOrders();
        setError(null);
      } else {
        //从购物车里买
        const cartItemIds = items.filter(item => item.selected).map(item => item.id);
        if (cartItemIds.length === 0) {
          throw new Error('请选择要购买的商品');
        }
        //await cartApi.removeFromCart(cartItemIds);
        await orderApi.createOrder({cartItemIds});
        await fetchCart();
        await fetchOrders();
        setError(null);
      }
    }catch (err) {
      console.error('创建订单失败:', err);
      setError('创建订单失败');

      const orderItems = items.map(item => ({
        id: Date.now() + item.id,
        bookId: item.id,
        title: item.title,
        price: item.price,
        quantity: item.quantity,
        date: new Date().toLocaleString()
      }));
      
      setOrders(prev => [...prev, ...orderItems]);
      if(items[0].selected !== undefined){
        setCartItems(prev => prev.filter(item => !item.selected));
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <CartContext.Provider value={{
      cartItems,
      setCartItems,
      orders,
      loading,
      error,
      addToCart,
      removeFromCart,
      toggleSelect,
      addOrder,
      fetchCart,
      fetchOrders
    }}>
      {children}
    </CartContext.Provider>
  );
};
