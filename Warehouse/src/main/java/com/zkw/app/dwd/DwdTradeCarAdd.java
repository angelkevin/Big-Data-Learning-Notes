package com.zkw.app.dwd;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class DwdTradeCarAdd {
    public static void main(String[] args) {
        //TODO 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);//生产环境中为kafka的主题分区数
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
//        //开启Checkpoint
//        env.enableCheckpointing(5 * 60000L, CheckpointingMode.EXACTLY_ONCE);
//        env.getCheckpointConfig().setCheckpointTimeout(10*60000L);
//        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
//        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,5000L));
//        //设置状态后端
//         env.setStateBackend(new HashMapStateBackend());
//         env.getCheckpointConfig().setCheckpointStorage("hdfs://hadoop01:9000/211123/ck");
//         System.setProperty("HADOOP_USER_NAME","root");
        //TODO 使用DDL方式读取topic_db 主题创建表


        tableEnv.sqlQuery();


        //TODO 过滤加购数据
        //TODO 读取mysql的base_dic 作为lookup表
        //TODO 关联两个表
        //TODO 使用DDL方式创建加购事实表
        //TODO 写出
    }
}
