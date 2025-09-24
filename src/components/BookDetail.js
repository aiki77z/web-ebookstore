import React from 'react';
import { Card, Button, Descriptions } from 'antd';

export default function BookDetail({ book }) {
    return (
        <Card
            title={book.title}
            extra={[
                <Button key="buy" type="primary" style={{ marginRight: 8 }}>
                    立即购买
                </Button>,
                <Button key="cart">加入购物车</Button>
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