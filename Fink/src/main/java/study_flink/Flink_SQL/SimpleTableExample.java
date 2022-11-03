package study_flink.Flink_SQL;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.time.Duration;

import static org.apache.flink.table.api.Expressions.$;

public class SimpleTableExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<Event> eventStream = env.addSource(new MySource()).assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).
                withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                    @Override
                    public long extractTimestamp(Event element, long recordTimestamp) {
                        return element.timestamp;
                    }
                }));
        //创建表执行环境
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        //将流转换成表
        Table envTable = tableEnv.fromDataStream(eventStream);

        //直接写sql
        Table table = tableEnv.sqlQuery("select user,url from " + envTable);

        //基于table直接转换
        Table table1 = envTable.select($("user"), $("url")).where($("user").isEqual("zkw"));

        tableEnv.toDataStream(table).print("table");
        tableEnv.toDataStream(table1).print("table1");


        env.execute();


    }
}
