import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Row, Col, Card, message, Spin } from 'antd';
import { userApi } from '../services/api';

export default function PersonalInfo() {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // 组件加载时获取用户信息
  useEffect(() => {
    fetchUserInfo();
  }, []);

  // 从API获取用户信息
  const fetchUserInfo = async () => {
    try {
      setLoading(true);
      const data = await userApi.getUserInfo();
      
      // 设置表单初始值
      form.setFieldsValue({
        name: data.name,
        email: data.email,
        address: data.address
      });
    } catch (err) {
      console.error('获取用户信息失败:', err);
      message.error('获取用户信息失败，将使用默认数据');
      
      // 使用本地数据作为备份
      import('../data').then(({ userInfo }) => {
        form.setFieldsValue({
          name: userInfo.name,
          email: userInfo.email,
          address: userInfo.address
        });
      });
    } finally {
      setLoading(false);
    }
  };

  // 提交用户信息
  const onFinish = async (values) => {
    try {
      setSubmitting(true);
      await userApi.updateUserInfo(values);
      message.success('保存成功');
    } catch (err) {
      console.error('保存用户信息失败:', err);
      message.error('保存失败，请稍后再试');
    } finally {
      setSubmitting(false);
    }
  };

  // 取消编辑
  const handleCancel = () => {
    fetchUserInfo(); // 重新加载用户信息
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', margin: '50px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div style={{ padding: '24px' }}>
      <h2 style={{ marginBottom: '24px' }}>My Profile</h2>
      <Form
        form={form}
        onFinish={onFinish}
        layout="vertical"
      >
        <Card title="User Info" bordered={false} style={{ marginBottom: '24px' }}>
          <Row gutter={24}>
            <Col span={12}>
              <Form.Item
                name="name"
                label="用户名"
                rules={[{ required: true, message: '请输入用户名' }]}
              >
                <Input placeholder="用户名" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="email"
                label="邮箱"
                rules={[
                  { required: true, message: '请输入邮箱' },
                  { type: 'email', message: '请输入有效的邮箱' }
                ]}
              >
                <Input placeholder="邮箱" />
              </Form.Item>
            </Col>
          </Row>
        </Card>

        <Card title="Address" bordered={false} style={{ marginBottom: '24px' }}>
          <Form.Item 
            name="address"
            label="地址"
            rules={[{ required: true, message: '请输入地址' }]}
          >
            <Input placeholder="地址" />
          </Form.Item>
        </Card>

        <Row justify="end" gutter={16}>
          <Col>
            <Button onClick={handleCancel}>Cancel</Button>
          </Col>
          <Col>
            <Button type="primary" htmlType="submit" loading={submitting}>
              Save
            </Button>
          </Col>
        </Row>
      </Form>
    </div>
  );
}
