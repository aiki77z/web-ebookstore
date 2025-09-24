package com.ebookstore.service.impl;

import com.ebookstore.dto.BookDTO;
import com.ebookstore.entity.Book;
import com.ebookstore.repository.BookRepository;
import com.ebookstore.service.BookService;
import com.ebookstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 书籍服务实现类
 * 使用Spring依赖注入
 */
@Service
public class BookServiceImpl implements BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private CartService cartService;
    
    // 普通用户功能 - 只显示未删除的书籍
    
    @Override
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAllAvailable();
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
        return convertToDTO(book);
    }
    
    @Override
    public List<BookDTO> searchBooks(String query) {
        List<Book> books = bookRepository.searchAvailableBooks(query);
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 管理员功能
    
    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
    
    @Override
    @Transactional
    public boolean softDeleteBook(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
            
            // 软删除书籍
            book.setDeleted(true);
            bookRepository.save(book);
            
            // 清理所有用户购物车中的该书籍
            int cleanedCount = cartService.cleanCartByBookId(id);
            System.out.println("书籍《" + book.getTitle() + "》已软删除，同时清理了 " + cleanedCount + " 个购物车项");
            
            return true;
        } catch (Exception e) {
            System.err.println("软删除书籍失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean restoreBook(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
            book.setDeleted(false);
            bookRepository.save(book);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public Page<Book> getAllBooksForAdmin(Pageable pageable) {
        return bookRepository.findAllForAdmin(pageable);
    }
    
    @Override
    public Book getBookEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
    }
    
    @Override
    public Book getAvailableBookById(Long id) {
        return bookRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
    }
    
    @Override
    public List<Book> searchBooksForAdmin(String query) {
        return bookRepository.searchBooks(query);
    }
    
    // 库存管理方法 - 只能操作未删除的书籍
    
    @Override
    @Transactional
    public boolean updateStock(Long bookId, Integer quantity) {
        try {
            Book book = getAvailableBookById(bookId);
            book.setStock(quantity);
            
            // 根据库存量更新状态
            if (quantity > 0) {
                book.setStatus("AVAILABLE");
            } else {
                book.setStatus("OUT_OF_STOCK");
            }
            
            bookRepository.save(book);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean reduceStock(Long bookId, Integer quantity) {
        try {
            Book book = getAvailableBookById(bookId);
            
            if (book.getStock() < quantity) {
                return false; // 库存不足
            }
            
            book.setStock(book.getStock() - quantity);
            
            // 根据库存量更新状态
            if (book.getStock() == 0) {
                book.setStatus("OUT_OF_STOCK");
            }
            
            bookRepository.save(book);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean checkStock(Long bookId, Integer quantity) {
        try {
            Book book = getAvailableBookById(bookId);
            return book.getStock() >= quantity;
        } catch (Exception e) {
            return false;
        }
    }
    
    // 统计和订单相关方法 - 保持历史数据完整性
    
    @Override
    public List<Book> getBooksByIds(List<Long> ids) {
        return bookRepository.findByIdIn(ids);
    }
    
    // 私有辅助方法
    
    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPrice(book.getPrice());
        dto.setDescription(book.getDescription());
        dto.setCover(book.getCover());
        dto.setStatus(book.getStatus());
        dto.setStock(book.getStock());
        dto.setIsbn(book.getIsbn());
        dto.setDeleted(book.getDeleted());
        return dto;
    }
} 