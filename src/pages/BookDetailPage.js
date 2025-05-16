import React, { useState, useContext, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { CartContext } from '../contexts/CartContext';
import {Row, Col, Button, InputNumber, Descriptions, Modal, Spin, Alert, message} from 'antd';
import {bookApi, orderApi} from '../services/api';

export default function BookDetailPage() {
  // 使用自定义Hook获取路由参数
  const { id } = useParams();
  const navigate = useNavigate();
  
  // 使用Context消费全局状态
  const { addToCart, addOrder,fetchOrders } = useContext(CartContext);
  
  // 状态管理组件内部数据
  const [book, setBook] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // 加载书籍详情
  useEffect(() => {
    fetchBookDetail();
  }, [id]);

  // 从API获取书籍详情
  const fetchBookDetail = async () => {
    try {
      setLoading(true);
      const data = await bookApi.getBook(id);
      setBook(data);
      setError(null);
    } catch (err) {
      console.error('获取书籍详情失败:', err);
      setError('获取书籍详情失败，请稍后再试');
      
      // 使用本地数据作为备份
      import('../data').then(({ books }) => {
        const foundBook = books.find(b => b.id === parseInt(id));
        if (foundBook) {
          setBook(foundBook);
        } else {
          setError('未找到该书籍');
        }
      });
    } finally {
      setLoading(false);
    }
  };

  // 事件处理函数
  const handleBuyNow = async() => {
    try{
      // 直接调用后端API创建订单
      await orderApi.createOrder({
        directBuy: true,
        items: [{
          bookId: book.id,
          quantity: quantity
        }]
      });

      // 显示成功提示
      setIsModalVisible(true);
      // 刷新订单列表
      fetchOrders && fetchOrders();
    }catch (err){
      message.error('购买失败: ' + (err.message || '未知错误'));
    }finally{
      setLoading(false);
    }
  };
  
  const handleOk = () => {
    setIsModalVisible(false);
  };
  
  const handleBackToHome = () => {
    navigate('/');
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', margin: '100px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error && !book) {
    return (
      <div style={{ padding: '24px' }}>
        <Alert
          message="错误"
          description={error}
          type="error"
          showIcon
          action={
            <Button type="primary" onClick={handleBackToHome}>
              返回首页
            </Button>
          }
        />
      </div>
    );
  }

  if (!book) return null;

  return (
    <div style={{ padding: '24px' }}>
      <Row gutter={24}>
        <Col span={12}>
          <img
            src={book.cover}
            alt={book.title}
            style={{
              width: '100%',
              maxHeight: '500px',
              objectFit: 'contain',
              border: '1px solid #f0f0f0'
            }}
          />
        </Col>
        <Col span={12}>
          <h1 style={{ fontSize: '24px', marginBottom: '16px' }}>{book.title}</h1>
          <p style={{ color: '#666', marginBottom: '24px' }}>{book.description}</p>

          <Descriptions bordered column={1}>
            <Descriptions.Item label="作者">{book.author}</Descriptions.Item>
            <Descriptions.Item label="价格">￥{book.price}</Descriptions.Item>
            {book.status && <Descriptions.Item label="库存">{book.status}</Descriptions.Item>}
          </Descriptions>

          <div style={{ margin: '24px 0' }}>
            <span style={{ marginRight: '16px' }}>数量：</span>
            <InputNumber
              min={1}
              value={quantity}
              onChange={setQuantity}
              style={{ width: '100px' }}
            />
          </div>

          <div>
            <Button
              type="primary"
              size="large"
              style={{ marginRight: '16px' }}
              onClick={() => addToCart(book, quantity)}
            >
              加入购物车
            </Button>
            <Button
              type="primary"
              size="large"
              danger
              onClick={handleBuyNow}
            >
              立即购买
            </Button>
          </div>

          <Modal
            title="购买成功"
            visible={isModalVisible}
            onOk={handleOk}
            onCancel={() => setIsModalVisible(false)}
          >
            <p>购买成功！</p>
          </Modal>
        </Col>
      </Row>
    </div>
  );
}
