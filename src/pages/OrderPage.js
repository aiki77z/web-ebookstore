import React, { useContext, useEffect } from 'react';
import { Table, Typography, Empty, Spin, Alert } from 'antd';
import { CartContext } from '../contexts/CartContext';
import { Link } from 'react-router-dom';

const { Title } = Typography;

export default function OrderPage() {
  const { orders, loading, fetchOrders } = useContext(CartContext);

  // 组件加载时获取订单数据
  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  const columns = [
    {
      title: '订单日期',
      dataIndex: 'orderDate',
      key: 'orderDate',
      render: (orderDate) => orderDate || '-'
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
      title: '作者',
      dataIndex: 'author',
      key: 'author',
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
      title: '小计',
      dataIndex: 'subtotal',
      key: 'subtotal',
      render: (subtotal) => `￥${subtotal || 0}`
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

    if (!orders || orders.length === 0) {
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
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条记录`,
        }}
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


