package study_flink.Flink_SQL;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class CommonApiTest {
    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().inStreamingMode().useBlinkPlanner().build();

        //TODO 创建输入表

        String createInputDDL = "CREATE TABLE clickTable (" +
                " `user_name` String," +
                " `url` String," +
                " `ts` BIGINT" +
                ") WITH (" +
                " 'connector' = 'filesystem'," +
                " 'path'='D:\\java\\Fink\\123.txt'," +
                " 'format' ='csv'" +
                ")";
        System.out.println(createInputDDL);
        tableEnv.executeSql(createInputDDL);

        //TODO TableAPI进行表查询转换
        Table clickTable = tableEnv.from("clickTable");

        //TODO 环境中注册零时表
        tableEnv.createTemporaryView("result1", clickTable);

        //TODO 执行SQL进行表查询
        Table table = tableEnv.sqlQuery("select `user_name`, `url` from result1 where `user_name` = 'zkw'");
        //TODO 打印控制台
//        String createOutDDL = "CREATE TABLE outTable (" +
//                " `user` String," +
//                " `url` String" +
//                ") WITH (" +
//                " 'connector' = 'print'" +
//                ")";
//        tableEnv.executeSql(createOutDDL);


        //TODO 创建输出表 输出到外部系统
        String createOutDDL = "create table outTable (" +
                " `user_name` String," +
                " `url` String" +
                ") with (" +
                " 'connector' ='filesystem', " +
                " 'path'='D:\\java\\Fink\\output', " +
                " 'format' ='csv'" +
                ")";
        System.out.println(createOutDDL);
        tableEnv.executeSql(createOutDDL);
        table.executeInsert("outTable");


    }
}
