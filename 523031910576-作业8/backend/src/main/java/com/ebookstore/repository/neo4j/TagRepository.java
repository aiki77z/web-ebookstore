package com.ebookstore.repository.neo4j;

import com.ebookstore.entity.neo4j.Tag;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Neo4J标签Repository
 */
@Repository
public interface TagRepository extends Neo4jRepository<Tag, String> {
    
    /**
     * 根据名称查找标签
     */
    Optional<Tag> findByName(String name);
    
    /**
     * 查找所有标签
     */
    List<Tag> findAll();
    
    /**
     * 查找与指定标签通过SUBCATEGORY_OF关系直接关联的所有标签（1度）
     * 包括父标签和子标签
     */
    @Query("MATCH (t:Tag {name: $tagName}) " +
           "OPTIONAL MATCH (t)-[:SUBCATEGORY_OF]->(parent:Tag) " +
           "OPTIONAL MATCH (t)<-[:SUBCATEGORY_OF]-(child:Tag) " +
           "RETURN DISTINCT parent, child")
    List<Tag> findDirectlyRelatedTags(@Param("tagName") String tagName);
    
    /**
     * 查找与指定标签通过2次SUBCATEGORY_OF关系可以关联到的所有标签（2度）
     * 包括：起始标签本身 + 直接关联的标签 + 这些标签的关联标签
     */
    @Query("MATCH (t:Tag {name: $tagName}) " +
           "OPTIONAL MATCH path = (t)-[:SUBCATEGORY_OF*1..2]-(related:Tag) " +
           "WITH collect(DISTINCT related) as relatedTags, t " +
           "WITH relatedTags + [t] as allTags " +
           "UNWIND allTags as tag " +
           "WITH tag " +
           "WHERE tag IS NOT NULL " +
           "RETURN DISTINCT tag")
    List<Tag> findTagsWithinTwoHops(@Param("tagName") String tagName);
    
    /**
     * 查找多个标签及其2度关联的所有标签
     */
    @Query("MATCH (t:Tag) " +
           "WHERE t.name IN $tagNames " +
           "OPTIONAL MATCH path = (t)-[:SUBCATEGORY_OF*1..2]-(related:Tag) " +
           "WITH collect(DISTINCT related) as relatedTags, collect(DISTINCT t) as startTags " +
           "WITH relatedTags + startTags as allTags " +
           "UNWIND allTags as tag " +
           "WITH tag " +
           "WHERE tag IS NOT NULL " +
           "RETURN DISTINCT tag")
    List<Tag> findTagsWithinTwoHopsFromMultiple(@Param("tagNames") List<String> tagNames);
    
    /**
     * 检查标签是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查关系是否存在
     */
    @Query("MATCH (child:Tag {name: $childName})-[r:SUBCATEGORY_OF]->(parent:Tag {name: $parentName}) RETURN count(r) > 0 as exists")
    boolean relationshipExists(@Param("childName") String childName, @Param("parentName") String parentName);
}

