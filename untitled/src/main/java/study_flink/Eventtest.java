package study_flink;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;

public class Eventtest {
    public static void main(String[] args) throws Exception {
//        创建执行环境
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();

        executionEnvironment.setParallelism(1);

        ArrayList<Event> events = new ArrayList<Event>();

        events.add(new Event("asd", "asd", 456L));
//        从集合中读取数据
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromCollection(events);

        eventDataStreamSource.print();
        executionEnvironment.execute();
    }
}
