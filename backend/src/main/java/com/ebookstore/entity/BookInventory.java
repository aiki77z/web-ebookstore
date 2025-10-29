package com.ebookstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookInventory {

    @Id
    @Column(name = "book_id")
    private Long bookId;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}



