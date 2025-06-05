import React, { useState, useEffect } from 'react';
import { 
    Table, 
    Button, 
    Modal, 
    Form, 
    Input, 
    InputNumber, 
    message, 
    Popconfirm, 
    Space, 
    Card 
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons';

const API_BASE_URL = 'http://localhost:8080/api';

const AdminBookPage = () => {
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(false);
    const [modalVisible, setModalVisible] = useState(false);
    const [editingBook, setEditingBook] = useState(null);
    const [form] = Form.useForm();
    const [searchKeyword, setSearchKeyword] = useState('');

    // 获取书籍列表
    const fetchBooks = async () => {
        setLoading(true);
        try {
            const response = await fetch(`${API_BASE_URL}/admin/books`, {
                credentials: 'include'
            });
            const result = await response.json();
            
            if (result.success) {
                setBooks(result.data);
            } else {
                message.error(result.message || '获取书籍列表失败');
            }
        } catch (error) {
            console.error('获取书籍列表失败:', error);
            message.error('网络错误，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    // 搜索书籍
    const searchBooks = async (keyword) => {
        if (!keyword) {
            fetchBooks();
            return;
        }
        
        setLoading(true);
        try {
            const response = await fetch(`${API_BASE_URL}/admin/books/search?keyword=${encodeURIComponent(keyword)}`, {
                credentials: 'include'
            });
            const result = await response.json();
            
            if (result.success) {
                setBooks(result.data);
            } else {
                message.error(result.message || '搜索失败');
            }
        } catch (error) {
            console.error('搜索失败:', error);
            message.error('网络错误，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    // 添加或更新书籍
    const handleSaveBook = async (values) => {
        setLoading(true);
        try {
            const url = editingBook ? 
                `${API_BASE_URL}/admin/books/${editingBook.id}` : 
                `${API_BASE_URL}/admin/books`;
            
            const method = editingBook ? 'PUT' : 'POST';
            
            const response = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(values)
            });
            
            const result = await response.json();
            
            if (result.success) {
                message.success(editingBook ? '书籍更新成功' : '书籍添加成功');
                setModalVisible(false);
                setEditingBook(null);
                form.resetFields();
                fetchBooks();
            } else {
                message.error(result.message || '操作失败');
            }
        } catch (error) {
            console.error('保存书籍失败:', error);
            message.error('网络错误，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    // 删除书籍
    const handleDeleteBook = async (id) => {
        setLoading(true);
        try {
            const response = await fetch(`${API_BASE_URL}/admin/books/${id}`, {
                method: 'DELETE',
                credentials: 'include'
            });
            
            const result = await response.json();
            
            if (result.success) {
                message.success('书籍删除成功');
                fetchBooks();
            } else {
                message.error(result.message || '删除失败');
            }
        } catch (error) {
            console.error('删除书籍失败:', error);
            message.error('网络错误，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    // 打开编辑模态框
    const handleEdit = (book) => {
        setEditingBook(book);
        form.setFieldsValue(book);
        setModalVisible(true);
    };

    // 打开添加模态框
    const handleAdd = () => {
        setEditingBook(null);
        form.resetFields();
        setModalVisible(true);
    };

    // 关闭模态框
    const handleCancel = () => {
        setModalVisible(false);
        setEditingBook(null);
        form.resetFields();
    };

    useEffect(() => {
        fetchBooks();
    }, []);

    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
            width: 80,
        },
        {
            title: '书籍标题',
            dataIndex: 'title',
            key: 'title',
        },
        {
            title: '作者',
            dataIndex: 'author',
            key: 'author',
        },
        {
            title: '价格',
            dataIndex: 'price',
            key: 'price',
            render: (price) => `¥${price}`,
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (status) => status === 'AVAILABLE' ? '有库存' : '缺货',
        },
        {
            title: '操作',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button 
                        type="primary" 
                        size="small" 
                        icon={<EditOutlined />}
                        onClick={() => handleEdit(record)}
                    >
                        编辑
                    </Button>
                    <Popconfirm
                        title="确定要删除这本书吗？"
                        onConfirm={() => handleDeleteBook(record.id)}
                        okText="确定"
                        cancelText="取消"
                    >
                        <Button 
                            type="primary" 
                            danger 
                            size="small" 
                            icon={<DeleteOutlined />}
                        >
                            删除
                        </Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <Card>
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
                <div>
                    <h2>书籍管理</h2>
                </div>
                <div style={{ display: 'flex', gap: 8 }}>
                    <Input.Search
                        placeholder="搜索书籍标题或作者"
                        style={{ width: 300 }}
                        value={searchKeyword}
                        onChange={(e) => setSearchKeyword(e.target.value)}
                        onSearch={searchBooks}
                        enterButton={<SearchOutlined />}
                        allowClear
                    />
                    <Button 
                        type="primary" 
                        icon={<PlusOutlined />}
                        onClick={handleAdd}
                    >
                        添加书籍
                    </Button>
                </div>
            </div>

            <Table
                columns={columns}
                dataSource={books}
                rowKey="id"
                loading={loading}
                pagination={{
                    showSizeChanger: true,
                    showQuickJumper: true,
                    showTotal: (total) => `共 ${total} 本书籍`,
                }}
            />

            <Modal
                title={editingBook ? '编辑书籍' : '添加书籍'}
                visible={modalVisible}
                onCancel={handleCancel}
                onOk={() => form.submit()}
                confirmLoading={loading}
                width={600}
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSaveBook}
                >
                    <Form.Item
                        name="title"
                        label="书籍标题"
                        rules={[{ required: true, message: '请输入书籍标题！' }]}
                    >
                        <Input placeholder="请输入书籍标题" />
                    </Form.Item>

                    <Form.Item
                        name="author"
                        label="作者"
                        rules={[{ required: true, message: '请输入作者！' }]}
                    >
                        <Input placeholder="请输入作者" />
                    </Form.Item>

                    <Form.Item
                        name="price"
                        label="价格"
                        rules={[{ required: true, message: '请输入价格！' }]}
                    >
                        <InputNumber
                            placeholder="请输入价格"
                            style={{ width: '100%' }}
                            min={0}
                            precision={2}
                            formatter={value => `¥ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                            parser={value => value.replace(/¥\s?|(,*)/g, '')}
                        />
                    </Form.Item>

                    <Form.Item
                        name="description"
                        label="书籍描述"
                    >
                        <Input.TextArea 
                            placeholder="请输入书籍描述"
                            rows={4}
                        />
                    </Form.Item>

                    <Form.Item
                        name="cover"
                        label="封面图片URL"
                    >
                        <Input placeholder="请输入封面图片URL（可选）" />
                    </Form.Item>

                    <Form.Item
                        name="status"
                        label="状态"
                        initialValue="AVAILABLE"
                    >
                        <Input placeholder="状态：AVAILABLE 或 OUT_OF_STOCK" />
                    </Form.Item>
                </Form>
            </Modal>
        </Card>
    );
};

export default AdminBookPage; 