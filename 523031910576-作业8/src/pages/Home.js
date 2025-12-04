import React, { useState, useEffect } from 'react';
import { Row, Col, Input, Spin, Alert, Tag, Space, Select, Button, message } from 'antd';
import { SearchOutlined, ClearOutlined, ReloadOutlined } from '@ant-design/icons';
import BookCard from '../components/BookCard';
import { bookApi, tagApi } from '../services/api';

const { Search } = Input;
const { Option } = Select;

export default function Home() {
  const [searchText, setSearchText] = useState('');
  const [displayBooks, setDisplayBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [tags, setTags] = useState([]);
  const [selectedTag, setSelectedTag] = useState(null);

  // 初始化加载书籍数据和标签
  useEffect(() => {
    fetchBooks();
    fetchTags();
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

  // 获取标签列表
  const fetchTags = async () => {
    try {
      const response = await tagApi.getAllTags();
      console.log('标签API响应:', response);
      
      if (response && response.success) {
        const tagList = response.data || [];
        console.log('获取到的标签列表:', tagList);
        
        // 确保标签数据格式正确
        if (Array.isArray(tagList) && tagList.length > 0) {
          setTags(tagList);
        } else {
          console.warn('标签列表为空或格式不正确');
          setTags([]);
        }
      } else {
        console.error('获取标签失败:', response?.message || '未知错误');
        setTags([]);
      }
    } catch (err) {
      console.error('获取标签失败:', err);
      setTags([]);
    }
  };

  // 搜索处理函数
  const handleSearch = async (value) => {
    if (!value.trim()) {
      fetchBooks(); // 如果搜索为空，获取所有书籍
      setSelectedTag(null);
      return;
    }

    try {
      setLoading(true);
      setSelectedTag(null);
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

  // 根据标签搜索处理函数
  const handleTagSearch = async (tagName) => {
    if (!tagName || !tagName.trim()) {
      return;
    }
    
    try {
      setLoading(true);
      setSelectedTag(tagName);
      setSearchText('');
      setError(null);
      
      console.log('开始按标签搜索:', tagName);
      const response = await bookApi.searchBooksByTag(tagName);
      console.log('标签搜索响应:', response);
      
      if (response && response.success) {
        setDisplayBooks(response.data || []);
        setError(null);
        console.log('找到书籍数量:', response.data?.length || 0);
      } else {
        throw new Error(response?.message || '根据标签搜索书籍失败');
      }
    } catch (err) {
      console.error('根据标签搜索书籍失败:', err);
      setError(`根据标签"${tagName}"搜索书籍失败: ${err.message || '请稍后再试'}`);
      setDisplayBooks([]);
    } finally {
      setLoading(false);
    }
  };

  // 清除标签选择
  const handleClearTag = () => {
    setSelectedTag(null);
    fetchBooks();
  };

  // 初始化标签图
  const handleInitializeTags = async () => {
    try {
      setLoading(true);
      const response = await tagApi.initializeTags();
      
      if (response && response.success) {
        message.success('标签图初始化成功！');
        // 重新获取标签列表
        await fetchTags();
      } else {
        message.error(response?.message || '标签图初始化失败');
      }
    } catch (err) {
      console.error('初始化标签图失败:', err);
      message.error('初始化标签图失败，请检查 Neo4J 连接');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <div style={{ marginBottom: '24px' }}>
        <Search
          placeholder="输入书名或作者搜索"
          allowClear
          enterButton="搜索"
          size="large"
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          onSearch={handleSearch}
          style={{ maxWidth: '500px' }}
          loading={loading}
        />
      </div>

      {/* 标签选择区域 */}
      <div style={{ marginBottom: '24px' }}>
        <div style={{ marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '12px', flexWrap: 'wrap' }}>
          <span style={{ fontWeight: 'bold', fontSize: '16px' }}>按标签搜索：</span>
          <Select
            placeholder={tags.length > 0 ? "选择标签进行搜索" : "暂无可用标签"}
            style={{ minWidth: '200px' }}
            size="large"
            value={selectedTag || undefined}
            onChange={(value) => {
              if (value) {
                handleTagSearch(value);
              } else {
                handleClearTag();
              }
            }}
            allowClear
            showSearch
            disabled={tags.length === 0}
            filterOption={(input, option) =>
              (option?.children ?? '').toLowerCase().includes(input.toLowerCase())
            }
            notFoundContent={tags.length === 0 ? "暂无标签数据，请先初始化标签图" : "未找到匹配的标签"}
          >
            {tags.map(tag => {
              // 处理标签对象，可能是 {name: "xxx"} 格式或直接是字符串
              const tagName = typeof tag === 'string' ? tag : (tag?.name || tag);
              return (
                <Option key={tagName} value={tagName}>
                  {tagName}
                </Option>
              );
            })}
          </Select>
          {selectedTag && (
            <Button
              icon={<ClearOutlined />}
              onClick={handleClearTag}
              size="large"
            >
              清除标签筛选
            </Button>
          )}
        </div>
        
        {/* 热门标签快速选择 */}
        {tags.length > 0 ? (
          <div style={{ marginTop: '12px' }}>
            <span style={{ marginRight: '8px', color: '#666' }}>热门标签：</span>
            <Space size={[8, 8]} wrap>
              {tags.slice(0, 15).map(tag => {
                // 处理标签对象，可能是 {name: "xxx"} 格式或直接是字符串
                const tagName = typeof tag === 'string' ? tag : (tag?.name || tag);
                return (
                  <Tag
                    key={tagName}
                    color={selectedTag === tagName ? 'blue' : 'default'}
                    style={{ 
                      cursor: 'pointer', 
                      fontSize: '14px', 
                      padding: '4px 12px',
                      marginBottom: '4px',
                      transition: 'all 0.3s'
                    }}
                    onClick={() => handleTagSearch(tagName)}
                  >
                    {tagName}
                  </Tag>
                );
              })}
            </Space>
          </div>
        ) : (
          <Alert
            message="暂无标签数据"
            description={
              <div>
                <p>标签数据尚未初始化。请执行以下操作：</p>
                <ol style={{ marginTop: '8px', paddingLeft: '20px', marginBottom: '12px' }}>
                  <li>确保 Neo4J 数据库已启动并运行（在 Neo4J Desktop 中启动）</li>
                  <li>点击下方按钮初始化标签图</li>
                  <li>或者等待应用启动时自动初始化（如果 Neo4J 已连接）</li>
                </ol>
                <Button
                  type="primary"
                  icon={<ReloadOutlined />}
                  onClick={handleInitializeTags}
                  loading={loading}
                >
                  初始化标签图
                </Button>
              </div>
            }
            type="warning"
            showIcon
            style={{ marginTop: '12px' }}
          />
        )}
      </div>

      <h2 style={{ marginBottom: '24px' }}>
        {selectedTag ? `标签 "${selectedTag}" 的搜索结果` : '热门书籍'}
      </h2>
      
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
