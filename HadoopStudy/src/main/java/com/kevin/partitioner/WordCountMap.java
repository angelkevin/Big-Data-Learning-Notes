package com.kevin.partitioner;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;


public class WordCountMap extends Mapper<LongWritable, Text, Text, IntWritable> {
    Text text = new Text();
    IntWritable one = new IntWritable(1);

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
        //获取一行数据
        String line = value.toString();
        //切割
        String[] words = line.split(" ");
        //循环写出
        for (String word : words) {
            text.set(word);
            context.write(text, one);
        }
    }
}
