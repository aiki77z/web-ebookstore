import React, { useState } from 'react';
import { Card, DatePicker, Button, Table, message, Row, Col, Statistic, Spin, Empty } from 'antd';
import { PieChart, Pie, Cell, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import { statisticsApi } from '../services/api';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;

const PersonalStatisticsPage = () => {
  const [dateRange, setDateRange] = useState([
    dayjs().subtract(30, 'day'),
    dayjs()
  ]);
  const [statisticsData, setStatisticsData] = useState(null);
  const [loading, setLoading] = useState(false);

  // 图表颜色
  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8', '#82ca9d', '#ffc658', '#ff7c7c'];

  // 加载个人统计数据
  const loadPersonalStatistics = async () => {
    if (!dateRange || dateRange.length !== 2) {
      message.warning('请选择时间范围');
      return;
    }

    setLoading(true);
    try {
      const [start, end] = dateRange;
      console.log('查询个人统计 - 开始时间:', start.format('YYYY-MM-DDTHH:mm:ss'));
      console.log('查询个人统计 - 结束时间:', end.format('YYYY-MM-DDTHH:mm:ss'));
      
      const data = await statisticsApi.getPersonalStatistics(
        start.startOf('day').format('YYYY-MM-DDTHH:mm:ss'),
        end.endOf('day').format('YYYY-MM-DDTHH:mm:ss')
      );
      console.log('个人统计数据:', data);
      setStatisticsData(data);
    } catch (error) {
      console.error('加载个人统计失败:', error);
      message.error('加载个人统计失败: ' + (error.message || '未知错误'));
    } finally {
      setLoading(false);
    }
  };

  // 书籍详情表格列
  const bookColumns = [
    {
      title: '排名',
      dataIndex: 'rank',
      key: 'rank',
      width: 80,
      render: (_, __, index) => index + 1,
    },
    {
      title: '书籍封面',
      dataIndex: 'cover',
      key: 'cover',
      width: 100,
      render: (cover) => (
        <img 
          src={cover || '/placeholder-book.jpg'} 
          alt="封面" 
          style={{ width: 60, height: 80, objectFit: 'cover' }}
        />
      ),
    },
    {
      title: '书名',
      dataIndex: 'bookTitle',
      key: 'bookTitle',
      width: 200,
    },
    {
      title: '作者',
      dataIndex: 'author',
      key: 'author',
      width: 150,
    },
    {
      title: '购买数量',
      dataIndex: 'quantity',
      key: 'quantity',
      width: 120,
      render: (quantity) => `${quantity} 本`,
      sorter: (a, b) => b.quantity - a.quantity,
    },
    {
      title: '总消费',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 120,
      render: (amount) => `¥${amount}`,
      sorter: (a, b) => parseFloat(b.totalAmount) - parseFloat(a.totalAmount),
    },
    {
      title: '平均价格',
      dataIndex: 'averagePrice',
      key: 'averagePrice',
      width: 120,
      render: (price) => `¥${price}`,
    },
  ];

  // 准备图表数据
  const prepareChartData = () => {
    if (!statisticsData || !statisticsData.bookDetails) return [];
    
    return statisticsData.bookDetails.slice(0, 10).map(item => ({
      name: item.bookTitle.length > 10 ? item.bookTitle.substring(0, 10) + '...' : item.bookTitle,
      购买数量: item.quantity,
      消费金额: parseFloat(item.totalAmount),
    }));
  };

  // 准备饼图数据
  const preparePieData = () => {
    if (!statisticsData || !statisticsData.bookDetails) return [];
    
    return statisticsData.bookDetails.slice(0, 8).map(item => ({
      name: item.bookTitle.length > 15 ? item.bookTitle.substring(0, 15) + '...' : item.bookTitle,
      value: parseFloat(item.totalAmount),
    }));
  };

  const chartData = prepareChartData();
  const pieData = preparePieData();

  // 获取最喜欢的书籍（购买数量最多）
  const getFavoriteBook = () => {
    if (!statisticsData || !statisticsData.bookDetails || statisticsData.bookDetails.length === 0) {
      return null;
    }
    return statisticsData.bookDetails[0]; // 已按购买数量降序排列
  };

  const favoriteBook = getFavoriteBook();

  return (
    <div style={{ padding: 24 }}>
      <Card title="我的购书统计" style={{ marginBottom: 24 }}>
        <Row gutter={16} style={{ marginBottom: 24 }}>
          <Col span={16}>
            <RangePicker
              value={dateRange}
              onChange={setDateRange}
              format="YYYY-MM-DD"
              placeholder={['开始日期', '结束日期']}
              style={{ width: '100%' }}
            />
          </Col>
          <Col span={8}>
            <Button type="primary" onClick={loadPersonalStatistics} loading={loading} block>
              查询我的统计
            </Button>
          </Col>
        </Row>

        {/* 统计概览 */}
        {statisticsData && (
          <>
            <Row gutter={16} style={{ marginBottom: 24 }}>
              <Col span={6}>
                <Card>
                  <Statistic 
                    title="总订单数" 
                    value={statisticsData.totalOrders} 
                    suffix="单"
                    valueStyle={{ color: '#3f8600' }}
                  />
                </Card>
              </Col>
              <Col span={6}>
                <Card>
                  <Statistic 
                    title="购书总数" 
                    value={statisticsData.totalBooks} 
                    suffix="本"
                    valueStyle={{ color: '#1890ff' }}
                  />
                </Card>
              </Col>
              <Col span={6}>
                <Card>
                  <Statistic 
                    title="总消费" 
                    value={parseFloat(statisticsData.totalAmount)} 
                    prefix="¥" 
                    precision={2}
                    valueStyle={{ color: '#cf1322' }}
                  />
                </Card>
              </Col>
              <Col span={6}>
                <Card>
                  <Statistic 
                    title="书籍种类" 
                    value={statisticsData.bookDetails ? statisticsData.bookDetails.length : 0} 
                    suffix="种"
                    valueStyle={{ color: '#722ed1' }}
                  />
                </Card>
              </Col>
            </Row>

            {/* 最喜欢的书籍 */}
            {favoriteBook && (
              <Card title="我最喜欢的书" style={{ marginBottom: 24 }}>
                <Row gutter={16} align="middle">
                  <Col span={4}>
                    <img 
                      src={favoriteBook.cover || '/placeholder-book.jpg'} 
                      alt={favoriteBook.bookTitle}
                      style={{ width: '100%', maxWidth: 120, height: 160, objectFit: 'cover' }}
                    />
                  </Col>
                  <Col span={20}>
                    <h3>{favoriteBook.bookTitle}</h3>
                    <p><strong>作者：</strong>{favoriteBook.author}</p>
                    <p><strong>购买数量：</strong>{favoriteBook.quantity} 本</p>
                    <p><strong>总消费：</strong>¥{favoriteBook.totalAmount}</p>
                    <p><strong>平均价格：</strong>¥{favoriteBook.averagePrice}</p>
                  </Col>
                </Row>
              </Card>
            )}

            {/* 图表展示 */}
            {chartData.length > 0 && (
              <Row gutter={16} style={{ marginBottom: 24 }}>
                <Col span={14}>
                  <Card title="我的购书排行" size="small">
                    <ResponsiveContainer width="100%" height={300}>
                      <BarChart data={chartData}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="name" />
                        <YAxis />
                        <Tooltip />
                        <Legend />
                        <Bar dataKey="购买数量" fill="#8884d8" />
                      </BarChart>
                    </ResponsiveContainer>
                  </Card>
                </Col>
                <Col span={10}>
                  <Card title="消费分布" size="small">
                    <ResponsiveContainer width="100%" height={300}>
                      <PieChart>
                        <Pie
                          data={pieData}
                          cx="50%"
                          cy="50%"
                          labelLine={false}
                          label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                          outerRadius={80}
                          fill="#8884d8"
                          dataKey="value"
                        >
                          {pieData.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                          ))}
                        </Pie>
                        <Tooltip />
                      </PieChart>
                    </ResponsiveContainer>
                  </Card>
                </Col>
              </Row>
            )}

            {/* 详细购书记录 */}
            <Card title="详细购书记录" size="small">
              <Spin spinning={loading}>
                {statisticsData.bookDetails && statisticsData.bookDetails.length > 0 ? (
                  <Table
                    columns={bookColumns}
                    dataSource={statisticsData.bookDetails}
                    rowKey="bookId"
                    pagination={{ pageSize: 10 }}
                    scroll={{ x: 800 }}
                  />
                ) : (
                  <Empty 
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                    description="在选定时间范围内没有购书记录"
                  />
                )}
              </Spin>
            </Card>
          </>
        )}

        {/* 无数据提示 */}
        {!loading && !statisticsData && (
          <Empty 
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description="请选择时间范围并点击查询按钮"
          />
        )}
      </Card>
    </div>
  );
};

export default PersonalStatisticsPage; 