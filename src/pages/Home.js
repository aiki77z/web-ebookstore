import React, { useState, useEffect } from 'react';
import { Row, Col, Input, Spin, Alert, Button, message, Modal } from 'antd';
import BookCard from '../components/BookCard';
import { bookApi, externalApi } from '../services/api';

const { Search } = Input;

export default function Home() {
  const [searchText, setSearchText] = useState('');
  const [displayBooks, setDisplayBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // 初始化加载书籍数据
  useEffect(() => {
    fetchBooks();
  }, []);

  // 从后端获取书籍数据
  const fetchBooks = async () => {
    try {
      setLoading(true);
      const response = await bookApi.getBooks();
      
      if (response && response.success) {
        setDisplayBooks(response.data || []);
        setError(null);
      } else {
        throw new Error(response.message || '获取书籍数据失败');
      }
    } catch (err) {
      console.error('获取书籍失败:', err);
      setError('获取书籍数据失败，请稍后再试');
      // 使用src/data.js中的书籍数据作为备份
      import('../data').then(({ books }) => {
        setDisplayBooks(books);
      });
    } finally {
      setLoading(false);
    }
  };

  // 搜索处理函数
  const handleSearch = async (value) => {
    if (!value.trim()) {
      fetchBooks(); // 如果搜索为空，获取所有书籍
      return;
    }

    try {
      setLoading(true);
      const response = await bookApi.searchBooks(value);
      
      if (response && response.success) {
        setDisplayBooks(response.data || []);
        setError(null);
      } else {
        throw new Error(response.message || '搜索书籍失败');
      }
    } catch (err) {
      console.error('搜索书籍失败:', err);
      setError('搜索书籍失败，请稍后再试');
      
      // 使用前端过滤作为备份
      import('../data').then(({ books }) => {
        const filtered = books.filter(book =>
          book.title.toLowerCase().includes(value.toLowerCase()) ||
          book.author.toLowerCase().includes(value.toLowerCase())
        );
        setDisplayBooks(filtered);
      });
    } finally {
      setLoading(false);
    }
  };

  // // 微服务作者查询（支持从输入参数触发）
  // const handleAuthorLookup = async (valueFromInput) => {
  //   const title = (typeof valueFromInput === 'string' ? valueFromInput : searchText || '').trim();
  //   if (!title) {
  //     message.warning('请输入书名后再点击“查询作者(微服务)”');
  //     return;
  //   }
  //   try {
  //     const resp = await externalApi.authorLookup(title);
  //     if (resp && resp.success && resp.data && resp.data.author) {
  //       Modal.info({
  //         title: '作者查询',
  //         content: `本书作者为 ${resp.data.author}`,
  //         okText: '知道了'
  //       });
  //     } else {
  //       Modal.error({
  //         title: '作者查询',
  //         content: '查询失败',
  //         okText: '关闭'
  //       });
  //     }
  //   } catch (e) {
  //     Modal.error({
  //       title: '作者查询',
  //       content: '查询失败',
  //       okText: '关闭'
  //     });
  //   }
  // };

  return (
    <div style={{ padding: '20px' }}>
      <div style={{ marginBottom: '24px' }}>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center', flexWrap: 'wrap' }}>
          <Search
            placeholder="输入书名或作者搜索"
            allowClear
            enterButton="搜索"
            size="large"
            onChange={(e) => setSearchText(e.target.value)}
            onSearch={handleSearch}
            style={{ maxWidth: '500px' }}
            loading={loading}
          />
          {/*<Search*/}
          {/*  placeholder="输入书名，查询作者(微服务)"*/}
          {/*  allowClear*/}
          {/*  enterButton="查询作者(微服务)"*/}
          {/*  size="large"*/}
          {/*  onSearch={(val) => handleAuthorLookup(val)}*/}
          {/*  style={{ maxWidth: '420px' }}*/}
          {/*/>*/}
        </div>
      </div>

      <h2 style={{ marginBottom: '24px' }}>热门书籍</h2>
      
      {error && (
        <Alert
          message="错误"
          description={error}
          type="error"
          showIcon
          style={{ marginBottom: '24px' }}
        />
      )}
      
      {loading ? (
        <div style={{ textAlign: 'center', margin: '50px 0' }}>
          <Spin size="large" />
        </div>
      ) : (
        <Row gutter={[24, 24]}>
          {displayBooks.length > 0 ? (
            displayBooks.map(book => (
              <Col xs={24} sm={12} md={8} lg={6} key={book.id}>
                <BookCard book={book} />
              </Col>
            ))
          ) : (
            <Col span={24}>
              <Alert
                message="没有找到相关书籍"
                type="info"
                showIcon
              />
            </Col>
          )}
        </Row>
      )}
    </div>
  );
}
