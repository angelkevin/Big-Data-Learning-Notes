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

public class TimeAndWindows {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        //事件时间
        String createInputDDL = "CREATE TABLE clickTable (" +
                " `user_name` String," +
                " `url` String," +
                " `ts` BIGINT," +
                "  et AS TO_TIMESTAMP(FROM_UNIXTIME(ts/1000))," +
                "  WATERMARK FOR et AS et - INTERVAL '1' SECOND" +
                ") WITH (" +
                " 'connector' = 'filesystem'," +
                " 'path'='D:\\java\\Fink\\123.txt'," +
                " 'format' ='csv'" +
                ")";
        //在流转换成表table的时候定义时间属性
        SingleOutputStreamOperator<Event> eventStream = env.addSource(new MySource()).assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event element, long recordTimestamp) {
                return element.timestamp;
            }
        }));
        Table table = tableEnv.fromDataStream(eventStream, $("user"), $("url"), $("timestamp"), $("et").rowtime());
        table.printSchema();

    }
}
