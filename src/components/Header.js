import React from 'react';
import { Layout, Typography, Input, message } from 'antd';
import { externalApi } from '../services/api';

const { Header } = Layout;
const { Title } = Typography;
const { Search } = Input;

export default function AppHeader() {
  const onAuthorSearch = async (value) => {
    const title = (value || '').trim();
    if (!title) {
      message.warning('请输入书名再进行作者查询');
      return;
    }
    try {
      const res = await externalApi.authorLookup(title);
      if (res.success && res.data && res.data.author) {
        message.success(`《${res.data.title}》作者：${res.data.author}`);
      } else {
        message.info(res.message || '未查询到对应作者');
      }
    } catch (e) {
      message.error('查询作者失败，请稍后再试');
    }
  };

  return (
    <Header style={{ background: '#fff', padding: '0 16px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <Title level={3} style={{ margin: '16px 0' }}>在线书店</Title>
      <div style={{ width: 360 }}>
        <Search allowClear enterButton="作者查询" placeholder="输入书名，查询作者"
                onSearch={onAuthorSearch} />
      </div>
    </Header>
  );
}
