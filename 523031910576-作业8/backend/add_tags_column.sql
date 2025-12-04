-- 为books表添加tags字段
-- 用于存储图书标签，多个标签用逗号分隔

ALTER TABLE books 
ADD COLUMN tags VARCHAR(500) NULL 
COMMENT '标签列表，用逗号分隔，例如：AFiction,Science Fiction,Adventure'
AFTER isbn;

-- 示例：为现有书籍添加标签（可选）
-- UPDATE books SET tags = 'Fiction,Adventure' WHERE id = 1;
-- UPDATE books SET tags = 'Technology,Programming' WHERE id = 2;
-- UPDATE books SET tags = 'Science,Physics' WHERE id = 3;

