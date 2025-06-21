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
    
    // 普通用户功能
    List<BookDTO> getAllBooks();
    
    BookDTO getBookById(Long id);
    
    List<BookDTO> searchBooks(String query);
    
    // 管理员功能
    Book saveBook(Book book);
    
    void deleteBook(Long id);
    
    Page<Book> getAllBooksForAdmin(Pageable pageable);
    
    Book getBookEntityById(Long id);
    
    List<Book> searchBooksForAdmin(String query);
    
    // 库存管理方法
    boolean updateStock(Long bookId, Integer quantity);
    
    boolean reduceStock(Long bookId, Integer quantity);
    
    boolean checkStock(Long bookId, Integer quantity);
} 