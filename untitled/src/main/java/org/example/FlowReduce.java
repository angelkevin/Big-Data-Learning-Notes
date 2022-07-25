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
