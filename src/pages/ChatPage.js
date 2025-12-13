import React from 'react';
import { Card, Typography } from 'antd';
import ChatPanel from '../components/ChatPanel';

const { Title, Paragraph } = Typography;

const ChatPage = () => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
      <Card>
        <Title level={4}>书店智能聊天</Title>
        <Paragraph>
          通过右侧的聊天界面，你可以直接向电子书店智能助手提问，比如“书店里有什么？”或“推荐几本高评分的小说”。
          聊天机器人会优先调用 MCP 书籍服务内容作为上下文，并结合 DeepSeek 大模型返回回答。
        </Paragraph>
        <Paragraph type="secondary">
          如需更详细控制，点击下方输入框发送问题即可。聊天历史会保留在本次会话内。
        </Paragraph>
      </Card>

      <div style={{ display: 'flex', justifyContent: 'center' }}>
        <ChatPanel style={{ width: '100%', maxWidth: 960, height: '70vh' }} />
      </div>
    </div>
  );
};

export default ChatPage;

