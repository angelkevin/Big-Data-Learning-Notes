package producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

//带回调
public class CustomProducerCallback {
    public static void main(String[] args) {
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
            kafkaProducer.send(new ProducerRecord<>("first", "hello"), (new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        System.out.println("主题:" + metadata.topic() + "分区:" + metadata.partition());
                    }

                }
            }));
        }


        //3 关闭资源

        kafkaProducer.close();
    }
}
