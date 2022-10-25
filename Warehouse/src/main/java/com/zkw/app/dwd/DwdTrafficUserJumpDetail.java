package com.zkw.app.dwd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zkw.utils.MyKafkaUtil;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.PatternTimeoutFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

import java.time.Duration;
import java.util.List;
import java.util.Map;


public class DwdTrafficUserJumpDetail {
    public static void main(String[] args) throws Exception {
        //TODO 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);//生产环境中为kafka的主题分区数
//        //开启Checkpoint
//        env.enableCheckpointing(5 * 60000L, CheckpointingMode.EXACTLY_ONCE);
//        env.getCheckpointConfig().setCheckpointTimeout(10*60000L);
//        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
//        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,5000L));
//        //设置状态后端
//         env.setStateBackend(new HashMapStateBackend());
//         env.getCheckpointConfig().setCheckpointStorage("hdfs://hadoop01:9000/211123/ck");
//         System.setProperty("HADOOP_USER_NAME","root");
        //TODO 消费数据
        String topic = "dwd_traffic_page_log";
        String groupId = "user_jump_detail";
        DataStreamSource<String> kafkaDS = env.addSource(MyKafkaUtil.getFlinkKafkaConsumer(topic, groupId));
        //TODO 转化成json格式并分组
        SingleOutputStreamOperator<JSONObject> jsonObjDS = kafkaDS.map(JSON::parseObject);
        KeyedStream<JSONObject, String> keyedStream = jsonObjDS.assignTimestampsAndWatermarks(WatermarkStrategy
                        .<JSONObject>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                        .withTimestampAssigner(new SerializableTimestampAssigner<JSONObject>() {
                            @Override
                            public long extractTimestamp(JSONObject element, long recordTimestamp) {
                                return element.getLong("ts");
                            }
                        }))
                .keyBy(json -> json.getJSONObject("common").getString("mid"));
        //TODO 定义CEP的模式序列
        Pattern<JSONObject, JSONObject> pattern = Pattern.<JSONObject>begin("start").where(new SimpleCondition<JSONObject>() {
            @Override
            public boolean filter(JSONObject value) throws Exception {
                return value.getJSONObject("page").getString("last_page_id") == null;
            }
        }).next("next").where(new SimpleCondition<JSONObject>() {
            @Override
            public boolean filter(JSONObject value) throws Exception {
                return value.getJSONObject("page").getString("last_page_id") == null;
            }

        }).within(Time.seconds(10));

        //TODO 模式序列作用流上
        PatternStream<JSONObject> patternDS = CEP.pattern(keyedStream, pattern);
        OutputTag<String> timeout = new OutputTag<>("timeout");

        SingleOutputStreamOperator<String> selectDS = patternDS.select(timeout, new PatternTimeoutFunction<JSONObject, String>() {
            @Override
            public String timeout(Map<String, List<JSONObject>> pattern, long timeoutTimestamp) throws Exception {
                return pattern.get("start").get(0).toJSONString();
            }
        }, new PatternSelectFunction<JSONObject, String>() {
            @Override
            public String select(Map<String, List<JSONObject>> pattern) throws Exception {
                return pattern.get("start").get(0).toJSONString();

            }
        });
        //TODO 写入支流
        DataStream<String> timeoutDS = selectDS.getSideOutput(timeout);
        //TODO 合并两个事件
        DataStream<String> unionDS = selectDS.union(timeoutDS);

        selectDS.print("Select>>>>");
        timeoutDS.print("TimeOut>>>>");
        //TODO 写出到kafka
        String targetTopic = "dwd_traffic_user_jump_detail";
        unionDS.addSink(MyKafkaUtil.getFlinkKafkaProducer(targetTopic));

        env.execute("DwdTrafficUserJumpDetail");
    }
}
