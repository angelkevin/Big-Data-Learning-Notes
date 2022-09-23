package study_flink.Transform;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;

public class TransformFlatMapTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);

        //从元素中读取数据
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromElements(new Event("mk", "a", 10000000));
        //使用自定义类
        eventDataStreamSource.flatMap(new MYFlatMap()).print();
        //使用匿名类

        //使用lambda表达式
        eventDataStreamSource.flatMap((Event value,Collector<String> out) ->{
            out.collect(value.url);
            out.collect(value.user);
            out.collect(String.valueOf(value.timestamp));
        }).returns(new TypeHint<String>() {
            @Override
            public TypeInformation<String> getTypeInfo() {
                return super.getTypeInfo();
            }
        }).print();

        executionEnvironment.execute();
    }

    public static class MYFlatMap implements FlatMapFunction<Event,String>{

        @Override
        public void flatMap(Event event, Collector<String> collector) throws Exception {
            collector.collect(event.user);
            collector.collect(event.url);
            collector.collect(String.valueOf(event.timestamp));
        }
    }
}
