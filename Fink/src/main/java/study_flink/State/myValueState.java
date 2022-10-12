package study_flink.State;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.time.Duration;

public class myValueState {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        SingleOutputStreamOperator<Event> stream = env.addSource(new MySource()).assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long recordTimestamp) {
                return event.timestamp;
            }
        }));


        stream.keyBy(data -> data.user).process(new KeyedProcessFunction<String, Event, String>() {
            ValueState<Long> countCount;
            ValueState<Long> timerTsState;

            @Override
            public void open(Configuration parameters) throws Exception {
                countCount = getRuntimeContext().getState(new ValueStateDescriptor<Long>("count", Long.class));
                timerTsState = getRuntimeContext().getState(new ValueStateDescriptor<>("ts", Long.class));

            }

            //每来一条数据都会调用
            @Override
            public void processElement(Event value, KeyedProcessFunction<String, Event, String>.Context ctx, Collector<String> out) throws Exception {
                Long count = countCount.value();
                countCount.update(count == null ? 1 : count + 1);

                if (timerTsState.value() == null) {
                    ctx.timerService().registerEventTimeTimer(value.timestamp + 10 * 1000L);
                    timerTsState.update(value.timestamp + 10 * 1000L);
                }

            }

            @Override
            public void onTimer(long timestamp, KeyedProcessFunction<String, Event, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
                out.collect("定时器触发：" + ctx.getCurrentKey() + " " + "PV:" + countCount.value());
                timerTsState.clear();
            }
        }).print();


        env.execute();
    }
}
