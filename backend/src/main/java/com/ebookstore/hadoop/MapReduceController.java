package com.ebookstore.hadoop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * MapReduce操作控制器
 */
@RestController
@RequestMapping("/api/hadoop")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MapReduceController {
    
    @Autowired
    private BookDescriptionExporter exporter;
    
    /**
     * 导出图书简介到分类文件
     */
    @PostMapping("/export")
    public ResponseEntity<Map<String, Object>> exportBookDescriptions(
            @RequestParam(defaultValue = "hadoop/input") String outputDir) {
        Map<String, Object> response = new HashMap<>();
        try {
            exporter.exportBookDescriptions(outputDir);
            response.put("success", true);
            response.put("message", "图书简介导出成功，输出目录: " + outputDir);
            response.put("outputDir", outputDir);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导出失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 获取导出文件列表
     */
    @GetMapping("/files")
    public ResponseEntity<Map<String, Object>> getExportedFiles(
            @RequestParam(defaultValue = "hadoop/input") String inputDir) {
        Map<String, Object> response = new HashMap<>();
        try {
            File dir = new File(inputDir);
            if (!dir.exists() || !dir.isDirectory()) {
                response.put("success", false);
                response.put("message", "目录不存在: " + inputDir);
                return ResponseEntity.ok(response);
            }
            
            File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
            String[] fileNames = new String[files != null ? files.length : 0];
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    fileNames[i] = files[i].getName();
                }
            }
            
            response.put("success", true);
            response.put("files", fileNames);
            response.put("count", fileNames.length);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取文件列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}

