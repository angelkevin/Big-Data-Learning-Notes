package com.zkw.app.dim;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import com.zkw.app.bean.TableProcess;
import com.zkw.app.func.DimSinkFunction;
import com.zkw.app.func.TableProcessFunction;
import com.zkw.app.utils.MyKafkaUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.checkerframework.checker.units.qual.C;

public class DimApp {
    public static void main(String[] args) throws Exception {
        //TODO 获取执行环境
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

        //TODO 读取kafka topic_db主题创建主流
        String topic = "topic_db";
        String groupID = "dim_app_0729";
        DataStreamSource<String> kafkaDS = env.addSource(MyKafkaUtil.getFlinkKafkaConsumer(topic, groupID));
        //TODO 过滤掉非json数据以及保留新增、变化以及初始化数据
        SingleOutputStreamOperator<JSONObject> filterJSONDs = kafkaDS.flatMap(new FlatMapFunction<String, JSONObject>() {
            @Override
            public void flatMap(String value, Collector<JSONObject> out) throws Exception {
                try {
                    JSONObject jsonObject = JSON.parseObject(value);
                    String type = jsonObject.getString("type");
                    //获取新增和变化
                    if ("insert".equals(type) || "update".equals(type) || "bootstrap-insert".equals(type)) {
                        out.collect(jsonObject);
                    }
                } catch (Exception e) {
                    System.out.println("发现脏数据");
                }

            }
        });
        //TODO 使用FlinkCDC读取mysql配置信息创建配置流
        MySqlSource<String> mysqlSource = MySqlSource.<String>builder().hostname("hadoop01")
                .port(3306)
                .username("root")
                .password("123456")
                .databaseList("gmall-config")
                .tableList("gmall-config.table_process")
                .startupOptions(StartupOptions.initial())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();


        DataStreamSource<String> mysqlsource = env.fromSource(mysqlSource, WatermarkStrategy.noWatermarks(), "MySqlSource");
//        {"before":null,"after":{"source_table":"base_region","sink_table":"dim_base_region",
//        "sink_columns":"id,region_name","sink_pk":null,"sink_extend":null},
//        "source":{"version":"1.5.4.Final","connector":"mysql","name":"mysql_binlog_source","ts_ms":1666366989614,
//        "snapshot":"false","db":"gmall-config","sequence":null,"table":"table_process","server_id":0,
//        "gtid":null,"file":"","pos":0,"row":0,"thread":null,"query":null},"op":"r","ts_ms":1666366989614,"transaction":null}

        //TODO 将配置处理为广播流
        MapStateDescriptor<String, TableProcess> mapStateDescriptor = new MapStateDescriptor<>("map-state", String.class, TableProcess.class);
        BroadcastStream<String> broadcastStream = mysqlsource.broadcast(mapStateDescriptor);


        //TODO 连接广播流与主流
        BroadcastConnectedStream<JSONObject, String> connect = filterJSONDs.connect(broadcastStream);

        //TODO 处理连接流
        SingleOutputStreamOperator<JSONObject> dimDS = connect.process(new TableProcessFunction(mapStateDescriptor));


        dimDS.print(">>>>>");
        dimDS.addSink(new DimSinkFunction());
        env.execute();
    }


}
