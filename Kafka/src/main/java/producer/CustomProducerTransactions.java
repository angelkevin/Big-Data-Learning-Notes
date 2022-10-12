package producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class CustomProducerTransactions {
    public static void main(String[] args) {
        // 1 创建kafka生产者对象
        Properties properties = new Properties();
        //连接
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop01:9092,hadoop02:9092");
        //指定kv的序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());


        //指定事务ID
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,"Transactions");
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        //初始化事务
        kafkaProducer.initTransactions();
        //开始事务
        kafkaProducer.beginTransaction();

        // 2 发送数据
        try {
            for (int i = 0; i < 100; i++) {
                kafkaProducer.send(new ProducerRecord<>("test", "i:" + i));
            }
            //提交事务
            kafkaProducer.commitTransaction();
        } catch (Exception e) {
            //回滚事务
            kafkaProducer.abortTransaction();
        } finally {
            //3 关闭资源
            kafkaProducer.close();
        }


    }
}
