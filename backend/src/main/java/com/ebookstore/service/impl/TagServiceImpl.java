package com.ebookstore.service.impl;

import com.ebookstore.entity.neo4j.Tag;
import com.ebookstore.repository.neo4j.TagRepository;
import com.ebookstore.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 */
@Service
public class TagServiceImpl implements TagService {
    
    @Autowired
    private TagRepository tagRepository;
    
    @Autowired
    private Neo4jClient neo4jClient;

    
    @Override
    @Transactional
    public void createTagRelationship(String childTagName, String parentTagName) {
        try {
            // 验证标签是否存在
            Tag child = tagRepository.findByName(childTagName).orElse(null);
            Tag parent = tagRepository.findByName(parentTagName).orElse(null);
            
            if (child == null) {
                System.err.println("警告: 找不到子标签: " + childTagName + "，跳过关系创建");
                return;
            }
            if (parent == null) {
                System.err.println("警告: 找不到父标签: " + parentTagName + "，跳过关系创建");
                return;
            }
            
            // 使用Neo4jClient直接执行Cypher查询创建关系（最可靠的方式）
            try {
                // 直接执行MERGE查询创建关系，MERGE会自动处理重复
                String cypher = "MATCH (child:Tag {name: $childName}), (parent:Tag {name: $parentName}) " +
                               "MERGE (child)-[:SUBCATEGORY_OF]->(parent)";
                
                neo4jClient.query(cypher)
                    .bind(childTagName).to("childName")
                    .bind(parentTagName).to("parentName")
                    .run();
                
                System.out.println("建立关系: " + childTagName + " -> " + parentTagName);
            } catch (Exception e) {
                // 捕获所有异常，记录但不中断
                String errorMsg = e.getMessage();
                System.err.println("建立关系失败: " + childTagName + " -> " + parentTagName + 
                                 (errorMsg != null ? " - " + errorMsg.substring(0, Math.min(100, errorMsg.length())) : ""));
                // 不抛出异常，继续处理其他关系
            }
            
        } catch (Exception e) {
            System.err.println("建立关系时出错: " + childTagName + " -> " + parentTagName + ", 错误: " + 
                             (e.getMessage() != null ? e.getMessage().substring(0, Math.min(100, e.getMessage().length())) : "未知错误"));
            // 不抛出异常，继续处理其他关系
        }
    }
    
    @Override
    public Set<String> findRelatedTagNames(String tagName) {
        List<Tag> relatedTags = tagRepository.findTagsWithinTwoHops(tagName);
        Set<String> tagNames = new HashSet<>();
        relatedTags.forEach(tag -> {
            if (tag != null && tag.getName() != null) {
                tagNames.add(tag.getName());
            }
        });
        return tagNames;
    }
    
    @Override
    public Set<String> findRelatedTagNames(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }
        
        Set<String> allRelatedTags = new HashSet<>(tagNames);
        List<Tag> relatedTags = tagRepository.findTagsWithinTwoHopsFromMultiple(tagNames);
        relatedTags.forEach(tag -> {
            if (tag != null && tag.getName() != null) {
                allRelatedTags.add(tag.getName());
            }
        });
        return allRelatedTags;
    }
    
    @Override
    public List<Tag> getAllTags() {
        try {
            // 使用自定义查询直接获取标签名称和描述，避免加载关系导致循环引用
            String cypher = "MATCH (t:Tag) RETURN t.name as name, COALESCE(t.description, '') as description ORDER BY t.name";
            
            List<Tag> tags = new ArrayList<>();
            neo4jClient.query(cypher)
                    .fetch()
                    .all()
                    .forEach(record -> {
                        Object nameObj = record.get("name");
                        Object descObj = record.get("description");
                        String name = nameObj != null ? nameObj.toString() : "";
                        String description = descObj != null ? descObj.toString() : "";
                        Tag tag = new Tag(name, description);
                        tags.add(tag);
                    });
            
            System.out.println("成功获取 " + tags.size() + " 个标签（不包含关系）");
            return tags;
        } catch (Exception e) {
            System.err.println("获取标签列表失败: " + e.getMessage());
            e.printStackTrace();
            // 返回空列表而不是抛出异常
            return new ArrayList<>();
        }
    }
    
    @Override
    public Tag findTagByName(String name) {
        return tagRepository.findByName(name).orElse(null);
    }
}

