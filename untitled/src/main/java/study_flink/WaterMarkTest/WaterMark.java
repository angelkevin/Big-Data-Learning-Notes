package study_flink.WaterMarkTest;

import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import study_flink.Source.Event;

import java.time.Duration;

public class WaterMark {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromElements(new Event("mk", "a", 10000000L),
                new Event("zkw", "pornhub", 100000L),
                new Event("zkw", "pornhub.com", 6666666666L),
                new Event("zkw", "pornhub", 100000L),
                new Event("zkw", "pornhub", 100000L));
        //有序流的watermark
        eventDataStreamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forMonotonousTimestamps().withTimestampAssigner(
                new SerializableTimestampAssigner<Event>() {
                    //抽取时间戳
                    @Override
                    public long extractTimestamp(Event event, long l) {
                        return event.timestamp;
                    }
                }
        ));
        //乱序流的watermark
        //插入水位线逻辑，延迟设置
        eventDataStreamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO)
                .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                    //抽取时间戳
                    @Override
                    public long extractTimestamp(Event event, long l) {
                        return event.timestamp;
                    }
                })
        );
    }
}
