import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Row, Col, Card, message, Spin } from 'antd';
import authService from '../services/authService';
import { userApi } from '../services/api';

export default function PersonalInfo() {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // 组件加载时获取用户信息
  useEffect(() => {
    fetchUserInfo();
  }, []);

  // 从当前登录用户获取信息
  const fetchUserInfo = async () => {
    try {
      setLoading(true);
      
      // 先尝试从后端获取用户信息
      try {
        const userInfo = await userApi.getUserInfo();
        if (userInfo) {
          form.setFieldsValue({
            name: userInfo.name,
            email: userInfo.email,
            address: userInfo.address,
            username: authService.getCurrentUser()?.username || ''
          });
          return;
        }
      } catch (err) {
        console.warn('从后端获取用户信息失败，使用本地信息:', err);
      }
      
      // 如果后端失败，使用本地存储的用户信息
      const localUserInfo = authService.getCurrentUser();
      if (localUserInfo) {
        form.setFieldsValue({
          name: localUserInfo.name,
          email: localUserInfo.email,
          address: localUserInfo.address,
          username: localUserInfo.username
        });
      } else {
        message.error('获取用户信息失败');
      }
    } catch (err) {
      console.error('获取用户信息失败:', err);
      message.error('获取用户信息失败');
    } finally {
      setLoading(false);
    }
  };

  // 提交用户信息
  const onFinish = async (values) => {
    try {
      setSubmitting(true);
      
      // 调用后端API更新用户信息
      const updatedUserInfo = await userApi.updateUserInfo({
        name: values.name,
        email: values.email,
        address: values.address
      });
      
      if (updatedUserInfo) {
        // 更新本地存储的用户信息
        const currentUser = authService.getCurrentUser();
        if (currentUser) {
          authService.setCurrentUser({
            ...currentUser,
            name: updatedUserInfo.name,
            email: updatedUserInfo.email,
            address: updatedUserInfo.address
          });
        }
        
        message.success('用户信息更新成功');
        
        // 重新获取用户信息以确保数据同步
        await fetchUserInfo();
      } else {
        throw new Error('更新用户信息返回空数据');
      }
    } catch (err) {
      console.error('保存用户信息失败:', err);
      message.error('保存失败: ' + (err.message || '请稍后再试'));
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
      <h2 style={{ marginBottom: '24px' }}>个人信息</h2>
      <Form
        form={form}
        onFinish={onFinish}
        layout="vertical"
      >
        <Card title="基本信息" bordered={false} style={{ marginBottom: '24px' }}>
          <Row gutter={24}>
            <Col span={12}>
              <Form.Item
                name="username"
                label="用户名"
              >
                <Input placeholder="用户名" disabled />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="name"
                label="姓名"
                rules={[{ required: true, message: '请输入姓名' }]}
              >
                <Input placeholder="姓名" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={24}>
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

        <Card title="地址信息" bordered={false} style={{ marginBottom: '24px' }}>
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
            <Button onClick={handleCancel}>取消</Button>
          </Col>
          <Col>
            <Button type="primary" htmlType="submit" loading={submitting}>
              保存
            </Button>
          </Col>
        </Row>
      </Form>
    </div>
  );
}
