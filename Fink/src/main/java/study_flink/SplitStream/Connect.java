package study_flink.SplitStream;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class Connect {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<Tuple3<String, String, Long>> stream1 = env.fromElements(
                Tuple3.of("order1", "app", 1000L),
                Tuple3.of("order2", "app", 2000L),
                Tuple3.of("order3", "app", 7000L)

        ).assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple3<String, String, Long>>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(
                new SerializableTimestampAssigner<Tuple3<String, String, Long>>() {
                    @Override
                    public long extractTimestamp(Tuple3<String, String, Long> element, long recordTimestamp) {
                        return element.f2;
                    }
                }
        ));

        SingleOutputStreamOperator<Tuple4<String, String, String, Long>> stream2 = env.fromElements(
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
//        检测同一支付单在两条流中是否匹配，不匹配就报警
        stream1.connect(stream2).keyBy(data -> data.f0, data1 -> data1.f0)
                .process(new OrderMatchResult()).print();


        env.execute();

    }

    public static class OrderMatchResult extends CoProcessFunction<Tuple3<String, String, Long>, Tuple4<String, String, String, Long>, String> {
        //定义状态变量，用来保存已经到达的事件
        private ValueState<Tuple3<String, String, Long>> app;
        private ValueState<Tuple4<String, String, String, Long>> third;

        @Override
        public void open(Configuration parameters) throws Exception {
            app = getRuntimeContext().getState(new ValueStateDescriptor<Tuple3<String, String, Long>>("app", Types.TUPLE(Types.STRING, Types.STRING, Types.LONG)));
            third = getRuntimeContext().getState(new ValueStateDescriptor<Tuple4<String, String, String, Long>>("third", Types.TUPLE(Types.STRING, Types.STRING, Types.STRING, Types.LONG)));
        }

        @Override
        public void processElement1(Tuple3<String, String, Long> value, Context ctx, Collector<String> out) throws Exception {
            if (third.value() != null) {
                out.collect("对账成功" + value + third.value());
                third.clear();
            } else {
                app.update(value);
                //注册一个五秒的定时器
                ctx.timerService().registerEventTimeTimer(value.f2 +5000L);
            }

        }

        @Override
        public void processElement2(Tuple4<String, String, String, Long> value, Context ctx, Collector<String> out) throws Exception {

            if (app.value() != null) {
                out.collect("对账成功" + value + app.value());
                app.clear();
            } else {
                third.update(value);
                ctx.timerService().registerEventTimeTimer(value.f3);
            }
        }

        @Override
        public void onTimer(long timestamp, CoProcessFunction<Tuple3<String, String, Long>, Tuple4<String, String, String, Long>, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
            if (app.value() != null) {
                System.out.println(666);
                out.collect("第三方失败");
            }
            if (third.value() != null) {
                out.collect("app失败");
            }
            app.clear();
            third.clear();

        }
    }
}
