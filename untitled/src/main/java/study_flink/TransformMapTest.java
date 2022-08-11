package study_flink;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TransformMapTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);

        //从元素中读取数据
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromElements(new Event("mk", "a", 10000000));

        //使用自定义类，实现MapFunction
        SingleOutputStreamOperator<String> map = eventDataStreamSource.map(new MyMapper());

        //使用匿名类实现MapFunction
        SingleOutputStreamOperator<String> map1 = eventDataStreamSource.map(new MapFunction<Event, String>() {

            @Override
            public String map(Event event) throws Exception {
                return event.url;
            }
        });

        //使用Lambda表达式
        SingleOutputStreamOperator<Long> map2 = eventDataStreamSource.map((data -> data.timestamp));

        map2.print();
        map1.print();
        map.print();
        executionEnvironment.execute();
    }

    // 自定义MapFunction类
    public static class MyMapper implements MapFunction<Event, String> {

        @Override
        public String map(Event event) throws Exception {
            return event.user;
        }
    }
}
