package com.ebookstore.repository.mongodb;

import com.ebookstore.entity.mongodb.BookDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * MongoDB书籍详情Repository
 */
@Repository
public interface BookDetailRepository extends MongoRepository<BookDetail, String> {
    
    /**
     * 根据bookId查找书籍详情
     */
    Optional<BookDetail> findByBookId(Long bookId);
    
    /**
     * 根据bookId删除书籍详情
     */
    void deleteByBookId(Long bookId);
}

