package com.zkw.app.dwd;

<<<<<<< HEAD
import com.zkw.utils.MyKafkaUtil;
import com.zkw.utils.MySqlUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class DwdTradeCarAdd {
    public static void main(String[] args) throws Exception {
        //TODO 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);//生产环境中为kafka的主题分区数
        //TODO 获取table环境
=======
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class DwdTradeCarAdd {
    public static void main(String[] args) {
        //TODO 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);//生产环境中为kafka的主题分区数
>>>>>>> origin/main
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
//        //开启Checkpoint
//        env.enableCheckpointing(5 * 60000L, CheckpointingMode.EXACTLY_ONCE);
//        env.getCheckpointConfig().setCheckpointTimeout(10*60000L);
//        env.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
//        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,5000L));
//        //设置状态后端
//         env.setStateBackend(new HashMapStateBackend());
<<<<<<< HEAD
//         env.getCheckpointConfig().setCheckpointStorage("hdfs://hadoop01:9000/ck");
//         System.setProperty("HADOOP_USER_NAME","root");
        //TODO 使用DDL方式读取topic_db 主题创建表
        tableEnv.executeSql("" +
                "create table topic_db( " +
                "`database` string, " +
                "`table` string, " +
                "`type` string, " +
                "`data` map<string, string>, " +
                "`old` map<string, string>, " +
                "`ts` string, " +
                "`proc_time` as PROCTIME() " +
                ")" + MyKafkaUtil.getKafkaDDL("topic_db", "dwd_trade_cart_add"));
        //TODO 过滤加购数据
        Table cartAdd = tableEnv.sqlQuery("" +
                "select " +
                "data['id'] id, " +
                "data['user_id'] user_id, " +
                "data['sku_id'] sku_id, " +
                "data['source_id'] source_id, " +
                "data['source_type'] source_type, " +
                "if(`type` = 'insert', " +
                "data['sku_num'],cast((cast(data['sku_num'] as int) - cast(`old`['sku_num'] as int)) as string)) sku_num, " +
                "ts, " +
                "proc_time " +
                "from `topic_db`  " +
                "where `table` = 'cart_info' " +
                "and (`type` = 'insert' " +
                "or (`type` = 'update'  " +
                "and `old`['sku_num'] is not null  " +
                "and cast(data['sku_num'] as int) > cast(`old`['sku_num'] as int)))");

        tableEnv.createTemporaryView("cart_add", cartAdd);
        //TODO 读取mysql的base_dic 作为lookup表
        tableEnv.executeSql(MySqlUtil.getBaseDicLookUpDDL());
        //TODO 关联两个表
        Table resultTable = tableEnv.sqlQuery("select " +
                "cadd.id, " +
                "user_id, " +
                "sku_id, " +
                "source_id, " +
                "source_type, " +
                "dic_name source_type_name, " +
                "sku_num, " +
                "ts " +
                "from cart_add cadd " +
                "join base_dic for system_time as of cadd.proc_time as dic " +
                "on cadd.source_type=dic.dic_code");
        tableEnv.createTemporaryView("result_table", resultTable);

        //TODO 使用DDL方式创建加购事实表

        tableEnv.executeSql("" +
                "create table dwd_trade_cart_add(\n" +
                "id string,\n" +
                "user_id string,\n" +
                "sku_id string,\n" +
                "source_id string,\n" +
                "source_type_code string,\n" +
                "source_type_name string,\n" +
                "sku_num string,\n" +
                "ts string\n" +
                ")" + MyKafkaUtil.getKafkaSinkDDL("dwd_trade_cart_add")
        );

        //TODO 写出

        tableEnv.executeSql("" +
                "insert into dwd_trade_cart_add select * from result_table");


=======
//         env.getCheckpointConfig().setCheckpointStorage("hdfs://hadoop01:9000/211123/ck");
//         System.setProperty("HADOOP_USER_NAME","root");
        //TODO 使用DDL方式读取topic_db 主题创建表


        tableEnv.sqlQuery();


        //TODO 过滤加购数据
        //TODO 读取mysql的base_dic 作为lookup表
        //TODO 关联两个表
        //TODO 使用DDL方式创建加购事实表
        //TODO 写出
>>>>>>> origin/main
    }
}
