package com.ebookstore.controller;

import com.ebookstore.dto.BookDTO;
import com.ebookstore.service.BookService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BookGraphQLController {

    private final BookService bookService;

    public BookGraphQLController(BookService bookService) {
        this.bookService = bookService;
    }

    @QueryMapping
    public List<BookDTO> searchBooksByTitle(@Argument String title) {
        return bookService.searchBooks(title);
    }
}

