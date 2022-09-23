package study_flink.Source;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

import java.util.Random;

public class MySourcetest {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        // 非并行数据
        // DataStreamSource<Event> eventDataStreamSource = executionEnvironment.addSource(new MySource());
        DataStreamSource<Integer> streamSource = executionEnvironment.addSource(new ParallelCustomSource()).setParallelism(2);
        streamSource.print();
        executionEnvironment.execute();
    }

    //并行数据
    public static class ParallelCustomSource implements ParallelSourceFunction<Integer> {
        private boolean running = true;
        private final Random random = new Random();

        @Override
        public void run(SourceContext<Integer> sourceContext) throws Exception {
            while (running) {
                sourceContext.collect(random.nextInt());
            }

        }

        @Override
        public void cancel() {
            running = false;

        }
    }
}
