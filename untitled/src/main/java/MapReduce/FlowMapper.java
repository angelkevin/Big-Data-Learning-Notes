package MapReduce;

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
