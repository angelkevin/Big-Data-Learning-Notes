package com.kevin.MapReduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class WordCountReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
    IntWritable count = new IntWritable();
    int sum = 0;

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {

        for (IntWritable value : values) {
            sum = sum + value.get();
        }

        count.set(sum);
        context.write(key, count);
    }
}
