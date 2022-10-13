package study_flink.KeyedState;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

public class TwoStreamJoin {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        SingleOutputStreamOperator<Tuple3<String, String, Long>> stream1 = env
                .fromElements(
                        Tuple3.of("a", "stream-1", 1000L),
                        Tuple3.of("b", "stream-1", 2000L)
                )
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Tuple3<String, String, Long>>forMonotonousTimestamps()
                                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, String, Long>>() {
                                    @Override
                                    public long extractTimestamp(Tuple3<String,
                                            String, Long> t, long l) {
                                        return t.f2;
                                    }
                                })
                );
        SingleOutputStreamOperator<Tuple3<String, String, Long>> stream2 = env
                .fromElements(
                        Tuple3.of("a", "stream-2", 3000L),
                        Tuple3.of("b", "stream-2", 4000L)
                )
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Tuple3<String, String, Long>>forMonotonousTimestamps()
                                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, String, Long>>() {
                                    @Override
                                    public long extractTimestamp(Tuple3<String,
                                            String, Long> t, long l) {
                                        return t.f2;
                                    }
                                })
                );

        stream1.keyBy(s -> s.f0).connect(stream2.keyBy(d -> d.f0)).process(new CoProcessFunction<Tuple3<String, String, Long>, Tuple3<String, String, Long>, String>() {
            ListState<Tuple3<String, String, Long>> listState1;
            ListState<Tuple3<String, String, Long>> listState2;

            @Override
            public void open(Configuration parameters) throws Exception {
                listState1 = getRuntimeContext().getListState(new ListStateDescriptor<>("list1", Types.TUPLE(Types.STRING, Types.STRING, Types.LONG)));
                listState2 = getRuntimeContext().getListState(new ListStateDescriptor<>("list2", Types.TUPLE(Types.STRING, Types.STRING, Types.LONG)));
            }

            @Override
            public void processElement1(Tuple3<String, String, Long> value, CoProcessFunction<Tuple3<String, String, Long>, Tuple3<String, String, Long>, String>.Context ctx, Collector<String> out) throws Exception {
                //获取另一条的数据,配对输出
                for (Tuple3<String, String, Long> right : listState2.get()) {
                    out.collect(value + "=>" + right);
                }
                listState1.add(value);
            }

            @Override
            public void processElement2(Tuple3<String, String, Long> value, CoProcessFunction<Tuple3<String, String, Long>, Tuple3<String, String, Long>, String>.Context ctx, Collector<String> out) throws Exception {
                //获取另一条的数据,配对输出
                for (Tuple3<String, String, Long> left : listState1.get()) {
                    out.collect(left + "=>" + value);
                }
                listState2.add(value);
            }
        }).print();
        env.execute();


    }
}
