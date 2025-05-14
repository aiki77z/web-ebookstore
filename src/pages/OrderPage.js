import React, { useContext } from 'react';
import { Table, Typography } from 'antd';
import { CartContext } from '../contexts/CartContext';

const { Title } = Typography;

export default function OrderPage() {
  const { orders } = useContext(CartContext);

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

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>我的订单</Title>
      {orders.length === 0 ? (
        <p>暂无订单记录</p>
      ) : (
        <Table
          columns={columns}
          dataSource={orders}
          rowKey="id"
          pagination={false}
        />
      )}
    </div>
  );
}


