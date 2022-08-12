package study_flink.Transform;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import study_flink.Source.Event;

public class TransformKeyByTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromElements(new Event("mk", "a", 10000000L),
                new Event("zkw", "pornhub", 100000L),
                new Event("mk", "a", 10000L),
                new Event("zkw", "pornhub.com", 6666666666L));
        eventDataStreamSource.keyBy(new MyKeySelector()).max("timestamp").print("Max:");
        eventDataStreamSource.keyBy(data -> data.user).max("timestamp").print("max");
        executionEnvironment.execute();

    }

    public static class MyKeySelector implements KeySelector<Event, String> {

        @Override
        public String getKey(Event event) throws Exception {
            return event.user;
        }
    }
}
