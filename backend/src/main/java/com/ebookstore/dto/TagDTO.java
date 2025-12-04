package com.ebookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签数据传输对象
 * 用于API返回，避免循环引用问题
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {
    
    private String name; // 标签名称
    
    private String description; // 标签描述
    
    public TagDTO(String name) {
        this.name = name;
    }
}

