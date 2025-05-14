import React, { useState, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { CartContext } from '../contexts/CartContext';
import {Row, Col, Button, InputNumber, Descriptions, Modal} from 'antd';
import { books } from '../data';

export default function BookDetailPage() {
  // 使用自定义Hook获取路由参数
  const { id } = useParams();
  // 使用Context消费全局状态
  const { addToCart, addOrder } = useContext(CartContext);
  // 状态管理组件内部数据
  const [quantity, setQuantity] = useState(1);//购买数量状态
  const [isModalVisible, setIsModalVisible] = useState(false);//模态框显示状态
  const book = books.find(b => b.id === parseInt(id));//根据id查找书籍

  // 事件处理函数
  const handleBuyNow = () => {
    // 调用Context方法更新全局状态
    addOrder([{ ...book, quantity }]);
    setIsModalVisible(true);
  };
  const handleOk = ()=>{
    setIsModalVisible(false);
  }

  if (!book) return <div>书籍不存在</div>;

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
            visible = {isModalVisible}
            onOk={handleOk}
            onCancel={()=>setIsModalVisible(false)}
            >
            <p>购买成功！</p>
          </Modal>
        </Col>
      </Row>
    </div>
  );
}
