import React from 'react';
import { Card } from 'antd';
import { Link } from 'react-router-dom';

const { Meta } = Card;

export default function BookCard({ book }) {
  return (
    <Card
      hoverable//阴影和上浮
      style={{ width: '100%', border: '1px solid #f0f0f0' }}
      cover={
        <img
          alt={book.title}
          src={book.cover}
          style={{ height: '200px', objectFit: 'cover' }}
        />
      }
    >
        {/*使用props接收父组件数据*/}
      <Meta
        title={book.title}
        description={`￥${book.price}`}
      />
      <div style={{ marginTop: '16px' }}>
          {/* 使用React Router的Link实现导航 */}
        <Link to={`/book/${book.id}`}>查看详情</Link>
      </div>
    </Card>
  );
}