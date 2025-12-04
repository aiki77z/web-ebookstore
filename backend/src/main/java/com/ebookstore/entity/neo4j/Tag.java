package com.ebookstore.entity.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Neo4J中的标签节点
 */
@Node("Tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    
    @Id
    private String name; // 标签名称作为ID
    
    private String description; // 标签描述
    
    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.OUTGOING)
    private Set<Tag> parentTags = new HashSet<>();
    
    @Relationship(type = "SUBCATEGORY_OF", direction = Relationship.Direction.INCOMING)
    private Set<Tag> childTags = new HashSet<>();
    
    public Tag(String name) {
        this.name = name;
    }
    
    public Tag(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

