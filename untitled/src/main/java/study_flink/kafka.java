package study_flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class kafka {
    public static void main(String[] args) throws Exception {
//        创建执行环境
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();;

        executionEnvironment.setParallelism(1);
//        从文件中读取数据
//        DataStreamSource<String> stringDataStreamSource = executionEnvironment.readTextFile("input/user");
//        从kafka中读取数据
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers","centos01:9092");
        DataStreamSource<String> kafka = executionEnvironment.addSource(new FlinkKafkaConsumer<String>("test", new SimpleStringSchema(), properties));

        kafka.print();
        executionEnvironment.execute();


    }
}
