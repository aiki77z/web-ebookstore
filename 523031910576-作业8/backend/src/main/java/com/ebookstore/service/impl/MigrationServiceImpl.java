package com.ebookstore.service.impl;

import com.ebookstore.entity.Book;
import com.ebookstore.entity.mongodb.BookDetail;
import com.ebookstore.repository.BookRepository;
import com.ebookstore.repository.mongodb.BookDetailRepository;
import com.ebookstore.service.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据迁移服务实现类
 * 用于将现有数据迁移到MongoDB和Neo4J
 */
@Service
public class MigrationServiceImpl implements MigrationService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private BookDetailRepository bookDetailRepository;
    
    @Override
    @Transactional
    public int migrateBooksToMongoDB() {
        System.out.println("开始迁移书籍数据到MongoDB...");
        
        // 获取所有书籍（包括已删除的，因为可能需要迁移历史数据）
        List<Book> books = bookRepository.findAll();
        int migratedCount = 0;
        
        for (Book book : books) {
            try {
                // 检查是否已经有MongoDB记录
                BookDetail existingDetail = bookDetailRepository.findByBookId(book.getId()).orElse(null);
                
                // 如果MySQL中有description或cover，且MongoDB中没有，则迁移
                if ((book.getDescription() != null && !book.getDescription().isEmpty()) ||
                    (book.getCover() != null && !book.getCover().isEmpty())) {
                    
                    BookDetail detail = existingDetail != null ? existingDetail : new BookDetail();
                    detail.setBookId(book.getId());
                    
                    // 迁移description
                    if (book.getDescription() != null && !book.getDescription().isEmpty()) {
                        if (detail.getDescription() == null || detail.getDescription().isEmpty()) {
                            detail.setDescription(book.getDescription());
                        }
                    }
                    
                    // 迁移cover
                    if (book.getCover() != null && !book.getCover().isEmpty()) {
                        if (detail.getCover() == null || detail.getCover().isEmpty()) {
                            detail.setCover(book.getCover());
                        }
                    }
                    
                    bookDetailRepository.save(detail);
                    migratedCount++;
                    System.out.println("已迁移书籍: " + book.getTitle() + " (ID: " + book.getId() + ")");
                }
            } catch (Exception e) {
                System.err.println("迁移书籍失败 (ID: " + book.getId() + "): " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("MongoDB迁移完成！共迁移 " + migratedCount + " 本书籍");
        return migratedCount;
    }
    
    @Override
    @Transactional
    public void addSampleTagsToBooks() {
        System.out.println("开始为书籍添加示例标签...");
        
        List<Book> books = bookRepository.findAll();
        int taggedCount = 0;
        
        // 示例标签映射（根据书名关键词匹配）
        for (Book book : books) {
            try {
                String title = book.getTitle().toLowerCase();
                String author = book.getAuthor().toLowerCase();
                String tags = book.getTags();
                
                // 如果已经有标签，跳过
                if (tags != null && !tags.isEmpty()) {
                    continue;
                }
                
                // 根据书名和作者关键词添加标签
                StringBuilder tagList = new StringBuilder();
                
                // 针对具体的8本书添加标签
                String bookTitle = book.getTitle();
                
                // 1. 藤井 風Help Ever Hu - 音乐专辑/非小说
                if (bookTitle.contains("藤井") || bookTitle.contains("風") || bookTitle.contains("Help Ever Hurt Never")) {
                    tagList.append("Non-Fiction,");
                }
                // 2. 三体 - 科幻小说
                else if (bookTitle.contains("三体")) {
                    tagList.append("Science Fiction,");
                    tagList.append("Fiction,");
                }
                // 3. JOJO的奇妙冒险 - 漫画/奇幻/冒险
                else if (bookTitle.contains("JOJO") || bookTitle.contains("奇妙冒险")) {
                    tagList.append("Fantasy,");
                    tagList.append("Adventure,");
                    tagList.append("Fiction,");
                }
                // 4. 素食者 - 小说
                else if (bookTitle.contains("素食者")) {
                    tagList.append("Fiction,");
                }
                // 5. 哈利波特 - 奇幻小说
                else if (bookTitle.contains("哈利波特") || bookTitle.contains("Harry Potter")) {
                    tagList.append("Fantasy,");
                    tagList.append("Fiction,");
                }
                // 6. 长日将尽 - 小说
                else if (bookTitle.contains("长日将尽") || bookTitle.contains("Remains of the Day")) {
                    tagList.append("Fiction,");
                }
                // 7. 孤独星球 埃及 - 旅行指南/非小说
                else if (bookTitle.contains("孤独星球") || bookTitle.contains("Lonely Planet") || bookTitle.contains("埃及")) {
                    tagList.append("Non-Fiction,");
                }
                // 8. 不能承受的生命之轻 - 小说
                else if (bookTitle.contains("不能承受") || bookTitle.contains("生命之轻") || bookTitle.contains("Unbearable Lightness")) {
                    tagList.append("Fiction,");
                }
                // 通用匹配规则
                else {
                    if (title.contains("小说") || title.contains("fiction") || 
                        title.contains("故事") || title.contains("story")) {
                        tagList.append("Fiction,");
                    }
                    
                    if (title.contains("科幻") || title.contains("science fiction") || 
                        title.contains("sci-fi") || title.contains("未来")) {
                        tagList.append("Science Fiction,");
                    }
                    
                    if (title.contains("编程") || title.contains("programming") || 
                        title.contains("代码") || title.contains("code") ||
                        title.contains("java") || title.contains("python") || 
                        title.contains("javascript")) {
                        tagList.append("Programming,");
                        tagList.append("Technology,");
                    }
                    
                    if (title.contains("web") || title.contains("网站") || 
                        title.contains("前端") || title.contains("后端")) {
                        tagList.append("Web Development,");
                        tagList.append("Technology,");
                    }
                    
                    if (title.contains("数据") || title.contains("data") || 
                        title.contains("分析") || title.contains("analysis")) {
                        tagList.append("Data Science,");
                        tagList.append("Technology,");
                    }
                    
                    if (title.contains("人工智能") || title.contains("ai") || 
                        title.contains("机器学习") || title.contains("machine learning")) {
                        tagList.append("Artificial Intelligence,");
                        tagList.append("Data Science,");
                    }
                    
                    if (title.contains("商业") || title.contains("business") || 
                        title.contains("管理") || title.contains("management")) {
                        tagList.append("Business,");
                        tagList.append("Management,");
                    }
                    
                    if (title.contains("科学") || title.contains("science") || 
                        title.contains("物理") || title.contains("physics") ||
                        title.contains("化学") || title.contains("chemistry")) {
                        tagList.append("Science,");
                    }
                    
                    if (title.contains("历史") || title.contains("history")) {
                        tagList.append("History,");
                    }
                    
                    if (title.contains("传记") || title.contains("biography")) {
                        tagList.append("Biography,");
                    }
                    
                    // 如果没有匹配到任何标签，添加默认标签
                    if (tagList.length() == 0) {
                        tagList.append("Fiction,");
                    }
                }
                
                // 移除最后的逗号
                if (tagList.length() > 0) {
                    String finalTags = tagList.substring(0, tagList.length() - 1);
                    book.setTags(finalTags);
                    bookRepository.save(book);
                    taggedCount++;
                    System.out.println("为书籍添加标签: " + book.getTitle() + " -> " + finalTags);
                }
                
            } catch (Exception e) {
                System.err.println("为书籍添加标签失败 (ID: " + book.getId() + "): " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("标签添加完成！共为 " + taggedCount + " 本书籍添加了标签");
    }
}

