package com.ebookstore.entity.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * MongoDB中存储的书籍详细信息
 * 包含description、cover、reviews等非结构化数据
 */
@Document(collection = "book_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDetail {
    
    @Id
    private String id;
    
    private Long bookId; // 关联MySQL中的Book.id
    
    private String description; // 书籍描述
    
    private String cover; // 封面图片URL或Base64
    
    private List<String> reviews; // 书评列表
    
    private String content; // 书籍内容介绍（扩展字段）
    
    private String publisher; // 出版社信息
    
    private String publishDate; // 出版日期
    
    private Integer pages; // 页数
    
    private String language; // 语言
}

