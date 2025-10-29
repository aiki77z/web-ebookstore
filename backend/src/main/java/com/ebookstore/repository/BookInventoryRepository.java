package com.ebookstore.repository;

import com.ebookstore.entity.BookInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookInventoryRepository extends JpaRepository<BookInventory, Long> {

    @Query("SELECT bi.stock FROM BookInventory bi WHERE bi.bookId = :bookId")
    Integer getStock(@Param("bookId") Long bookId);

    @Modifying
    @Query("UPDATE BookInventory bi SET bi.stock = :stock, bi.updatedAt = CURRENT_TIMESTAMP WHERE bi.bookId = :bookId")
    int updateStock(@Param("bookId") Long bookId, @Param("stock") Integer stock);
}


