import React, { createContext, useState } from 'react';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState([]);
  const [orders, setOrders] = useState([]); // 新增订单状态

  const addToCart = (book, quantity = 1) => {
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
  };

  // 删除
  const removeFromCart = (id) => {
    setCartItems(prevItems => prevItems.filter(item => item.id !== id));
  };

  // 添加切换选择状态的方法
  const toggleSelect = (id) => {
    setCartItems(prevItems => 
      prevItems.map(item => 
        item.id === id ? { ...item, selected: !item.selected } : item
      )
    );
  };

  // 结算订单
  const addOrder = (items) => {
    const orderItems = items.map(item => ({
      id: Date.now() + item.id,
      bookId: item.id,
      title: item.title,
      price: item.price,
      quantity: item.quantity,
      date: new Date().toLocaleString()
    }));
    
    setOrders(prev => [...prev, ...orderItems]);
    
    // 清空已购买的商品
    setCartItems(prev => prev.filter(item => !item.selected));
  };

  return (
    <CartContext.Provider value={{
      cartItems,
      setCartItems,
      orders,
      addToCart,
      removeFromCart,
      toggleSelect,
      addOrder
    }}>
      {children}
    </CartContext.Provider>
  );
};
