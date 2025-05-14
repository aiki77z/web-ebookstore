import React, { useState } from 'react';
import { Form, Input, Button, Row, Col, Card } from 'antd';
import { userInfo } from '../data';

export default function PersonalInfo() {
  const [form] = Form.useForm();
  
  const onFinish = (values) => {
    console.log('保存信息:', values);
  };

  return (
    <div style={{ padding: '24px' }}>
      <h2 style={{ marginBottom: '24px' }}>My Profile</h2>
      <Form
        form={form}
        initialValues={{
          name: userInfo.name,
          email: userInfo.email,
          address: userInfo.address
        }}
        onFinish={onFinish}
        layout="vertical"
      >
        <Card title="name" bordered={false} style={{ marginBottom: '24px' }}>
          <Row gutter={24}>
            <Col span={12}>
              <Form.Item
                name="name"
                rules={[{ required: true, message: '请输入用户名' }]}
              >
                <Input placeholder="name" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="email"
                rules={[{ required: true, message: '请输入邮箱' }]}
              >
                <Input placeholder="email" />
              </Form.Item>
            </Col>
          </Row>
        </Card>

        <Card title="address" bordered={false} style={{ marginBottom: '24px' }}>
          <Form.Item name="address">
            <Input placeholder="address" />
          </Form.Item>
        </Card>

        <Row justify="end" gutter={16}>
          <Col>
            <Button>Cancel</Button>
          </Col>
          <Col>
            <Button type="primary" htmlType="submit">
              Save
            </Button>
          </Col>
        </Row>
      </Form>
    </div>
  );
}
