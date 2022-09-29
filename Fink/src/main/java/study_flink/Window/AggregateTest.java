package study_flink.Window;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.util.HashSet;

public class AggregateTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Event> streamSource = env.addSource(new MySource());
        SingleOutputStreamOperator<Event> watermarks = streamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forMonotonousTimestamps().withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long l) {
                return event.timestamp;
            }
        }));
        streamSource.print("data");
        watermarks.keyBy(data -> "key").window(TumblingEventTimeWindows.of(Time.seconds(10))).aggregate(
                new AggregateFunction<Event, Tuple2<Long, HashSet<String>>, Double>() {
                    @Override
                    public Tuple2<Long, HashSet<String>> createAccumulator() {
                        return Tuple2.of(0L,new HashSet<>());
                    }

                    @Override
                    public Tuple2<Long, HashSet<String>> add(Event event, Tuple2<Long, HashSet<String>> longHashSetTuple2) {
                        longHashSetTuple2.f1.add(event.user);


                        return Tuple2.of(longHashSetTuple2.f0+1,longHashSetTuple2.f1);
                    }

                    @Override
                    public Double getResult(Tuple2<Long, HashSet<String>> longHashSetTuple2) {
                        //输出的值
                        return (double) (longHashSetTuple2.f0 / longHashSetTuple2.f1.size());
                    }

                    @Override
                    public Tuple2<Long, HashSet<String>> merge(Tuple2<Long, HashSet<String>> longHashSetTuple2, Tuple2<Long, HashSet<String>> acc1) {
                        return null;
                    }
                }

        ).print();
        env.execute();

    }
}
