package com.ebookstore.hadoop;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Reduce阶段：汇总每个关键词的总出现次数
 */
public class KeywordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        
        int sum = 0;
        
        // 累加所有相同的键（关键词）的值（出现次数）
        for (IntWritable value : values) {
            sum += value.get();
        }
        
        // 输出关键词及其总出现次数
        context.write(key, new IntWritable(sum));
    }
}

