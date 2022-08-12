package study_flink.Sink;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;
import study_flink.Source.Event;

import java.util.Properties;

public class SinkToKafkaTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "centos01:9092");
        DataStreamSource<String> kafkadata = executionEnvironment.addSource(new FlinkKafkaConsumer<String>("test", new SimpleStringSchema(), properties));
        SingleOutputStreamOperator<String> map1 = kafkadata.map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                String[] strings = s.split(",");
                return new Event(strings[0].trim(), strings[1].trim(), Long.parseLong(strings[2])).toString();
            }
        });

        map1.addSink(new FlinkKafkaProducer<String>("centos01:9092", "test", new SimpleStringSchema()));
        executionEnvironment.execute();


    }

}
