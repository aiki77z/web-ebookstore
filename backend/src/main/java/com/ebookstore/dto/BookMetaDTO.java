package com.ebookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookMetaDTO implements Serializable {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private String description;
    private String cover;
    private String status;
    private String isbn;
    private Boolean deleted;
}



