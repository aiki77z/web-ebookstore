// ============================================
// Neo4J 标签图初始化脚本
// 使用方法：在 Neo4J Browser 中直接执行此脚本
// ============================================

// 清空现有数据（可选，如果需要重新初始化）
// MATCH (n:Tag) DETACH DELETE n;

// ============================================
// 1. 创建顶级分类标签
// ============================================

CREATE (t1:Tag {name: "Fiction", description: "小说类"})
CREATE (t2:Tag {name: "Non-Fiction", description: "非小说类"})
CREATE (t3:Tag {name: "Technology", description: "技术类"})
CREATE (t4:Tag {name: "Business", description: "商业类"})
CREATE (t5:Tag {name: "Science", description: "科学类"})
CREATE (t6:Tag {name: "History", description: "历史类"})
CREATE (t7:Tag {name: "Biography", description: "传记类"})

// ============================================
// 2. 创建 Fiction 的子分类
// ============================================

CREATE (t8:Tag {name: "Science Fiction", description: "科幻小说"})
CREATE (t9:Tag {name: "Fantasy", description: "奇幻小说"})
CREATE (t10:Tag {name: "Mystery", description: "悬疑小说"})
CREATE (t11:Tag {name: "Romance", description: "言情小说"})
CREATE (t12:Tag {name: "Thriller", description: "惊悚小说"})
CREATE (t13:Tag {name: "Adventure", description: "冒险小说"})

// ============================================
// 3. 创建 Technology 的子分类
// ============================================

CREATE (t14:Tag {name: "Programming", description: "编程技术"})
CREATE (t15:Tag {name: "Web Development", description: "Web开发"})
CREATE (t16:Tag {name: "Data Science", description: "数据科学"})
CREATE (t17:Tag {name: "Artificial Intelligence", description: "人工智能"})
CREATE (t18:Tag {name: "Cybersecurity", description: "网络安全"})

// ============================================
// 4. 创建 Business 的子分类
// ============================================

CREATE (t19:Tag {name: "Management", description: "管理类"})
CREATE (t20:Tag {name: "Marketing", description: "市场营销"})
CREATE (t21:Tag {name: "Finance", description: "金融类"})
CREATE (t22:Tag {name: "Entrepreneurship", description: "创业类"})

// ============================================
// 5. 创建 Science 的子分类
// ============================================

CREATE (t23:Tag {name: "Physics", description: "物理学"})
CREATE (t24:Tag {name: "Chemistry", description: "化学"})
CREATE (t25:Tag {name: "Biology", description: "生物学"})
CREATE (t26:Tag {name: "Mathematics", description: "数学"})

// ============================================
// 6. 建立 Fiction 的子分类关系
// ============================================

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

// ============================================
// 7. 建立 Technology 的子分类关系
// ============================================

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

// ============================================
// 8. 建立 Business 的子分类关系
// ============================================

MATCH (child:Tag {name: "Management"}), (parent:Tag {name: "Business"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Marketing"}), (parent:Tag {name: "Business"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Finance"}), (parent:Tag {name: "Business"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Entrepreneurship"}), (parent:Tag {name: "Business"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

// ============================================
// 9. 建立 Science 的子分类关系
// ============================================

MATCH (child:Tag {name: "Physics"}), (parent:Tag {name: "Science"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Chemistry"}), (parent:Tag {name: "Science"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Biology"}), (parent:Tag {name: "Science"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Mathematics"}), (parent:Tag {name: "Science"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

// ============================================
// 10. 建立更深层的关系
// ============================================

MATCH (child:Tag {name: "Web Development"}), (parent:Tag {name: "Programming"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Data Science"}), (parent:Tag {name: "Programming"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

MATCH (child:Tag {name: "Artificial Intelligence"}), (parent:Tag {name: "Data Science"})
MERGE (child)-[:SUBCATEGORY_OF]->(parent);

// ============================================
// 11. 验证数据（可选）
// ============================================

// 查看所有标签
// MATCH (t:Tag) RETURN t.name, t.description ORDER BY t.name;

// 查看所有关系
// MATCH (t:Tag)-[r:SUBCATEGORY_OF]->(parent:Tag) RETURN t.name, parent.name ORDER BY t.name;

// 统计标签数量
// MATCH (t:Tag) RETURN count(t) as tagCount;

// 统计关系数量
// MATCH ()-[r:SUBCATEGORY_OF]->() RETURN count(r) as relationshipCount;

// 查看图形可视化
// MATCH path = (t:Tag)-[:SUBCATEGORY_OF*1..2]-(related:Tag) RETURN path LIMIT 50;

