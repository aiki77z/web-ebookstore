package com.ebookstore.hadoop;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Map阶段：提取文本中的关键词并计数
 */
public class KeywordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    
    private static final IntWritable ONE = new IntWritable(1);
    private Set<String> keywords = new HashSet<>();
    
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // 从配置中获取关键词文件路径
        String keywordsPath = context.getConfiguration().get("keywords.file.path");
        if (keywordsPath != null && !keywordsPath.isEmpty()) {
            loadKeywords(keywordsPath);
        } else {
            // 如果没有指定关键词文件，使用默认关键词
            loadDefaultKeywords();
        }
    }
    
    /**
     * 从文件加载关键词列表
     */
    private void loadKeywords(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    keywords.add(line.toLowerCase());
                }
            }
        }
    }
    
    /**
     * 加载默认关键词列表
     */
    private void loadDefaultKeywords() {
        // 根据书籍内容设置的关键词
        String[] defaultKeywords = {
            "科幻", "宇宙", "文明", "地球", "未来", "科技",
            "魔法", "奇幻", "冒险", "巫师", "学校",
            "人性", "生活", "情感", "哲学", "文学", "小说",
            "战斗", "漫画", "动漫", "奇妙",
            "钢琴", "音乐", "演奏", "曲目", "专辑",
            "旅行", "国家", "文化", "历史", "旅游", "埃及", "路线"
        };
        for (String keyword : defaultKeywords) {
            keywords.add(keyword.toLowerCase());
        }
    }
    
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        
        String line = value.toString();
        if (line == null || line.trim().isEmpty()) {
            return;
        }
        
        // 将文本转换为小写以便匹配（不区分大小写）
        String lowerLine = line.toLowerCase();
        
        // 检查每个关键词是否出现在当前行中
        for (String keyword : keywords) {
            int count = countOccurrences(lowerLine, keyword);
            if (count > 0) {
                context.write(new Text(keyword), new IntWritable(count));
            }
        }
    }
    
    /**
     * 统计关键词在文本中出现的次数
     */
    private int countOccurrences(String text, String keyword) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }
}

