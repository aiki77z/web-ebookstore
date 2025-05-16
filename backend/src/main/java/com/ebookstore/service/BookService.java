package com.ebookstore.service;

import com.ebookstore.dto.BookDTO;

import java.util.List;

public interface BookService {
    
    List<BookDTO> getAllBooks();
    
    BookDTO getBookById(Long id);
    
    List<BookDTO> searchBooks(String query);
} 