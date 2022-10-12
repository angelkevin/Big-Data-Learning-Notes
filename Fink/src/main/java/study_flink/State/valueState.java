package study_flink.State;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.time.Duration;

public class valueState<E> {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        SingleOutputStreamOperator<Event> stream = env.addSource(new MySource()).assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long recordTimestamp) {
                return event.timestamp;
            }
        }));
        stream.keyBy(data -> data.user).flatMap(new MyFlatMap()).print();


        env.execute();
    }

    //实现自定义的RichFlatMapFunction
    public static class MyFlatMap extends RichFlatMapFunction<Event, String> {

        //定义状态
        ValueState<Event> myValueState;
        ListState<Event> myListState;
        MapState<String, Long> myMapState;
        ReducingState<Event> myReducingState;
        AggregatingState<Event, String> myAggregatingState;

        @Override
        public void open(Configuration parameters) throws Exception {
            myValueState = getRuntimeContext().getState(new ValueStateDescriptor<Event>("myValueState", Event.class));
            myListState = getRuntimeContext().getListState(new ListStateDescriptor<Event>("myListState", Event.class));
            myMapState = getRuntimeContext().getMapState(new MapStateDescriptor<String, Long>("myMapState", String.class, Long.class));
            myReducingState = getRuntimeContext().getReducingState(new ReducingStateDescriptor<Event>("myReducingState", new ReduceFunction<Event>() {
                @Override
                public Event reduce(Event value1, Event value2) throws Exception {
                    return new Event(value1.user, value2.url, value1.timestamp);
                }
            }, Event.class));
            myAggregatingState = getRuntimeContext().getAggregatingState(new AggregatingStateDescriptor<Event, Long, String>("myAggregatingState", new AggregateFunction<Event, Long, String>() {
                @Override
                public Long createAccumulator() {

                    return 0L;
                }

                @Override
                public Long add(Event value, Long accumulator) {
                    return accumulator + 1;
                }

                @Override
                public String getResult(Long accumulator) {
                    return "count:" + accumulator;
                }

                @Override
                public Long merge(Long a, Long b) {
                    return a + b;
                }
            }, Long.class));


        }

        @Override
        public void flatMap(Event value, Collector<String> out) throws Exception {


            myValueState.update(value);
            System.out.println("myValueState: " + myValueState.value());

            myListState.add(value);
            System.out.println("myListState: " + myListState.toString());

            myMapState.put(value.user, myMapState.get(value.user) == null ? 1 : myMapState.get(value.user) + 1);
            System.out.println("myMapState: " + myMapState.get(value.user));

            myAggregatingState.add(value);
            System.out.println("myAggregatingState: " + myAggregatingState.get());

            myReducingState.add(value);
            System.out.println("myReducingState: " + myReducingState.get());

        }
    }
}
