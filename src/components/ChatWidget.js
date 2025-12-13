import React, { useState } from 'react';
import { Button } from 'antd';
import { RobotOutlined } from '@ant-design/icons';
import ChatPanel from './ChatPanel';
import './ChatWidget.css';

const ChatWidget = () => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      {!isOpen && (
        <Button
          type="primary"
          shape="circle"
          size="large"
          icon={<RobotOutlined />}
          onClick={() => setIsOpen(true)}
          className="chat-toggle-button"
          style={{
            position: 'fixed',
            bottom: 24,
            right: 24,
            width: 56,
            height: 56,
            zIndex: 1000,
            boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
          }}
        />
      )}

      {isOpen && (
        <div
          style={{
            position: 'fixed',
            bottom: 24,
            right: 24,
            zIndex: 1000,
          }}
        >
          <ChatPanel onClose={() => setIsOpen(false)} />
        </div>
      )}
    </>
  );
};

export default ChatWidget;
