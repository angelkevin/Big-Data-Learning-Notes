# Flow Bean

```java
package org.example;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FlowBean implements Writable {
    private long upFlow;
    private long downFlow;
    private long sumFlow;
    public FlowBean() {
    }

    public long getDownFlow() {
        return downFlow;
    }

    public long getSumFlow() {
        return sumFlow;
    }

    public long getUpFlow() {
        return upFlow;
    }

    public void setUpFlow(long upFlow) {
        this.upFlow = upFlow;
    }

    public void setDownFlow(long downFlow) {
        this.downFlow = downFlow;
    }

    public void setSumFlow() {
        this.sumFlow = this.downFlow+this.upFlow;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

        dataOutput.writeLong(upFlow);
        dataOutput.writeLong(downFlow);
        dataOutput.writeLong(sumFlow);

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.upFlow = dataInput.readLong();
        this.downFlow = dataInput.readLong();
        this.sumFlow = dataInput.readLong();
    }

    @Override
    public String toString() {
        return  upFlow +"\t"+ downFlow + "\t" +sumFlow ;
    }
}

```

# Flow Mapper

```java
package org.example;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowMapper extends Mapper<LongWritable, Text, Text, FlowBean> {
    private final Text outk = new Text();
    private final FlowBean outv = new FlowBean();
    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, FlowBean>.Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] strings = line.split(";");
        String phone = strings[1];
        String up = strings[strings.length - 3];
        String down = strings[strings.length - 2];
        outk.set(phone);
        outv.setUpFlow(Long.parseLong(up));
        outv.setDownFlow(Long.parseLong(down));
        outv.setSumFlow();
        context.write(outk, outv);
    }
}

```

# Flow Reduce

```java
package org.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FlowReduce extends Reducer<Text, FlowBean, Text, FlowBean> {
    private final FlowBean outv = new FlowBean();

    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Reducer<Text, FlowBean, Text, FlowBean>.Context context) throws IOException, InterruptedException {
        long totalup = 0;
        long totaldowm = 0;
        for (FlowBean flowBean : values) {
            totalup += flowBean.getUpFlow();
            totaldowm += flowBean.getDownFlow();
        }
        outv.setUpFlow(totalup);
        outv.setDownFlow(totaldowm);
        outv.setSumFlow();
        context.write(key, outv);
    }
}
```

# Flow Job

```java
package org.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class Flowjob {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
//        初始化设置,获取job;
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

//        jar包路径
        job.setJarByClass(Flowjob.class);


//        关联job map reduce;
        job.setMapperClass(FlowMapper.class);
        job.setReducerClass(FlowReduce.class);


//        设置map输出类型;
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

//        设置最终输出类型;
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

//        设置输入输出路径;
        FileInputFormat.setInputPaths(job, new Path("D:\\1.txt"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\out"));

//        提交job;
//        job.submit();
        boolean result = job.waitForCompletion(true);

        System.exit(result ? 0 : 1);


    }
}
```

