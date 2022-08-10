package study_flink.WordCount;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class StreamWordCounts {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        ParameterTool parameterTool = ParameterTool.fromArgs(args);
//        String s = parameterTool.get("host");
//        int port = parameterTool.getInt("port");


        DataStreamSource<String> line = env.socketTextStream("centos01", 7777);
        SingleOutputStreamOperator<Tuple2<String, Long>> res = line.flatMap((String data, Collector<Tuple2<String, Long>> out) -> {
            String[] words = data.split(",");
            for (String word : words
            ) {
                out.collect(Tuple2.of(word, 1L));
            }
        }).returns(Types.TUPLE(Types.STRING, Types.LONG));

        KeyedStream<Tuple2<String, Long>, String> keyedStream = res.keyBy(data1 -> data1.f0);

        SingleOutputStreamOperator<Tuple2<String, Long>> sum = keyedStream.sum(1);

        sum.print();

        env.execute();
    }


}
