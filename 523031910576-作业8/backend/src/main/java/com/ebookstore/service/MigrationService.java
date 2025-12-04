package com.ebookstore.service;

/**
 * 数据迁移服务接口
 */
public interface MigrationService {
    
    /**
     * 将MySQL中现有书籍的description和cover迁移到MongoDB
     * @return 迁移的书籍数量
     */
    int migrateBooksToMongoDB();
    
    /**
     * 为现有书籍批量添加示例标签（用于测试）
     */
    void addSampleTagsToBooks();
}

