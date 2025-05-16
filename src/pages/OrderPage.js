import React, { useContext, useEffect } from 'react';
import { Table, Typography, Empty, Spin, Alert } from 'antd';
import { CartContext } from '../contexts/CartContext';
import { Link } from 'react-router-dom';

const { Title } = Typography;

export default function OrderPage() {
  const { orders, loading, error, fetchOrders } = useContext(CartContext);

  // 组件加载时获取订单数据
  useEffect(() => {
    fetchOrders();
  }, []);

  const columns = [
    {
      title: '订单日期',
      dataIndex: 'date',
      key: 'date',
    },
    {
      title: '书籍名称',
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
      render: price => `￥${price}`
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      key: 'quantity',
    },
    {
      title: '总价',
      key: 'total',
      render: (_, record) => `￥${(record.price * record.quantity).toFixed(2)}`
    }
  ];

  const renderContent = () => {
    if (loading) {
      return (
        <div style={{ textAlign: 'center', margin: '50px 0' }}>
          <Spin size="large" />
        </div>
      );
    }

    if (error) {
      return (
        <Alert
          message="获取订单失败"
          description={error}
          type="error"
          showIcon
        />
      );
    }

    if (orders.length === 0) {
      return (
        <Empty
          description="暂无订单记录"
          image={Empty.PRESENTED_IMAGE_SIMPLE}
        />
      );
    }

    return (
      <Table
        columns={columns}
        dataSource={orders}
        rowKey="id"
        pagination={false}
      />
    );
  };

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>我的订单</Title>
      {renderContent()}
    </div>
  );
}


