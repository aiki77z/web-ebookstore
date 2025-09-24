-- 为books表添加软删除功能的数据库迁移脚本
-- 执行前请备份数据库

USE ebookstorehw1;

-- 添加deleted字段（软删除标记）
ALTER TABLE books ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '软删除标记，true表示已删除';

-- 添加时间戳字段
ALTER TABLE books ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
ALTER TABLE books ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 为deleted字段创建索引，提高查询性能
CREATE INDEX idx_books_deleted ON books(deleted);

-- 为了确保现有数据的完整性，将现有书籍的deleted设置为false
UPDATE books SET deleted = FALSE WHERE deleted IS NULL;

-- 验证结果
SELECT COUNT(*) as total_books, 
       COUNT(CASE WHEN deleted = FALSE THEN 1 END) as available_books,
       COUNT(CASE WHEN deleted = TRUE THEN 1 END) as deleted_books
FROM books;

-- 显示表结构确认修改成功
DESCRIBE books; 