package com.kevin.PartitionAndCompare;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class FlowPartitioner extends Partitioner<Text, FlowBean> {
    @Override
    public int getPartition(Text text, FlowBean flowBean, int numPartitions) {
        String phone = text.toString();
        String pre = phone.substring(0, 3);
        System.out.println(pre);
        if ("136".equals(pre)) {
            return 0;
        } else if (("137".equals(pre))) {
            return 1;

        } else return 2;
    }
}
