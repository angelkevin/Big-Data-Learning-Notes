package study_flink.State;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.sql.Timestamp;
import java.time.Duration;

public class FakeWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        SingleOutputStreamOperator<Event> stream = env.addSource(new MySource()).assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(
                new SerializableTimestampAssigner<Event>() {
                    @Override
                    public long extractTimestamp(Event element, long recordTimestamp) {
                        return element.timestamp;
                    }
                }
        ));
        stream.keyBy(data -> data.url).process(new KeyedProcessFunction<String, Event, String>() {


            MapState<Long, Long> windowUrlCountState;


            @Override
            public void open(Configuration parameters) throws Exception {
                windowUrlCountState = getRuntimeContext().getMapState(new MapStateDescriptor<>("window", Long.class, Long.class));
            }

            @Override
            public void processElement(Event value, KeyedProcessFunction<String, Event, String>.Context ctx, Collector<String> out) throws Exception {
                //每来一条数据，根据时间戳判断他属于哪个窗口
                Long windowStart = value.timestamp/10000L*10000L;
                Long windowEnd = windowStart + 10000L;
                //注册end减一的定时器
                ctx.timerService().registerEventTimeTimer(windowEnd - 1L);
                //更新状态，进行聚合
                if (windowUrlCountState.contains(windowStart)) {
                    Long count = windowUrlCountState.get(windowStart);
                    windowUrlCountState.put(windowStart, count + 1);
                } else {
                    windowUrlCountState.put(windowStart, 1L);
                }
            }

            @Override
            public void onTimer(long timestamp, KeyedProcessFunction<String, Event, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
                Long windowEnd = timestamp + 1;
                Long windowStart = windowEnd - 10000L;
                Long count = windowUrlCountState.get(windowStart);
                out.collect("窗口 :" + new Timestamp(windowStart) + "~" + new Timestamp(windowEnd) + "Url = " + ctx.getCurrentKey() + "count =" + count);

                windowUrlCountState.remove(windowStart);


            }
        }).print();
        env.execute();


    }
}
