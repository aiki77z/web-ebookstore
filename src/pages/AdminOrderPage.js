import React, { useState, useEffect } from 'react';
import { Table, Empty, Spin, Card, Input, DatePicker, Button, Space, message, Tag } from 'antd';
import { SearchOutlined, ReloadOutlined, UserOutlined } from '@ant-design/icons';
import { orderApi } from '../services/api';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;

export default function AdminOrderPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchParams, setSearchParams] = useState({
    bookName: '',
    dateRange: null
  });

  // 组件加载时获取订单数据
  useEffect(() => {
    fetchAllOrders();
  }, []);

  const fetchAllOrders = async () => {
    setLoading(true);
    try {
      const response = await orderApi.getAllOrdersForAdmin();
      if (response.success) {
        setOrders(response.data || []);
      } else {
        message.error(response.message || '获取订单失败');
      }
    } catch (error) {
      console.error('获取订单失败:', error);
      message.error('获取订单失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    setLoading(true);
    try {
      const params = {};
      
      if (searchParams.bookName.trim()) {
        params.bookName = searchParams.bookName.trim();
      }
      
      if (searchParams.dateRange && searchParams.dateRange.length === 2) {
        params.startDate = searchParams.dateRange[0].format('YYYY-MM-DDTHH:mm:ss');
        params.endDate = searchParams.dateRange[1].format('YYYY-MM-DDTHH:mm:ss');
      }

      const response = await orderApi.searchAllOrdersForAdmin(params);
      if (response.success) {
        setOrders(response.data || []);
        message.success(`找到 ${response.data?.length || 0} 条订单记录`);
      } else {
        message.error(response.message || '搜索订单失败');
      }
    } catch (error) {
      console.error('搜索订单失败:', error);
      message.error('搜索订单失败');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setSearchParams({
      bookName: '',
      dateRange: null
    });
    fetchAllOrders();
  };

  const expandedRowRender = (record) => {
    const columns = [
      { title: '书籍名称', dataIndex: 'title', key: 'title' },
      { title: '作者', dataIndex: 'author', key: 'author' },
      { title: '单价', dataIndex: 'price', key: 'price', render: price => `¥${price}` },
      { title: '数量', dataIndex: 'quantity', key: 'quantity' },
      { title: '小计', dataIndex: 'subtotal', key: 'subtotal', render: subtotal => `¥${subtotal}` },
    ];

    return (
      <Table
        columns={columns}
        dataSource={record.orderItems}
        pagination={false}
        rowKey="id"
        size="small"
      />
    );
  };

  const columns = [
    {
      title: '订单ID',
      dataIndex: 'id',
      key: 'id',
      width: 100,
    },
    {
      title: '用户信息',
      key: 'user',
      width: 150,
      render: (_, record) => (
        <div>
          <div>
            <UserOutlined style={{ marginRight: 4 }} />
            用户ID: {record.user?.id || '-'}
          </div>
          <div style={{ fontSize: '12px', color: '#666' }}>
            {record.user?.name || record.user?.username || '-'}
          </div>
        </div>
      ),
    },
    {
      title: '订单日期',
      dataIndex: 'orderDate',
      key: 'orderDate',
      width: 180,
      render: (date) => {
        if (!date) return '-';
        return dayjs(date).format('YYYY-MM-DD HH:mm:ss');
      },
      sorter: (a, b) => new Date(a.orderDate) - new Date(b.orderDate),
    },
    {
      title: '订单总额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 120,
      render: (amount) => `¥${amount || 0}`,
      sorter: (a, b) => (a.totalAmount || 0) - (b.totalAmount || 0),
    },
    {
      title: '订单状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => {
        const statusConfig = {
          'PENDING': { color: 'orange', text: '待处理' },
          'PAID': { color: 'blue', text: '已支付' },
          'SHIPPED': { color: 'purple', text: '已发货' },
          'DELIVERED': { color: 'green', text: '已送达' },
          'CANCELLED': { color: 'red', text: '已取消' },
          'COMPLETED': { color: 'green', text: '已完成' }
        };
        const config = statusConfig[status] || { color: 'default', text: status };
        return <Tag color={config.color}>{config.text}</Tag>;
      },
      filters: [
        { text: '待处理', value: 'PENDING' },
        { text: '已支付', value: 'PAID' },
        { text: '已发货', value: 'SHIPPED' },
        { text: '已送达', value: 'DELIVERED' },
        { text: '已取消', value: 'CANCELLED' },
        { text: '已完成', value: 'COMPLETED' },
      ],
      onFilter: (value, record) => record.status === value,
    },
    {
      title: '商品数量',
      key: 'itemCount',
      width: 100,
      render: (_, record) => `${record.orderItems?.length || 0} 种商品`,
    },
    {
      title: '配送地址',
      dataIndex: 'shippingAddress',
      key: 'shippingAddress',
      width: 200,
      ellipsis: true,
      render: (address) => address || '-',
    },
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
        expandable={{
          expandedRowRender,
          rowExpandable: (record) => record.orderItems && record.orderItems.length > 0,
        }}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/共 ${total} 条`,
          pageSizeOptions: ['10', '20', '50', '100'],
        }}
        scroll={{ x: 1200 }}
        size="middle"
      />
    );
  };

  // 计算统计信息
  const getStatistics = () => {
    if (!orders || orders.length === 0) return null;

    const totalOrders = orders.length;
    const totalAmount = orders.reduce((sum, order) => sum + (order.totalAmount || 0), 0);
    const statusCount = orders.reduce((acc, order) => {
      acc[order.status] = (acc[order.status] || 0) + 1;
      return acc;
    }, {});

    return { totalOrders, totalAmount, statusCount };
  };

  const statistics = getStatistics();

  return (
    <div style={{ padding: '24px' }}>
      <Card title="订单管理" style={{ marginBottom: 24 }}>
        <Space direction="vertical" style={{ width: '100%' }} size="middle">
          {/* 统计信息 */}
          {statistics && (
            <Card size="small" style={{ marginBottom: 16 }}>
              <Space size="large">
                <div>
                  <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff' }}>
                    {statistics.totalOrders}
                  </div>
                  <div style={{ color: '#666' }}>总订单数</div>
                </div>
                <div>
                  <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#52c41a' }}>
                    ¥{statistics.totalAmount.toFixed(2)}
                  </div>
                  <div style={{ color: '#666' }}>总订单金额</div>
                </div>
                <div>
                  <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#faad14' }}>
                    {statistics.statusCount['COMPLETED'] || 0}
                  </div>
                  <div style={{ color: '#666' }}>已完成订单</div>
                </div>
                <div>
                  <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#ff4d4f' }}>
                    {statistics.statusCount['PENDING'] || 0}
                  </div>
                  <div style={{ color: '#666' }}>待处理订单</div>
                </div>
              </Space>
            </Card>
          )}

          {/* 搜索区域 */}
          <Space wrap>
            <Input
              placeholder="输入书籍名称搜索"
              value={searchParams.bookName}
              onChange={(e) => setSearchParams({ ...searchParams, bookName: e.target.value })}
              style={{ width: 200 }}
              allowClear
            />
            <RangePicker
              value={searchParams.dateRange}
              onChange={(dates) => setSearchParams({ ...searchParams, dateRange: dates })}
              showTime
              placeholder={['开始时间', '结束时间']}
            />
            <Button
              type="primary"
              icon={<SearchOutlined />}
              onClick={handleSearch}
              loading={loading}
            >
              搜索
            </Button>
            <Button
              icon={<ReloadOutlined />}
              onClick={handleReset}
              loading={loading}
            >
              重置
            </Button>
          </Space>

          {/* 订单列表 */}
          {renderContent()}
        </Space>
      </Card>
    </div>
  );
} 