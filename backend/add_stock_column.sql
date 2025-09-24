-- 为书籍表添加库存量和ISBN字段
USE ebookstorehw1;

-- 添加库存量字段，默认值为100
ALTER TABLE books ADD COLUMN stock INT NOT NULL DEFAULT 100 COMMENT '库存量';

-- 添加ISBN字段
ALTER TABLE books ADD COLUMN isbn VARCHAR(50) COMMENT 'ISBN编号';

-- 为现有书籍更新ISBN（示例数据）
UPDATE books SET isbn = '9787020024759' WHERE id = 1; -- 藤井 風钢琴谱
UPDATE books SET isbn = '9787536692930' WHERE id = 2; -- 三体
UPDATE books SET isbn = '9784088742588' WHERE id = 3; -- JOJO的奇妙冒险
UPDATE books SET isbn = '9788954655927' WHERE id = 4; -- 素食者
UPDATE books SET isbn = '9787020139316' WHERE id = 5; -- 哈利波特
UPDATE books SET isbn = '9787532761968' WHERE id = 6; -- 长日将尽
UPDATE books SET isbn = '9787559642868' WHERE id = 7; -- 孤独星球 埃及
UPDATE books SET isbn = '9787532761975' WHERE id = 8; -- 不能承受的生命之轻

-- 创建库存量索引以提高查询性能
CREATE INDEX idx_books_stock ON books(stock);

-- 查看更新结果
SELECT id, title, stock, isbn FROM books; 