package com.ebookstore.service;

import com.ebookstore.dto.BookDTO;
import com.ebookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 书籍服务接口
 * 体现接口与实现分离的设计原则
 */
public interface BookService {
    
    // 普通用户功能 - 只能看到未删除的书籍
    List<BookDTO> getAllBooks();
    
    BookDTO getBookById(Long id);
    
    List<BookDTO> searchBooks(String query);
    
    // 根据标签搜索书籍（包括2度关联的标签）
    List<BookDTO> searchBooksByTag(String tagName);
    
    // 管理员功能
    Book saveBook(Book book);
    
    boolean softDeleteBook(Long id); // 软删除方法
    
    boolean restoreBook(Long id); // 恢复书籍方法
    
    Page<Book> getAllBooksForAdmin(Pageable pageable);
    
    Book getBookEntityById(Long id); // 管理员可以获取包括已删除的书籍
    
    Book getAvailableBookById(Long id); // 只获取未删除的书籍
    
    List<Book> searchBooksForAdmin(String query);
    
    // 库存管理方法
    boolean updateStock(Long bookId, Integer quantity);
    
    boolean reduceStock(Long bookId, Integer quantity);
    
    boolean checkStock(Long bookId, Integer quantity);
    
    // 统计和订单相关方法 - 保持历史数据完整性
    List<Book> getBooksByIds(List<Long> ids);
} 