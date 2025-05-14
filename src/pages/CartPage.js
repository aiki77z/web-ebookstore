import React, {useContext, useMemo, useState} from 'react';
import { Table, Button, Space, Typography, Checkbox, Modal } from 'antd';
import { CartContext } from '../contexts/CartContext';

const { Title } = Typography;

export default function CartPage() {
  const { cartItems, removeFromCart, toggleSelect, addOrder } = useContext(CartContext);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const selectedItems = cartItems.filter(item => item.selected);
  const total = selectedItems.reduce(
    (sum, item) => sum + item.price * item.quantity, 0
  );

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

  const handleCheckout = () => {
    // 添加到订单
    if (selectedItems.length > 0) {
      addOrder(selectedItems);
      setIsModalVisible(true);
    }
  };

  const handleOk = () => {
    setIsModalVisible(false);
  };

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>购物车</Title>
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
        >
          结算
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
    </div>
  );
}
