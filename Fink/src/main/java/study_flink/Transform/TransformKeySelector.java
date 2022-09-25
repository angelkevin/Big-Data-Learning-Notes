package study_flink.Transform;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import study_flink.Source.Event;

public class TransformKeySelector {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = new StreamExecutionEnvironment().getExecutionEnvironment();
        DataStreamSource<Event> eventDataStreamSource = env.fromElements(
                new Event("mk", "a", 10000000L),
                new Event("zkw", "pornhub", 100000L),
                new Event("zkw", "pornhub.com", 6666666666L),
                new Event("zkw", "pornhub", 100000L),
                new Event("zkw", "pornhub", 100000L));
        KeyedStream<Event, String> keyBy = eventDataStreamSource.keyBy(new KeySelector<Event, String>() {
            @Override
            public String getKey(Event event) throws Exception {
                return event.user;
            }
        });
        keyBy.max("timestamp").print();

        env.execute();
    }
}
