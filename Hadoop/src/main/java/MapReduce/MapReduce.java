package MapReduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class MapReduce {


    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {


//        初始化设置,获取job;
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

//        jar包路径
        job.setJarByClass(MapReduce.class);


//        关联job map reduce;
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);


//        设置map输出类型;
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

//        设置最终输出类型;
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

//        设置输入输出路径;
        FileInputFormat.setInputPaths(job, new Path("D:\\1.txt"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\out"));

//        提交job;
//        job.submit();
        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);


    }
}
