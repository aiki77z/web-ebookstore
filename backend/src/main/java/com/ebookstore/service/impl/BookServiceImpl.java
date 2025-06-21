package com.ebookstore.service.impl;

import com.ebookstore.dto.BookDTO;
import com.ebookstore.entity.Book;
import com.ebookstore.repository.BookRepository;
import com.ebookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    
    @Autowired
    private BookRepository bookRepository;//使用BookRepository来进行数据访问
    
    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
        return convertToDTO(book);
    }
    
    @Override
    public List<BookDTO> searchBooks(String query) {
        return bookRepository.searchBooks(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 管理员功能实现
    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }//用repository保存book实体
    
    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("未找到ID为 " + id + " 的书籍");
        }
        bookRepository.deleteById(id);
    }
    
    @Override
    public Page<Book> getAllBooksForAdmin(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
    
    @Override
    public Book getBookEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
    }
    
    @Override//支持管理员查看所有图书 使用分页功能
    public List<Book> searchBooksForAdmin(String query) {
        return bookRepository.searchBooks(query);
    }
    
    // 实现库存管理方法
    @Override
    @Transactional
    public boolean updateStock(Long bookId, Integer quantity) {
        try {
            Book book = getBookEntityById(bookId);
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
            Book book = getBookEntityById(bookId);
            
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
            Book book = getBookEntityById(bookId);
            return book.getStock() >= quantity;
        } catch (Exception e) {
            return false;
        }
    }
    
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
        return dto;
    }
} 