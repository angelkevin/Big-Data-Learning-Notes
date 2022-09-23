package study_flink.Transform;


import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import study_flink.Source.Event;

public class TransformRichFunctionTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(2);
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromElements(new Event("mk", "a", 10000000L),
                new Event("zkw", "pornhub", 100000L),
                new Event("zkw", "pornhub.com", 6666666666L),
                new Event("zkw", "pornhub", 100000L),
                new Event("zkw", "pornhub", 100000L));
        eventDataStreamSource.map(new MyRichMapper()).print();
        executionEnvironment.execute();


    }

    public static class MyRichMapper extends RichMapFunction<Event,Integer>{

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            System.out.println("666"+getRuntimeContext().getIndexOfThisSubtask());
        }

        @Override
        public void close() throws Exception {
            super.close();
            System.out.println("666"+getRuntimeContext().getIndexOfThisSubtask());
        }

        @Override
        public Integer map(Event event) throws Exception {
            return event.url.length();
        }
    }
}
