-- 互联网应用开发技术-第三阶段迭代 数据库设计
-- 实现User和UserAuth分离，符合函数依赖和范式要求

-- 创建数据库
CREATE DATABASE IF NOT EXISTS ebookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ebookstore;

-- 用户认证表（UserAuth）
-- 存储用户认证相关信息，与用户基本信息分离
CREATE TABLE user_auth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，唯一标识',
    password_hash VARCHAR(255) NOT NULL COMMENT 'BCrypt加密后的密码',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色：USER, ADMIN',
    active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '账户是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    last_login TIMESTAMP NULL COMMENT '最后登录时间',
    
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_active (active)
) ENGINE=InnoDB COMMENT='用户认证信息表';

-- 用户信息表（User）
-- 存储用户基本信息，与认证信息分离
-- id是主键
-- auth_id是外键，关联到user_auth表的id
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '用户姓名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱地址，唯一',
    address VARCHAR(255) COMMENT '地址',
    phone VARCHAR(20) COMMENT '手机号码',
    auth_id BIGINT NOT NULL COMMENT '关联的认证信息ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_auth_id (auth_id),
    INDEX idx_name (name),
    
    FOREIGN KEY (auth_id) REFERENCES user_auth(id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='用户基本信息表';

-- 书籍表
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '书籍标题',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    description TEXT COMMENT '书籍描述',
    cover VARCHAR(500) COMMENT '封面图片URL',
    status VARCHAR(20) DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE, OUT_OF_STOCK',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_status (status),
    INDEX idx_price (price)
) ENGINE=InnoDB COMMENT='书籍信息表';

-- 购物车表
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '书籍ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    selected BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否选中',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    
    UNIQUE KEY uk_user_book (user_id, book_id),
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='购物车表';

-- 订单表
-- id是主键
-- user_id是外键，关联到users表的id
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单状态：PENDING, PAID, SHIPPED, DELIVERED, CANCELLED',
    shipping_address VARCHAR(500) COMMENT '收货地址',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '订单日期',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date),
    INDEX idx_created_at (created_at),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='订单表';

-- 订单商品表
-- id是主键
-- order_id是外键，关联到orders表的id
-- book_id是外键，关联到books表的id
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    book_id BIGINT NOT NULL COMMENT '书籍ID',
    quantity INT NOT NULL COMMENT '购买数量',
    price DECIMAL(10,2) NOT NULL COMMENT '购买时的单价',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    
    INDEX idx_order_id (order_id),
    INDEX idx_book_id (book_id),
    
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='订单商品明细表';

-- 初始化数据

-- 插入管理员认证信息（密码：123456，已BCrypt加密）
INSERT INTO user_auth (username, password_hash, role, active) VALUES 
('admin', '$2a$10$zwE/tZIrZ2wnP/tsIRxMTuZxcoezYhI36u7lS6C2LLMdI6WrnwaIO', 'ADMIN', TRUE);

-- 插入测试用户认证信息（密码：123456，已BCrypt加密）
INSERT INTO user_auth (username, password_hash, role, active) VALUES 
('tom', '$2a$10$zwE/tZIrZ2wnP/tsIRxMTuZxcoezYhI36u7lS6C2LLMdI6WrnwaIO', 'USER', TRUE),
('jerry', '$2a$10$zwE/tZIrZ2wnP/tsIRxMTuZxcoezYhI36u7lS6C2LLMdI6WrnwaIO', 'USER', TRUE);

-- 插入用户基本信息
INSERT INTO users (name, email, address, phone, auth_id) VALUES 
('管理员', 'admin@ebookstore.com', '北京市朝阳区', '13800000001', 1),
('汤姆', 'tom@example.com', '上海市浦东新区', '13800000002', 2),
('杰瑞', 'jerry@example.com', '广州市天河区', '13800000003', 3);

-- 插入书籍数据
INSERT INTO books (title, author, price, description, cover, status) VALUES                                                                     ('藤井 風Help Ever Hurt Never钢琴谱', '藤井 風', 165.00, '这是藤井 風的钢琴谱集，收录了Help Ever Hurt Never专辑中的经典曲目。适合中级到高级钢琴演奏者。', '/images/fujikaze.jpg', 'AVAILABLE'),
('三体', '刘慈欣', 93.00, '科幻小说的中国里程碑之作，获得雨果奖的第一部亚洲作品。小说讲述了地球文明与三体文明的惊心动魄的第一次接触。', '/images/threebody.jpg', 'AVAILABLE'),
('JOJO的奇妙冒险 第五部', '荒木飞吕彦', 200.00, '《JOJO的奇妙冒险》系列第五部，讲述了乔鲁诺·乔巴纳在意大利黑帮中的冒险故事。', '/images/jojo.jpg', 'AVAILABLE'),
('素食者', '韩江', 37.00, '一部令人震撼的当代韩国小说，讲述了一个放弃肉食的女性的故事，展现了人性的复杂面向。', '/images/vegetarian.jpg', 'AVAILABLE'),
('哈利波特', 'J.K.罗琳', 375.00, '全球畅销的魔法世界系列小说，讲述了男孩哈利·波特在霍格沃茨魔法学校的冒险故事。', '/images/harrypotter.jpg', 'AVAILABLE'),
('长日将尽', '石黑一雄', 59.00, '诺贝尔文学奖得主石黑一雄的代表作，通过一位管家的回忆，展现了英国贵族社会的没落。', '/images/remains.jpg', 'AVAILABLE'),
('孤独星球 埃及', '澳大利亚Lonely Planet公司', 69.30, '本书"计划你的行程"帮助旅行者打造适合自己的出行攻略，使旅途更轻松；并策划了"乘船游览尼罗河""红海潜水"专题，在水上玩转埃及。"在路上"分为"开罗""尼罗河谷北部""卢克索""锡瓦绿洲和西部沙漠"等11章，带你全方位走入这片奇幻之地。"了解埃及"向你介绍了这个国家的背景知识，"生存指南"涉及在埃及可能会遇到的衣食住行问题，极具参考价值。还有特别策划的"埃及博物馆"章节，全面详细地介绍了各场馆及展品的历史，推荐内涵丰富的展品及合理的游览路线。', '/images/lonelyplanet.jpg', 'AVAILABLE'),                                                                         ('不能承受的生命之轻', '米兰·昆德拉', 88.00, '依托二十世纪六十年代捷克斯洛伐克的历史剧变，以托马斯与特蕾莎偶然而宿命般的爱情为主线展开故事，不仅仅是描述几对男女感情上的纠葛，也不仅仅是书写个人命运在大的境遇变迁中的沉浮、个人在变革时刻的选择，更是一部层次丰富、意象繁复的哲理小说，从永恒轮回的谵妄之下人的生命分量几何这一带着神秘感的疑问开篇，随着不断穿插的书中人物的生活走向、所思所想提出了生命之轻与重、灵与肉的相对论。', '/images/life.jpg', 'AVAILABLE');
-- 数据库设计说明：
-- 1. 函数依赖分析：
--    user_auth: username → password_hash, role, active
--    users: email → name, address, phone, auth_id
--    books: id → title, author, price, description
--    orders: id → user_id, total_amount, status
--    order_items: (order_id, book_id) → quantity, price
--
-- 2. 范式设计：
--    - 所有表均满足第一范式（1NF）：每个属性都是不可分割的基本数据项 原子属性
--    - 所有表均满足第二范式（2NF）：非主属性完全依赖主键
--    - 所有表均满足第三范式（3NF）：非主属性不传递依赖主键
--    - User和UserAuth分离满足BCNF：消除了用户基本信息与认证信息的混合依赖
--
-- 3. 外键关联：
--    - users.auth_id → user_auth.id (一对一关联)
--    - cart_items.user_id → users.id (多对一关联)
--    - cart_items.book_id → books.id (多对一关联)
--    - orders.user_id → users.id (多对一关联)
--    - order_items.order_id → orders.id (多对一关联)
--    - order_items.book_id → books.id (多对一关联)
--
-- 4. 级联操作：
--    - 删除用户认证信息时，级联删除用户基本信息
--    - 删除用户时，级联删除购物车和订单信息
--    - 删除订单时，级联删除订单商品明细 