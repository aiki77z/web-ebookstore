package com.ebookstore.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * MapReduce作业主类
 * 统计图书简介中关键词的出现次数
 */
public class KeywordCountJob {
    
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: KeywordCountJob <input path> <output path> [keywords file path]");
            System.exit(-1);
        }
        
        Configuration conf = new Configuration();
        
        // 如果提供了关键词文件路径，则设置到配置中
        if (args.length >= 3) {
            conf.set("keywords.file.path", args[2]);
            System.out.println("使用关键词文件: " + args[2]);
        } else {
            System.out.println("使用默认关键词列表");
        }
        
        Job job = Job.getInstance(conf, "Keyword Count Job");
        job.setJarByClass(KeywordCountJob.class);
        job.setMapperClass(KeywordCountMapper.class);
        job.setReducerClass(KeywordCountReducer.class);
        
        // 设置输出键值类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        
        // 设置输入和输出路径
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        // 等待作业完成
        boolean success = job.waitForCompletion(true);
        System.exit(success ? 0 : 1);
    }
}

