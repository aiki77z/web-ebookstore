import React, {useContext, useEffect, useMemo, useState} from 'react';
import { Table, Button, Space, Typography, Checkbox, Modal, Spin, Alert, Empty, message } from 'antd';
import { CartContext } from '../contexts/CartContext';
import { Link } from 'react-router-dom';
import { orderApi } from '../services/api';

const { Title } = Typography;

export default function CartPage() {
  const {
    cartItems,
    removeFromCart,
    toggleSelect,
    loading,
    error,
    fetchCart,
    fetchOrders
  } = useContext(CartContext);

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [checkoutLoading, setCheckoutLoading] = useState(false);
  const selectedItems = cartItems.filter(item => item.selected);
  const total = selectedItems.reduce(
      (sum, item) => sum + item.price * item.quantity, 0
  );

  // 组件加载时获取购物车数据
  useEffect(() => {
    fetchCart();
  }, []);

  const columns = useMemo(() => [
    {
      title: '选择',
      dataIndex: 'selected',
      key: 'selected',
      render: (_, record) => (
          <Checkbox
              checked={record.selected}
              onChange={() => toggleSelect(record.id)}
          />
      ),
    },
    {
      title: '书籍封面',
      dataIndex: 'cover',
      key: 'cover',
      render: (cover) => (
          <img
              src={cover}
              alt="书籍封面"
              style={{ width: '60px', height: '80px', objectFit: 'cover' }}
          />
      ),
    },
    {
      title: '书名',
      dataIndex: 'title',
      key: 'title',
      render: (title, record) => (
          <Link to={`/book/${record.bookId}`}>{title}</Link>
      ),
    },
    {
      title: '单价',
      dataIndex: 'price',
      key: 'price',
      render: (price) => `￥${price}`,
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      key: 'quantity',
    },
    {
      title: '小计',
      key: 'subtotal',
      render: (_, record) => `￥${(record.price * record.quantity).toFixed(2)}`,
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
          <Space size="middle">
            <Button
                type="link"
                danger
                onClick={() => removeFromCart(record.id)}
            >
              删除
            </Button>
          </Space>
      ),
    }
  ], [toggleSelect, removeFromCart]);

  const handleCheckout = async () => {
    if (selectedItems.length === 0) {
      message.warning('请至少选择一个商品');
      return;
    }

    try {
      setCheckoutLoading(true);

      // 获取选中的购物车项ID
      const cartItemIds = selectedItems.map(item => item.id);

      // 调用API创建订单
      await orderApi.createOrder({ cartItemIds });

      // 刷新购物车和订单数据
      await fetchCart();
      fetchOrders && fetchOrders();

      // 显示成功提示
      setIsModalVisible(true);
    } catch (err) {
      message.error('结算失败: ' + (err.message || '未知错误'));
      console.error('结算失败:', err);
    } finally {
      setCheckoutLoading(false);
    }
  };

  const handleOk = () => {
    setIsModalVisible(false);
  };

  const renderContent = () => {
    if (loading) {
      return (
          <div style={{ textAlign: 'center', margin: '50px 0' }}>
            <Spin size="large" />
          </div>
      );
    }

    if (error && cartItems.length === 0) {
      return (
          <Alert
              message="获取购物车失败"
              description={error}
              type="error"
              showIcon
          />
      );
    }

    if (cartItems.length === 0) {
      return (
          <Empty
              description="购物车为空"
              image={Empty.PRESENTED_IMAGE_SIMPLE}
          />
      );
    }

    return (
        <>
          <Table
              columns={columns}
              dataSource={cartItems}
              rowKey="id"
              pagination={false}
          />
          <div style={{ textAlign: 'right', marginTop: '24px' }}>
            <Title level={4}>总计：￥{total.toFixed(2)}</Title>
            <Button
                type="primary"
                size="large"
                onClick={handleCheckout}
                disabled={selectedItems.length === 0}
                loading={checkoutLoading}
            >
              结算
            </Button>
          </div>
        </>
    );
  };

  return (
      <div style={{ padding: '24px' }}>
        <Title level={2}>购物车</Title>
        {renderContent()}

        <Modal
            title="购买成功"
            visible={isModalVisible}
            onOk={handleOk}
            onCancel={() => setIsModalVisible(false)}
        >
          <p>购买成功！</p>
        </Modal>
      </div>
  );
}