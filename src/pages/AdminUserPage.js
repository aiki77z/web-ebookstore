import React, { useState, useEffect } from 'react';
import { Table, Switch, message, DatePicker, Button, Space, Tag, Card } from 'antd';
import { ReloadOutlined, UserOutlined } from '@ant-design/icons';
import { getAllUsers, toggleUserStatus, getUserStatistics } from '../services/api';

const { RangePicker } = DatePicker;

const AdminUserPage = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [dateRange, setDateRange] = useState([]);
    const [viewMode, setViewMode] = useState('list'); // 'list' or 'statistics'

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        setLoading(true);
        try {
            const data = await getAllUsers();
            console.log('获取到的用户数据:', data);
            setUsers(data || []);
        } catch (error) {
            console.error('获取用户列表失败:', error);
            message.error('获取用户列表失败');
        } finally {
            setLoading(false);
        }
    };

    const handleToggleStatus = async (userId, currentStatus, username) => {
        const action = currentStatus ? '禁用' : '启用';
        try {
            setLoading(true);
            const response = await toggleUserStatus(userId);
            console.log('切换用户状态响应:', response);
            
            if (response && response.success !== false) {
                message.success(`${action}用户 ${username} 成功`);
                // 重新获取用户列表以确保数据同步
                await fetchUsers();
            } else {
                throw new Error(response?.message || `${action}用户失败`);
            }
        } catch (error) {
            console.error('切换用户状态失败:', error);
            message.error(`${action}用户失败: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };

    const handleDateRangeChange = (dates) => {
        setDateRange(dates);
    };

    const fetchStatistics = async () => {
        if (!dateRange[0] || !dateRange[1]) {
            message.warning('请选择日期范围');
            return;
        }

        setLoading(true);
        try {
            const startDate = dateRange[0].format('YYYY-MM-DDTHH:mm:ss');
            const endDate = dateRange[1].format('YYYY-MM-DDTHH:mm:ss');
            const data = await getUserStatistics(startDate, endDate);
            setUsers(data || []);
            setViewMode('statistics');
            message.success('获取用户统计成功');
        } catch (error) {
            console.error('获取用户统计失败:', error);
            message.error('获取用户统计失败');
        } finally {
            setLoading(false);
        }
    };

    const columns = [
        {
            title: '用户名',
            dataIndex: 'username',
            key: 'username',
            width: 120,
            render: (text) => (
                <span>
                    <UserOutlined style={{ marginRight: 8 }} />
                    {text}
                </span>
            ),
        },
        {
            title: '姓名',
            dataIndex: 'name',
            key: 'name',
            width: 100,
        },
        {
            title: '邮箱',
            dataIndex: 'email',
            key: 'email',
            width: 200,
            ellipsis: true,
        },
        {
            title: '角色',
            dataIndex: 'role',
            key: 'role',
            width: 80,
            render: (role) => (
                <Tag color={role === 'ADMIN' ? 'red' : 'blue'}>
                    {role === 'ADMIN' ? '管理员' : '用户'}
                </Tag>
            ),
        },
        {
            title: '状态',
            dataIndex: 'active',
            key: 'active',
            width: 100,
            render: (active, record) => (
                <Switch
                    checked={active}
                    onChange={() => handleToggleStatus(record.id, active, record.username)}
                    disabled={record.role === 'ADMIN' || loading}
                    checkedChildren="启用"
                    unCheckedChildren="禁用"
                    loading={loading}
                />
            ),
        },
        {
            title: '创建时间',
            dataIndex: 'createdAt',
            key: 'createdAt',
            width: 150,
            render: (date) => {
                if (!date) return '-';
                try {
                    return new Date(date).toLocaleString('zh-CN', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit',
                        hour: '2-digit',
                        minute: '2-digit'
                    });
                } catch {
                    return '-';
                }
            },
        },
        {
            title: '最后登录',
            dataIndex: 'lastLogin',
            key: 'lastLogin',
            width: 150,
            render: (date) => {
                if (!date) return '从未登录';
                try {
                    return new Date(date).toLocaleString('zh-CN', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit',
                        hour: '2-digit',
                        minute: '2-digit'
                    });
                } catch {
                    return '从未登录';
                }
            },
        },
    ];

    if (viewMode === 'statistics') {
        columns.push({
            title: '总消费',
            dataIndex: 'totalSpending',
            key: 'totalSpending',
            width: 100,
            render: (amount) => amount ? `¥${amount.toFixed(2)}` : '¥0.00',
        });
    }

    return (
        <div style={{ padding: '24px' }}>
            <Card title="用户管理" extra={
                <Button 
                    type="primary" 
                    icon={<ReloadOutlined />} 
                    onClick={fetchUsers}
                    loading={loading}
                >
                    刷新
                </Button>
            }>
                <Space style={{ marginBottom: 16 }} wrap>
                    <RangePicker
                        onChange={handleDateRangeChange}
                        showTime
                        placeholder={['开始日期', '结束日期']}
                        disabled={loading}
                    />
                    <Button 
                        type="primary" 
                        onClick={fetchStatistics}
                        loading={loading}
                        disabled={!dateRange[0] || !dateRange[1]}
                    >
                        查看消费统计
                    </Button>
                    <Button 
                        onClick={() => {
                            setViewMode('list');
                            fetchUsers();
                        }}
                        loading={loading}
                    >
                        查看用户列表
                    </Button>
                </Space>
                
                <Table
                    columns={columns}
                    dataSource={users}
                    rowKey="id"
                    loading={loading}
                    pagination={{
                        pageSize: 10,
                        showSizeChanger: true,
                        showQuickJumper: true,
                        showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/共 ${total} 条`,
                        pageSizeOptions: ['10', '20', '50', '100'],
                    }}
                    scroll={{ x: 800 }}
                    size="middle"
                />
            </Card>
        </div>
    );
};

export default AdminUserPage; 