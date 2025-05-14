import React, { useState } from 'react';
import { Row, Col, Input } from 'antd';
import BookCard from '../components/BookCard';
import { books } from '../data';

const { Search } = Input;

export default function Home() {
  const [searchText, setSearchText] = useState('');
  const [displayBooks, setDisplayBooks] = useState(books); //是否显示书籍

  //搜索处理函数
  const handleSearch = (value) => {
    if (!value.trim()) {
      setDisplayBooks(books); // 如果搜索为空，显示所有书籍
      return;
    }
    const filtered = books.filter(book =>
      book.title.toLowerCase().includes(value.toLowerCase()) ||
      book.author.toLowerCase().includes(value.toLowerCase())
    );
    setDisplayBooks(filtered);
  };

  return (
    <div style={{ padding: '20px' }}>
      <div style={{ marginBottom: '24px' }}>
        <Search
          placeholder="输入书名或作者搜索"
          allowClear
          enterButton="搜索"
          size="large"
          onChange={(e) => setSearchText(e.target.value)}
          onSearch={handleSearch} // 添加搜索事件处理
          style={{ maxWidth: '500px' }}
        />
      </div>

      <h2 style={{ marginBottom: '24px' }}>热门书籍</h2>
      <Row gutter={[24, 24]}>
        {displayBooks.map(book => (
          <Col span={6} key={book.id}>
            <BookCard book={book} />
          </Col>
        ))}
      </Row>
    </div>
  );
}
