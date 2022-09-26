package study_flink.WordCount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;


public class StreamWordcount {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> line = env.readTextFile("D:\\java\\Fink\\src\\main\\input\\1.txt");
        SingleOutputStreamOperator<Tuple2<String, Long>> res = line.flatMap(new myflat());

        KeyedStream<Tuple2<String, Long>, String> keyedStream = res.keyBy(data1 -> data1.f0);

        SingleOutputStreamOperator<Tuple2<String, Long>> sum = keyedStream.sum(1);

        sum.print();

        env.execute();


    }

    public static class myflat implements FlatMapFunction<String, Tuple2<String, Long>> {

        @Override
        public void flatMap(String s, Collector<Tuple2<String, Long>> collector) throws Exception {
            for (String s1 : s.split(",")) {
                collector.collect(Tuple2.of(s1, 1L));

            }
        }
    }
}
