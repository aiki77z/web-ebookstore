// API基础URL
const API_BASE_URL = 'http://localhost:8080/api';

// 通用请求处理函数
async function request(url, options = {}) {
  try {
    const defaultOptions = {
      credentials: 'include', // 包含cookies
      headers: {
        'Content-Type': 'application/json',
      },
    };

    const response = await fetch(`${API_BASE_URL}${url}`, {
      ...defaultOptions,
      ...options,
    });

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('API请求失败:', error);
    throw error;
  }
}

// 用户相关API
export const userApi = {
  // 登录
  login: (credentials) => request('/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials),
  }),

  // 注册
  register: (userData) => request('/auth/register', {
    method: 'POST',
    body: JSON.stringify(userData),
  }),

  // 登出
  logout: () => request('/auth/logout', {
    method: 'POST',
  }),

  // 获取当前用户信息
  getCurrentUser: () => request('/auth/current-user'),

  // 更新用户信息
  updateProfile: (userData) => request('/auth/profile', {
    method: 'PUT',
    body: JSON.stringify(userData),
  }),
};

// 书籍相关API
export const bookApi = {
  // 获取所有书籍
  getBooks: () => request('/books'),
  
  // 获取单本书籍详情
  getBook: (id) => request(`/books/${id}`),
  
  // 搜索书籍
  searchBooks: (query) => request(`/books/search?query=${encodeURIComponent(query)}`),
};

// 购物车相关API
export const cartApi = {
  // 获取购物车
  getCart: () => request('/cart'),

  // 添加到购物车
  addToCart: (item) => request('/cart/add', {
    method: 'POST',
    body: JSON.stringify(item),
  }),

  // 从购物车移除
  removeFromCart: (cartItemId) => request(`/cart/remove/${cartItemId}`, {
    method: 'DELETE',
  }),

  // 更新购物车商品数量
  updateCartItemQuantity: (cartItemId, quantity) => request(`/cart/update/${cartItemId}`, {
    method: 'PUT',
    body: JSON.stringify({ quantity }),
  }),
};

// 订单相关API
export const orderApi = {
  // 获取订单列表
  getOrders: () => request('/orders'),

  // 获取订单详情
  getOrder: (orderId) => request(`/orders/${orderId}`),

  // 创建订单
  createOrder: (orderData) => request('/orders/create', {
    method: 'POST',
    body: JSON.stringify(orderData),
  }),

  // 取消订单
  cancelOrder: (orderId) => request(`/orders/${orderId}/cancel`, {
    method: 'POST',
  }),
}; 