package study_flink.Source;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;

import java.util.ArrayList;

public class Eventtest {
    public static void main(String[] args) throws Exception {
//        创建执行环境
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(new Event("asd", "asd", 456L));
        //从集合中读取数据
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromCollection(events);

        eventDataStreamSource.print();
//        DataStreamSource<String> line = executionEnvironment.readTextFile("input/user");
//        SingleOutputStreamOperator<Tuple1<String>> res = line.flatMap((String data, Collector<Tuple1<String>> out) ->
//        {
//            String[] strings = data.split(",");
//            Event event = new Event(strings[0], strings[1], Long.parseLong(strings[2]));
//            String rr = event.toString();
//            out.collect(Tuple1.of(rr));
//        }).returns(Types.TUPLE(Types.STRING));
//
//        res.print();
        executionEnvironment.execute();
    }
}
