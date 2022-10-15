package study_flink.Operatortate;

import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

public class BC {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //用户的行为数据
        DataStreamSource<Action> actionStream = env.fromElements(new Action("zkw", "login"),
                new Action("zkw", "pay"),
                new Action("jly", "login"),
                new Action("jly", "order"));

        DataStreamSource<Pattern> patternStream = env.fromElements(new Pattern("login", "pay"),
                new Pattern("login", "order"));

        //创建描述器，构建广播流
        MapStateDescriptor<Void, Pattern> descriptor = new MapStateDescriptor<>("pattern", Types.VOID, Types.POJO(Pattern.class));
        BroadcastStream<Pattern> broadcast = patternStream.broadcast(descriptor);

        //连接广播流
        actionStream.keyBy(data ->data.id).connect(broadcast).process(new KeyedBroadcastProcessFunction<String, Action, Pattern, String>() {

            ValueState<String> valueState;

            @Override
            public void open(Configuration parameters) throws Exception {
                valueState = getRuntimeContext().getState(new ValueStateDescriptor<String>("zkw",String.class));

            }

            @Override
            public void processElement(Action value, KeyedBroadcastProcessFunction<String, Action, Pattern, String>.ReadOnlyContext ctx, Collector<String> out) throws Exception {
                ReadOnlyBroadcastState<Void, Pattern> zz = ctx.getBroadcastState(new MapStateDescriptor<>("pattern", Types.VOID, Types.POJO(Pattern.class)));

                Pattern pattern = zz.get(null);
//                上一次的行为
                String value1 = valueState.value();
                if (value1!=null&&pattern!=null){
                    if (pattern.action1.equals(value1)&&pattern.action2.equals(value.action)){
                        out.collect(value.id+pattern);
                    }
                }else {
                    valueState.update(value.action);
                }
            }

            @Override
            public void processBroadcastElement(Pattern value, KeyedBroadcastProcessFunction<String, Action, Pattern, String>.Context ctx, Collector<String> out) throws Exception {
                BroadcastState<Void, Pattern> zz = ctx.getBroadcastState(new MapStateDescriptor<Void, Pattern>("pattern", Types.VOID, Types.POJO(Pattern.class)));
                zz.put(null,value);

            }
        }).print();
        env.execute();

    }


    //定义用户行为和模式的POJO类
    public static class Action {
        public String id;
        public String action;

        public Action() {

        }

        public Action(String id, String action) {
            this.id = id;
            this.action = action;
        }

        @Override
        public String toString() {
            return "Action{" +
                    "id='" + id + '\'' +
                    ", action='" + action + '\'' +
                    '}';
        }
    }

    public static class Pattern {
        public String action1;
        public String action2;

        public Pattern(String action1, String action2) {
            this.action1 = action1;
            this.action2 = action2;
        }

        public Pattern() {

        }

        @Override
        public String toString() {
            return "Pattern{" +
                    "action1='" + action1 + '\'' +
                    ", action2='" + action2 + '\'' +
                    '}';
        }
    }


}