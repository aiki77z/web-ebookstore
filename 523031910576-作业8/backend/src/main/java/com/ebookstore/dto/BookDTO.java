package com.ebookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 书籍数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    
    private Long id;
    
    @NotBlank(message = "书籍标题不能为空")
    private String title;
    
    @NotBlank(message = "作者不能为空")
    private String author;
    
    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    private BigDecimal price;
    
    private String description;
    private String cover;
    private String status;
    
    @NotNull(message = "库存量不能为空")
    @Positive(message = "库存量必须大于0")
    private Integer stock;
    
    private String isbn;
    
    private String tags; // 标签列表，用逗号分隔
    
    private Boolean deleted; // 软删除标记
} 