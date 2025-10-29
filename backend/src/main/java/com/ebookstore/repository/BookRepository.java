package com.ebookstore.repository;

import com.ebookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // 用户端查询方法 - 排除已删除的书籍
    
    @Query("SELECT b FROM Book b WHERE b.deleted = false")
    List<Book> findAllAvailable();
    
    @Query("SELECT b FROM Book b WHERE b.deleted = false AND b.id = :id")
    Optional<Book> findByIdAndNotDeleted(@Param("id") Long id);
    
    @Query("SELECT b FROM Book b WHERE b.deleted = false AND (lower(b.title) LIKE lower(concat('%', :query, '%')) OR lower(b.author) LIKE lower(concat('%', :query, '%')))")
    List<Book> searchAvailableBooks(@Param("query") String query);
    
    @Query("SELECT b FROM Book b WHERE b.deleted = false")
    Page<Book> findAllAvailable(Pageable pageable);
    
    // 管理员端查询方法 - 可以查看已删除的书籍
    
    @Query("SELECT b FROM Book b WHERE lower(b.title) LIKE lower(concat('%', :query, '%')) OR lower(b.author) LIKE lower(concat('%', :query, '%'))")
    List<Book> searchBooks(@Param("query") String query);
    
    @Query("SELECT b FROM Book b")
    Page<Book> findAllForAdmin(Pageable pageable);
    
    // 统计和订单相关查询 - 包含已删除的书籍（用于保持历史数据完整性）
    
    @Query("SELECT b FROM Book b WHERE b.id IN :ids")
    List<Book> findByIdIn(@Param("ids") List<Long> ids);

    // 注意：库存已迁移至 book_inventory 表，相关查询请使用 BookInventoryRepository
}
//使用@Query注解来定义自定义查询