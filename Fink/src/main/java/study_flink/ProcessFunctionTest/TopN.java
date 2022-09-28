package study_flink.ProcessFunctionTest;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class TopN {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Event> dataStreamSource = env.addSource(new MySource());
        SingleOutputStreamOperator<Event> watermarks = dataStreamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(0)).withTimestampAssigner(
                new SerializableTimestampAssigner<Event>() {
                    @Override
                    public long extractTimestamp(Event event, long l) {
                        return event.timestamp;
                    }
                }
        ));

        watermarks.map(data -> data.url)
                .windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                .aggregate(new count(),
                        new allwindowsprocess()).print();


        env.execute();
    }

    public static class count implements AggregateFunction<String, HashMap<String, Integer>, ArrayList<Tuple2<String, Integer>>> {
        @Override
        public HashMap<String, Integer> createAccumulator() {
            return new HashMap<>();
        }

        @Override
        public HashMap<String, Integer> add(String url, HashMap<String, Integer> stringIntegerHashMap) {
            if (stringIntegerHashMap.containsKey(url)) {
                int i = stringIntegerHashMap.get(url) + 1;
                stringIntegerHashMap.put(url, i);
            } else {
                stringIntegerHashMap.put(url, 1);
            }
            return stringIntegerHashMap;
        }

        @Override
        public ArrayList<Tuple2<String, Integer>> getResult(HashMap<String, Integer> stringIntegerHashMap) {
            ArrayList<Tuple2<String, Integer>> tuple2s = new ArrayList<>();
            for (String s : stringIntegerHashMap.keySet()) {
                tuple2s.add(Tuple2.of(s, stringIntegerHashMap.get(s)));
            }

            tuple2s.sort(new Comparator<Tuple2<String, Integer>>() {
                @Override
                public int compare(Tuple2<String, Integer> o1, Tuple2<String, Integer> o2) {
                    return o2.f1 - o1.f1;
                }
            });
            return tuple2s;
        }

        @Override
        public HashMap<String, Integer> merge(HashMap<String, Integer> stringIntegerHashMap, HashMap<String, Integer> acc1) {
            return null;
        }
    }

    public static class allwindowsprocess extends ProcessAllWindowFunction<ArrayList<Tuple2<String, Integer>>, String, TimeWindow> {

        @Override
        public void process(ProcessAllWindowFunction<ArrayList<Tuple2<String, Integer>>, String, TimeWindow>.Context context,
                            Iterable<ArrayList<Tuple2<String, Integer>>> iterable, Collector<String> collector) throws Exception {


            ArrayList<Tuple2<String, Integer>> list = iterable.iterator().next();

            collector.collect("窗口开始时间" + new Timestamp(context.window().getStart()) + "\n" + "TOP1：" + list.get(0).f0 + "浏览量为：" + list.get(0).f1 + "\n" +
                    "TOP2：" + list.get(1).f0 + "浏览量为：" + list.get(1).f1 + "\n" + "窗口结束时间" + new Timestamp(context.window().getEnd())+"\n");


        }
    }
}
