package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
//同步
public class CustomProducerSync {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1 创建kafka生产者对象
        Properties properties = new Properties();
        //连接
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop01:9092,hadoop02:9092");
        //指定kv的序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        //2 发送数据
        for (int i = 0; i < 10; i++) {
            kafkaProducer.send(new ProducerRecord<>("first", "hello" + i)).get();

        }

        //3 关闭资源

        kafkaProducer.close();
    }
}
