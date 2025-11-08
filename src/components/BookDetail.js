import React from 'react';
import { Card, Button, Descriptions, message } from 'antd';
import { externalApi } from '../services/api';

export default function BookDetail({ book }) {
    const handleQueryAuthor = async () => {
        try {
            const res = await externalApi.authorLookup(book.title);
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
        <Card
            title={book.title}
            extra={[
                <Button key="buy" type="primary" style={{ marginRight: 8 }}>
                    立即购买
                </Button>,
                <Button key="cart" style={{ marginRight: 8 }}>加入购物车</Button>,
                <Button key="author" onClick={handleQueryAuthor}>根据书名查作者</Button>
            ]}
        >
            <Descriptions column={1}>
                <Descriptions.Item label="作者">{book.author}</Descriptions.Item>
                <Descriptions.Item label="价格">￥{book.price}</Descriptions.Item>
                <Descriptions.Item label="描述">{book.description}</Descriptions.Item>
            </Descriptions>
        </Card>
    );
}