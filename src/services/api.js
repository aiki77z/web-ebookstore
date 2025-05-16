// 后端API基础URL
const API_BASE_URL = 'http://localhost:8080/api';

// 通用请求函数
async function request(url, options = {}, retries = 1) {
  try {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    });
    
    if (!response.ok) {
      const errorText = await response.text();
      let errorMessage = `请求失败：${response.status} ${response.statusText}`;
      try {
        // 尝试解析错误响应为JSON
        const errorJson = JSON.parse(errorText);
        if (errorJson.message) {
          errorMessage = errorJson.message;
        }
      } catch (e) {
        // 如果不是JSON，使用原始错误文本
        if (errorText) {
          errorMessage += ` - ${errorText}`;
        }
      }
      throw new Error(errorMessage);
    }
    
    // 检查响应是否为空
    const text = await response.text();
    if (!text) {
      return []; // 返回空数组而不是JSON.parse空字符串
    }
    
    return JSON.parse(text);
  } catch (error) {
    console.error('API请求错误:', error);
    
    // 实现重试机制
    if (retries > 0 && !url.includes('/orders/create')) {
      console.log(`重试请求 ${url}，剩余重试次数: ${retries - 1}`);
      return request(url, options, retries - 1);
    }
    
    // 抛出一个更通用的错误，让调用者决定如何处理
    throw error;
  }
}

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
  
  // 添加商品到购物车
  addToCart: (bookId, quantity) => request('/cart/add', {
    method: 'POST',
    body: JSON.stringify({ bookId, quantity }),
  }),
  
  // 从购物车移除商品
  removeFromCart: (cartItemId) => request(`/cart/remove/${cartItemId}`, {
    method: 'DELETE',
  }),
  
  // 更新购物车商品数量
  updateCartItemQuantity: (cartItemId, quantity) => request('/cart/update', {
    method: 'PUT',
    body: JSON.stringify({ cartItemId, quantity }),
  }),
};

// 订单相关API
export const orderApi = {
  // 获取所有订单
  getOrders: () => request('/orders'),
  
  // 创建订单
  createOrder: (data) => request('/orders/create', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  
  // 获取订单详情
  getOrderDetails: (orderId) => request(`/orders/${orderId}`),
};

// 用户相关API
export const userApi = {
  // 获取用户信息
  getUserInfo: () => request('/user/info'),
  
  // 更新用户信息
  updateUserInfo: (userData) => request('/user/update', {
    method: 'PUT',
    body: JSON.stringify(userData),
  }),
}; 