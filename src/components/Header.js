import React from 'react';
import { Layout, Typography } from 'antd';

const { Header } = Layout;
const { Title } = Typography;

export default function AppHeader() {
  return (
    <Header style={{ background: '#fff', padding: 0 }}>
      <Title level={3} style={{ margin: '16px 0' }}>
        在线书店
      </Title>
    </Header>
  );
}
