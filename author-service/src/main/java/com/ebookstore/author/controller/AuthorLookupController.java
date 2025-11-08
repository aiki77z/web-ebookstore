package com.ebookstore.author.controller;

import com.ebookstore.author.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/author")
public class AuthorLookupController {
    private final BookRepository bookRepository;

    public AuthorLookupController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/lookup")
    public ResponseEntity<Map<String, Object>> findAuthorByTitle(@RequestParam("title") String title) {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 先合并两种查询（优先未删除），再统一 map，避免 Optional 类型不匹配
            Optional<com.ebookstore.author.entity.Book> bookOpt =
                    bookRepository.findFirstByTitleAndDeletedFalse(title)
                            .or(() -> bookRepository.findFirstByTitle(title));

            return bookOpt
                    .map(book -> {
                        resp.put("success", true);
                        Map<String, Object> data = new HashMap<>();
                        data.put("title", book.getTitle());
                        data.put("author", book.getAuthor());
                        resp.put("data", data);
                        return ResponseEntity.ok(resp);
                    })
                    .orElseGet(() -> {
                        resp.put("success", false);
                        resp.put("message", "未找到匹配书名");
                        return ResponseEntity.ok(resp);
                    });
        } catch (Exception ex) {
            resp.put("success", false);
            resp.put("message", "查询异常: " + ex.getMessage());
            return ResponseEntity.ok(resp);
        }
    }
}


