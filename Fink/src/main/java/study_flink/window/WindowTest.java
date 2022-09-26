package study_flink.window;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.sql.Timestamp;

public class WindowTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.addSource(new MySource());
        SingleOutputStreamOperator<Event> eventSingleOutputStreamOperator = eventDataStreamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forMonotonousTimestamps().withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long l) {
                return event.timestamp;
            }
        }));
//        SingleOutputStreamOperator<Tuple2<String, Long>> reduce = eventSingleOutputStreamOperator.map(new MapFunction<Event, Tuple2<String, Long>>() {
//                    @Override
//                    public Tuple2<String, Long> map(Event event) throws Exception {
//                        return Tuple2.of(event.user, 1L);
//                    }
//                })
//                .keyBy(data -> data.f0)
//                .window(TumblingEventTimeWindows.of(Time.seconds(10))) //滚动事件时间窗口
//                .reduce(new ReduceFunction<Tuple2<String, Long>>() {
//                    @Override
//                    public Tuple2<String, Long> reduce(Tuple2<String, Long> stringLongTuple2, Tuple2<String, Long> t1) throws Exception {
//                        return Tuple2.of(stringLongTuple2.f0, stringLongTuple2.f1 + t1.f1);
//                    }
//                });

        eventSingleOutputStreamOperator.keyBy(data -> data.user).window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .aggregate(
                        new AggregateFunction<Event, Tuple2<Long,Integer>, String>() {
                            // 初始化，只调用一次
                            @Override
                            public Tuple2<Long, Integer> createAccumulator() {
                                return Tuple2.of(0L,0);
                            }

                            @Override
                            public Tuple2<Long, Integer> add(Event event, Tuple2<Long, Integer> longIntegerTuple2) {
                                return Tuple2.of(longIntegerTuple2.f0+event.timestamp,longIntegerTuple2.f1+1);
                            }

                            @Override
                            public String getResult(Tuple2<Long, Integer> longIntegerTuple2) {
                                Timestamp timestamp = new Timestamp(longIntegerTuple2.f0 / longIntegerTuple2.f1);
                                return String.valueOf(timestamp);
                            }

                            @Override
                            public Tuple2<Long, Integer> merge(Tuple2<Long, Integer> longIntegerTuple2, Tuple2<Long, Integer> acc1) {
                                return Tuple2.of(longIntegerTuple2.f0+acc1.f0,longIntegerTuple2.f1+acc1.f1 );
                            }
                        }
                ).print();





       // reduce.print();
        executionEnvironment.execute();
    }
}
