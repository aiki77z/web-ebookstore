package com.ebookstore.service.impl;

import com.ebookstore.dto.BookDTO;
import com.ebookstore.entity.Book;
import com.ebookstore.repository.BookRepository;
import com.ebookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
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
    }
    
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
    
    @Override
    public List<Book> searchBooksForAdmin(String query) {
        return bookRepository.searchBooks(query);
    }
    
    private BookDTO convertToDTO(Book book) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getDescription(),
                book.getCover(),
                book.getStatus()
        );
    }
} 