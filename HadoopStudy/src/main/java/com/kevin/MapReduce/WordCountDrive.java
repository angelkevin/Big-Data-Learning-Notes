package com.kevin.MapReduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class WordCountDrive {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        //获取job
        Configuration conf = new Configuration(true);
        Job job = Job.getInstance(conf);
        //设置jar包路径
        job.setJarByClass(WordCountDrive.class);
        //关联mapper reducer
        job.setMapperClass(WordCountMap.class);
        job.setReducerClass(WordCountReduce.class);
        //设置map输出的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        //设置最终输出的kv
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        //设置输入和输出路径
        FileInputFormat.setInputPaths(job,new Path("D:\\java\\HadoopStudy\\Word.txt"));
        FileOutputFormat.setOutputPath(job,new Path("D:\\java\\HadoopStudy\\1"));
        //提交job
        job.waitForCompletion(true);
    }
}
