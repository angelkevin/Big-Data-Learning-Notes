package study_flink.SplitStream;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Duration;

public class WindowJoin {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<Tuple3<String, String, Long>> stream1 = env.fromElements(
                Tuple3.of("order1", "app", 1000L),
                Tuple3.of("order2", "app", 2000L),
                Tuple3.of("order3", "app", 3500L)
        ).assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple3<String, String, Long>>forBoundedOutOfOrderness(Duration.ofSeconds(0)).withTimestampAssigner(
                new SerializableTimestampAssigner<Tuple3<String, String, Long>>() {
                    @Override
                    public long extractTimestamp(Tuple3<String, String, Long> element, long recordTimestamp) {
                        return element.f2;
                    }
                }
        ));

        SingleOutputStreamOperator<Tuple4<String, String, String, Long>> stream2 = env.fromElements(
                Tuple4.of("order1", "third-party", "success", 2000L),
                Tuple4.of("order1", "third-party", "success", 3000L),
                Tuple4.of("order3", "third-party", "success", 4000L)

        ).assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple4<String, String, String, Long>>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(
                new SerializableTimestampAssigner<Tuple4<String, String, String, Long>>() {
                    @Override
                    public long extractTimestamp(Tuple4<String, String, String, Long> element, long recordTimestamp) {
                        return element.f3;
                    }
                }
        ));

        stream1.join(stream2).where(data -> data.f0).equalTo(data1 -> data1.f0).window(TumblingEventTimeWindows.of(Time.seconds(5)))
                .apply(new JoinFunction<Tuple3<String, String, Long>, Tuple4<String, String, String, Long>, String>() {
                    @Override
                    public String join(Tuple3<String, String, Long> first, Tuple4<String, String, String, Long> second) throws Exception {
                        return first + "," + second;
                    }
                }).print();

        env.execute();

    }
}
