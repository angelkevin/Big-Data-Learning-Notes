package study_flink.Operatortate;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BufferingSink {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Event> streamSource = env.addSource(new MySource());

        SingleOutputStreamOperator<Event> stream = streamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long l) {
                return event.timestamp;
            }
        }));

        stream.addSink(new MyBufferingSink(10));


        env.execute();


    }

    public static class MyBufferingSink implements SinkFunction<Event>, CheckpointedFunction {
        //定义当前类的属性，批量阈值
        private final int threshold;
        private List<Event> list;

        //定义算子状态
        ListState<Event> checkpointState;

        public MyBufferingSink(int threshold) {
            this.threshold = threshold;
            this.list = new ArrayList<>();
        }


        @Override
        public void invoke(Event value, Context context) throws Exception {
            list.add(value);
            if (list.size() == threshold) {
                //打印到控制台来模拟
                for (Event event : list) {
                    System.out.println(event);
                }
                System.out.println("===========================");
                list.clear();
            }
        }

        //保存状态快照到检查点时，调用这个方法
        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            //先清空状态
            checkpointState.clear();


            //对状态进行持久化的状态，复制缓存的列表到列表状态
            for (Event event : list) {
                checkpointState.add(event);
            }

        }


        //初始化算子状态。也会在恢复状态时调用
        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            //定义算子状态
            ListStateDescriptor<Event> descriptor = new ListStateDescriptor<>("list", Event.class);
            checkpointState = context.getOperatorStateStore().getListState(descriptor);
            //如果从故障恢复，需要将ListState中的所有元素复制到列表中
            if (context.isRestored()) {
                for (Event event : checkpointState.get()) {
                    list.add(event);
                }
            }

        }

    }

}
