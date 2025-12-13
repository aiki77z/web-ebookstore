import React, { useState, useEffect, useRef } from 'react';
import { Button, Input, Avatar, Spin, Card, message } from 'antd';
import { SendOutlined, RobotOutlined, UserOutlined, CloseOutlined } from '@ant-design/icons';
import { sendChatMessage } from '../services/chatApi';
import './ChatWidget.css';

const defaultIntro = {
  role: 'assistant',
  content: '你好！我是电子书店智能助手，可以帮你查询书店里的书籍信息。有什么可以帮你的吗？',
  timestamp: new Date(),
};

const ChatPanel = ({ onClose, style = {}, className = '' }) => {
  const [messages, setMessages] = useState([defaultIntro]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  useEffect(() => {
    if (inputRef.current) {
      inputRef.current.focus();
    }
  }, []);

  const history = messages
    .slice(-10)
    .map((msg) => ({ role: msg.role === 'assistant' ? 'assistant' : 'user', content: msg.content }));

  const handleSend = async () => {
    if (!inputValue.trim() || loading) return;

    const userMessage = {
      role: 'user',
      content: inputValue.trim(),
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInputValue('');
    setLoading(true);

    try {
      const res = await sendChatMessage(userMessage.content, history);
      const assistantMessage = {
        role: 'assistant',
        content: res.answer || res.message || '抱歉，我没有理解你的问题。',
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, assistantMessage]);
    } catch (error) {
      console.error('Chat error:', error);
      message.error('聊天服务不可用，请稍后重试');
      setMessages((prev) => [
        ...prev,
        {
          role: 'assistant',
          content: '抱歉，服务暂时不可用，请稍后再试。',
          timestamp: new Date(),
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <Card
      className={`chat-panel ${className}`}
      style={{
        width: 400,
        height: 600,
        display: 'flex',
        flexDirection: 'column',
        boxShadow: '0 8px 24px rgba(0,0,0,0.25)',
        borderRadius: 16,
        padding: 0,
        ...style,
      }}
      bodyStyle={{ display: 'flex', flexDirection: 'column', flex: 1, padding: 0 }}
    >
      <div className="chat-panel-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <RobotOutlined style={{ fontSize: 18, color: '#1890ff' }} />
          <span>书店智能助手</span>
        </div>
        {onClose && (
          <Button
            type="text"
            icon={<CloseOutlined />}
            onClick={onClose}
            style={{ color: '#333' }}
          />
        )}
      </div>

      <div className="chat-panel-body">
        {messages.map((msg, index) => (
          <div
            key={index}
            className={`chat-message ${msg.role === 'user' ? 'chat-message-user' : 'chat-message-assistant'}`}
          >
            <Avatar
              icon={msg.role === 'user' ? <UserOutlined /> : <RobotOutlined />}
              size="small"
              style={msg.role === 'user' ? { backgroundColor: '#1890ff' } : { backgroundColor: '#52c41a' }}
            />
            <div className="chat-message-content">{msg.content}</div>
          </div>
        ))}
        {loading && (
          <div className="chat-message chat-message-assistant">
            <Avatar icon={<RobotOutlined />} size="small" style={{ backgroundColor: '#52c41a' }} />
            <Spin size="small" />
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      <div className="chat-panel-footer">
        <Input
          ref={inputRef}
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="输入你的问题..."
          disabled={loading}
          style={{ flex: 1 }}
        />
        <Button
          type="primary"
          icon={<SendOutlined />}
          onClick={handleSend}
          loading={loading}
          disabled={!inputValue.trim()}
        >
          发送
        </Button>
      </div>
    </Card>
  );
};

export default ChatPanel;

