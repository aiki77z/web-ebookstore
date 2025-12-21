package com.ebookstore.hadoop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 独立的数据导出工具
 */
@SpringBootApplication
@EntityScan("com.ebookstore.entity")
@EnableJpaRepositories("com.ebookstore.repository")
@ComponentScan("com.ebookstore")
public class ExportMain {
    
    public static void main(String[] args) {
        SpringApplication.run(ExportMain.class, args);
    }
    
    @Bean
    public CommandLineRunner run(BookDescriptionExporter exporter) {
        return args -> {
            String outputDir = args.length > 0 ? args[0] : "hadoop/input";
            System.out.println("开始导出图书简介到目录: " + outputDir);
            try {
                exporter.exportBookDescriptions(outputDir);
                System.out.println("导出完成！");
            } catch (Exception e) {
                System.err.println("导出失败: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        };
    }
}

