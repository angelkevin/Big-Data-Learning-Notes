package study_flink.Flink_SQL;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
<<<<<<< HEAD
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

=======
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

>>>>>>> origin/main
public class CommonApiTest {
    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().inStreamingMode().useBlinkPlanner().build();

        //TODO 创建输入表
<<<<<<< HEAD
        String createInputDDL = "CREATE TABLE clickTable (" +
                " `user` String," +
                " `url` String," +
                " `ts` BIGINT" +
                ") WITH (" +
                " 'connector' = 'filesystem'," +
                " 'path'='D:\\java\\Fink\\123.txt'," +
                " 'format' ='csv'" +
                ")";
        tableEnv.executeSql(createInputDDL);

        //TODO TableAPI进行表查询转换
        Table clickTable = tableEnv.from("clickTable");
//        Table user = clickTable.select($("user"));

        //TODO 环境中注册零时表
        tableEnv.createTemporaryView("result1", clickTable);

        //TODO 执行SQL进行表查询
        Table table = tableEnv.sqlQuery("select user, url from result1");


        //TODO 创建输出表,输出文件系统
        String createOutDDL = "CREATE TABLE outTable (" +
                " `user` String," +
                " `url` String" +
                ") WITH (" +
                " 'connector' = 'filesystem'," +
                " 'path'='D:\\java\\Fink\\output'," +
                " 'format' ='csv'" +
                ")";
        //TODO 打印控制台
//        String createOutDDL = "CREATE TABLE outTable (" +
//                " `user` String," +
//                " `url` String" +
//                ") WITH (" +
//                " 'connector' = 'print'" +
//                ")";
        tableEnv.executeSql(createOutDDL);
        //TODO 输出到外部系统
        table.executeInsert("outTable");


=======
        String createInputDDL = "create table clickTable (" +
                " user_name String," +
                " url String," +
                " ts Bigint" +
                ") with (" +
                " `connector` = `filesystem`" +
                " `path`=`D:\\java\\Fink\\123.txt`" +
                " `format` =`csv`" +
                ")";
        tableEnv.executeSql(createInputDDL);

        //TODO 创建输出表
        String createOutDDL = "create table outTable (" +
                " user_name String," +
                " url String," +
                ") with (" +
                " `connector` = `filesystem`" +
                " `path`=`D:\\java\\Fink\\output`" +
                " `format` =`csv`" +
                ")";
        tableEnv.executeSql(createOutDDL);
>>>>>>> origin/main
    }
}
