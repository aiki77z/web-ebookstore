// 手动创建标签关系的 Cypher 脚本
// 在 Neo4J Browser 中执行此脚本

// 建立 Fiction 的子分类关系
MATCH (child:Tag {name: "Science Fiction"}), (parent:Tag {name: "Fiction"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Fantasy"}), (parent:Tag {name: "Fiction"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Mystery"}), (parent:Tag {name: "Fiction"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Romance"}), (parent:Tag {name: "Fiction"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Thriller"}), (parent:Tag {name: "Fiction"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Adventure"}), (parent:Tag {name: "Fiction"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

// 建立 Technology 的子分类关系
MATCH (child:Tag {name: "Programming"}), (parent:Tag {name: "Technology"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Web Development"}), (parent:Tag {name: "Technology"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Data Science"}), (parent:Tag {name: "Technology"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Artificial Intelligence"}), (parent:Tag {name: "Technology"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Cybersecurity"}), (parent:Tag {name: "Technology"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

// 建立 Business 的子分类关系
MATCH (child:Tag {name: "Management"}), (parent:Tag {name: "Business"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Marketing"}), (parent:Tag {name: "Business"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Finance"}), (parent:Tag {name: "Business"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Entrepreneurship"}), (parent:Tag {name: "Business"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

// 建立 Science 的子分类关系
MATCH (child:Tag {name: "Physics"}), (parent:Tag {name: "Science"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Chemistry"}), (parent:Tag {name: "Science"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Biology"}), (parent:Tag {name: "Science"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Mathematics"}), (parent:Tag {name: "Science"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

// 建立更深层的关系
MATCH (child:Tag {name: "Web Development"}), (parent:Tag {name: "Programming"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Data Science"}), (parent:Tag {name: "Programming"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Artificial Intelligence"}), (parent:Tag {name: "Data Science"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

