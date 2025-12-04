package com.ebookstore.service;

import com.ebookstore.entity.neo4j.Tag;

import java.util.List;
import java.util.Set;

/**
 * 标签服务接口
 */
public interface TagService {
    
    /**
     * 初始化标签关系图
     * 创建常见的图书分类标签及其关系
     */
    void initializeTagGraph();
    
    /**
     * 创建标签
     */
    Tag createTag(String name, String description);
    
    /**
     * 建立标签之间的父子关系（子标签是父标签的细分）
     * @param childTagName 子标签名称
     * @param parentTagName 父标签名称
     */
    void createTagRelationship(String childTagName, String parentTagName);
    
    /**
     * 根据标签名称查找标签及其2度关联的所有标签
     */
    Set<String> findRelatedTagNames(String tagName);
    
    /**
     * 根据多个标签名称查找所有相关标签（包括2度关联）
     */
    Set<String> findRelatedTagNames(List<String> tagNames);
    
    /**
     * 获取所有标签
     */
    List<Tag> getAllTags();
    
    /**
     * 根据名称查找标签
     */
    Tag findTagByName(String name);
}

