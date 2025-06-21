package com.ebookstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//主键自增
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(nullable = false, length = 100)
    private String author;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(length = 1000)
    private String description;
    
    @Column(length = 255)
    private String cover;
    
    @Column(length = 20)
    private String status;//AVAILABLE/OUT_OF_STOCK
    
    @Column(nullable = false)
    private Integer stock = 100; // 库存量，默认100本
    
    @Column(length = 50)
    private String isbn; // ISBN编号
} 