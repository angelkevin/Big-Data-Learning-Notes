package study_flink;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TransformFilterTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
        DataStreamSource<String> line = executionEnvironment.readTextFile("input/user");
        //使用匿名类
        SingleOutputStreamOperator<String> Zyf = line.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String s) throws Exception {
                String[] split = s.split(",");
                return split[0].equals("Zyf");
            }
        });
        //使用自定义类
        SingleOutputStreamOperator<String> Zyf1 = line.filter(new Myfilter());

        Zyf1.print();
        Zyf.print();
        executionEnvironment.execute();
    }

    public static class Myfilter implements FilterFunction<String> {

        @Override
        public boolean filter(String s) throws Exception {
            String[] split = s.split(",");
            return split[0].equals("Zyf");
        }
    }
}

