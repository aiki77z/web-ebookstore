import React from 'react';
import { Card, Tag } from 'antd';
import { Link } from 'react-router-dom';

const { Meta } = Card;

export default function BookCard({ book }) {
  const isOutOfStock = book.status === 'OUT_OF_STOCK';

  return (
    <Card
      hoverable//阴影和上浮
      style={{ width: '100%', border: '1px solid #f0f0f0' }}
      cover={
        <div style={{ position: 'relative' }}>
          <img
            alt={book.title}
            src={book.cover}
            style={{ height: '200px', objectFit: 'cover', opacity: isOutOfStock ? 0.5 : 1 }}
          />
          {isOutOfStock && (
            <Tag color="error" style={{
              position: 'absolute',
              top: '10px',
              right: '10px',
              fontSize: '14px'
            }}>
              售罄
            </Tag>
          )}
        </div>
      }
    >
        {/*使用props接收父组件数据*/}
      <Meta
        title={
          <div style={{ color: isOutOfStock ? '#999' : 'inherit' }}>
            {book.title}
          </div>
        }
        description={
          <div style={{ color: isOutOfStock ? '#999' : 'inherit' }}>
            ￥{book.price}
          </div>
        }
      />
      <div style={{ marginTop: '16px' }}>
          {/* 使用React Router的Link实现导航 */}
        <Link 
          to={`/book/${book.id}`}
          style={{ color: isOutOfStock ? '#999' : 'inherit' }}
        >
          查看详情
        </Link>
      </div>
    </Card>
  );
}