import React, { useState, useContext, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { CartContext } from '../contexts/CartContext';
import {Row, Col, Button, InputNumber, Descriptions, Modal, Spin, Alert, message, Tag} from 'antd';
import {bookApi} from '../services/api';

export default function BookDetailPage() {
  // 使用自定义Hook获取路由参数
  const { id } = useParams();
  const navigate = useNavigate();
  
  // 使用Context消费全局状态
  const { addToCart, directPurchase } = useContext(CartContext);
  
  // 状态管理组件内部数据
  const [book, setBook] = useState(null);
  const [quantity, setQuantity] = useState(1);
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
      const response = await bookApi.getBook(id);
      
      if (response && response.success) {
        setBook(response.data);
        setError(null);
      } else {
        throw new Error(response.message || '获取书籍详情失败');
      }
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
  const handleBuyNow = async () => {
    try {
      setLoading(true);
      await directPurchase(book, quantity);
      message.success('购买成功！');
    } catch (err) {
      console.error('购买失败:', err);
      message.error(err.message || '购买失败，请稍后再试');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async () => {
    try {
      setLoading(true);
      await addToCart(book, quantity);
      message.success('已添加到购物车！');
    } catch (err) {
      console.error('添加到购物车失败:', err);
      message.error(err.message || '添加到购物车失败，请稍后再试');
    } finally {
      setLoading(false);
    }
  };
  
  const handleBackToHome = () => {
    navigate('/');
  };

  // 检查是否售罄
  const isOutOfStock = book?.status === 'OUT_OF_STOCK';

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
              border: '1px solid #f0f0f0',
              opacity: isOutOfStock ? 0.5 : 1
            }}
          />
          {isOutOfStock && (
            <Tag color="error" style={{
              position: 'absolute',
              top: '10px',
              right: '10px',
              fontSize: '14px'
            }}>
              售罄
            </Tag>
          )}
        </Col>
        <Col span={12}>
          <h1 style={{ 
            fontSize: '24px', 
            marginBottom: '16px',
            color: isOutOfStock ? '#999' : 'inherit'
          }}>
            {book.title}
          </h1>
          <p style={{ 
            color: isOutOfStock ? '#999' : '#666', 
            marginBottom: '24px' 
          }}>
            {book.description}
          </p>

          <Descriptions bordered column={1}>
            <Descriptions.Item label="作者">{book.author}</Descriptions.Item>
            <Descriptions.Item label="价格">￥{book.price}</Descriptions.Item>
            <Descriptions.Item label="库存状态">
              {isOutOfStock ? (
                <Tag color="error">售罄</Tag>
              ) : (
                <Tag color="success">有库存</Tag>
              )}
            </Descriptions.Item>
          </Descriptions>

          <div style={{ margin: '24px 0' }}>
            <span style={{ marginRight: '16px' }}>数量：</span>
            <InputNumber
              min={1}
              value={quantity}
              onChange={setQuantity}
              style={{ width: '100px' }}
              disabled={isOutOfStock}
            />
          </div>

          <div>
            <Button
              type="primary"
              size="large"
              style={{ marginRight: '16px' }}
              onClick={handleAddToCart}
              loading={loading}
              disabled={isOutOfStock}
              title={isOutOfStock ? "商品已售罄" : ""}
            >
              加入购物车
            </Button>
            <Button
              type="primary"
              size="large"
              danger
              onClick={handleBuyNow}
              loading={loading}
              disabled={isOutOfStock}
              title={isOutOfStock ? "商品已售罄" : ""}
            >
              立即购买
            </Button>
          </div>
        </Col>
      </Row>
    </div>
  );
}
