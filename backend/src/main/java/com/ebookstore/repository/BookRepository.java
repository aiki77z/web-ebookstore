package com.ebookstore.repository;

import com.ebookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    @Query("SELECT b FROM Book b WHERE lower(b.title) LIKE lower(concat('%', :query, '%')) OR lower(b.author) LIKE lower(concat('%', :query, '%'))")
    List<Book> searchBooks(@Param("query") String query);
}
//使用@Query注解来定义自定义查询