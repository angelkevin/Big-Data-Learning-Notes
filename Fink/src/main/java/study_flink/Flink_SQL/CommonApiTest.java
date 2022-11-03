package study_flink.Flink_SQL;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class CommonApiTest {
    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().inStreamingMode().useBlinkPlanner().build();

        //TODO 创建输入表
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
    }
}
