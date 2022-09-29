package study_flink.SplitStream;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.time.Duration;

public class SplitStream {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.addSource(new MySource());
        SingleOutputStreamOperator<Event> eventSingleOutputStreamOperator = eventDataStreamSource
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Event>forBoundedOutOfOrderness(Duration.ZERO)
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event element, long recordTimestamp) {
                return element.timestamp;
            }
        }));

        OutputTag<Tuple2<String, String>> zkw = new OutputTag<Tuple2<String, String>>("zkw"){};

        SingleOutputStreamOperator<Event> zkw1 = eventSingleOutputStreamOperator.process(new ProcessFunction<Event, Event>() {
            @Override
            public void processElement(Event value, ProcessFunction<Event, Event>.Context ctx, Collector<Event> out) throws Exception {
                if (value.user.equals("zkw")) {
                    ctx.output(zkw, Tuple2.of(value.user, value.url));
                }
                else
                    out.collect(value);
            }
        });

        zkw1.print("else");
        zkw1.getSideOutput(zkw).print("zkw");
        executionEnvironment.execute();
    }
}
