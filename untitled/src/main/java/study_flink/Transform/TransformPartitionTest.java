package study_flink.Transform;

import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import study_flink.Source.Event;

public class TransformPartitionTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromElements(new Event("1", "a", 10000000L), new Event("2", "pornhub", 100000L), new Event("3", "pornhub.com", 6666666666L), new Event("4", "pornhub", 100000L), new Event("5", "pornhub", 100000L), new Event("6", "pornhub.com", 6666666666L), new Event("7", "pornhub", 100000L), new Event("8", "pornhub", 100000L));
//        executionEnvironment.addSource(new RichParallelSourceFunction<Integer>() {
//            @Override
//            public void run(SourceContext<Integer> sourceContext) throws Exception {
//                for (int i = 1; i < 9; i++) {
//                    if (i % 2 == getRuntimeContext().getIndexOfThisSubtask()) {
//                        sourceContext.collect(i);
//                    }
//
//                }
//            }
//
//            @Override
//            public void cancel() {
//
//            }
//        }).setParallelism(2).rebalance().print().setParallelism(4);
//      全局分区
//        eventDataStreamSource.global().print().setParallelism(4);
//      广播分区
//        eventDataStreamSource.broadcast().print().setParallelism(4);
//      随机分区
//        eventDataStreamSource.shuffle().print().setParallelism(4);
//      并行度改变
//      轮询分区
//        eventDataStreamSource.rebalance().print().setParallelism(2);
//      rescale重缩放分区
//        eventDataStreamSource.rescale().print().setParallelism(4);
//      自定义分区
        executionEnvironment.fromElements(1,2,3,4,5,6,7,8).partitionCustom(new Partitioner<Integer>() {
            @Override
            public int partition(Integer integer, int i) {
                return integer%2;
            }
        }, new KeySelector<Integer, Integer>() {
            @Override
            public Integer getKey(Integer integer) throws Exception {
                return integer;
            }
        }).print().setParallelism(4);
        executionEnvironment.execute();
    }
}
