package com.ebookstore.hadoop;

import com.ebookstore.entity.Book;
import com.ebookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 图书简介导出工具
 * 将图书简介按照类型分类存储到不同的文本文件中
 */
@Component
public class BookDescriptionExporter {
    
    @Autowired
    private BookRepository bookRepository;
    
    // 书籍分类映射
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>();
    
    static {
        // 科幻小说类
        CATEGORY_KEYWORDS.put("ScienceFiction.txt", Arrays.asList("科幻", "宇宙", "文明", "地球", "未来", "科技"));
        // 奇幻小说类
        CATEGORY_KEYWORDS.put("Fantasy.txt", Arrays.asList("魔法", "奇幻", "冒险", "巫师", "学校"));
        // 文学小说类
        CATEGORY_KEYWORDS.put("Literature.txt", Arrays.asList("人性", "生活", "情感", "哲学", "文学", "小说"));
        // 漫画动漫类
        CATEGORY_KEYWORDS.put("Comic.txt", Arrays.asList("冒险", "战斗", "漫画", "动漫", "奇妙"));
        // 音乐艺术类
        CATEGORY_KEYWORDS.put("Music.txt", Arrays.asList("钢琴", "音乐", "演奏", "曲目", "专辑"));
        // 旅行地理类
        CATEGORY_KEYWORDS.put("Travel.txt", Arrays.asList("旅行", "国家", "文化", "历史", "旅游", "埃及", "路线"));
    }
    
    // 特定书籍的分类映射（如果标题或描述包含特定关键词）
    private static final Map<String, String> BOOK_TITLE_MAPPING = new HashMap<>();
    
    static {
        BOOK_TITLE_MAPPING.put("藤井", "Music.txt");
        BOOK_TITLE_MAPPING.put("钢琴谱", "Music.txt");
        BOOK_TITLE_MAPPING.put("三体", "ScienceFiction.txt");
        BOOK_TITLE_MAPPING.put("JOJO", "Comic.txt");
        BOOK_TITLE_MAPPING.put("哈利波特", "Fantasy.txt");
        BOOK_TITLE_MAPPING.put("孤独星球", "Travel.txt");
        BOOK_TITLE_MAPPING.put("素食者", "Literature.txt");
        BOOK_TITLE_MAPPING.put("长日将尽", "Literature.txt");
        BOOK_TITLE_MAPPING.put("不能承受的生命之轻", "Literature.txt");
    }
    
    /**
     * 导出所有图书简介到分类文件
     * @param outputDir 输出目录
     * @throws IOException IO异常
     */
    public void exportBookDescriptions(String outputDir) throws IOException {
        // 创建输出目录
        Path outputPath = Paths.get(outputDir);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        // 获取所有书籍
        List<Book> books = bookRepository.findAll();
        
        // 为每个分类创建文件写入器
        Map<String, FileWriter> writers = new HashMap<>();
        try {
            // 初始化所有分类文件的写入器
            for (String fileName : CATEGORY_KEYWORDS.keySet()) {
                Path filePath = outputPath.resolve(fileName);
                writers.put(fileName, new FileWriter(filePath.toFile(), false));
            }
            
            // 处理每本书
            for (Book book : books) {
                String description = book.getDescription();
                if (description == null || description.trim().isEmpty()) {
                    continue;
                }
                
                String categoryFile = determineCategory(book);
                if (categoryFile != null && writers.containsKey(categoryFile)) {
                    FileWriter writer = writers.get(categoryFile);
                    // 写入书籍标题和简介，每本书用空行分隔
                    writer.write("=== " + book.getTitle() + " ===\n");
                    writer.write(description + "\n\n");
                }
            }
            
            System.out.println("成功导出 " + books.size() + " 本书的简介到目录: " + outputDir);
            
        } finally {
            // 关闭所有写入器
            for (FileWriter writer : writers.values()) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("关闭文件写入器失败: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 确定书籍的分类
     * @param book 书籍对象
     * @return 分类文件名
     */
    private String determineCategory(Book book) {
        String title = book.getTitle() != null ? book.getTitle() : "";
        String description = book.getDescription() != null ? book.getDescription() : "";
        String text = title + " " + description;
        
        // 首先检查标题映射
        for (Map.Entry<String, String> entry : BOOK_TITLE_MAPPING.entrySet()) {
            if (title.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // 然后根据关键词匹配
        int maxMatches = 0;
        String bestCategory = null;
        
        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            int matches = 0;
            for (String keyword : entry.getValue()) {
                if (text.contains(keyword)) {
                    matches++;
                }
            }
            if (matches > maxMatches) {
                maxMatches = matches;
                bestCategory = entry.getKey();
            }
        }
        
        // 如果没有匹配到，默认归为文学类
        return bestCategory != null ? bestCategory : "Literature.txt";
    }
}

