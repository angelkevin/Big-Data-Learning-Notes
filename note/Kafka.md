``` shell
kafka-topics.sh --create  --zookeeper  centos01:2181 --replication-factor 1 --partitions 1 --topic test
# 创建主题（topic），partitions 分区数，replication-factor 副本数；
kafka-console-producer.sh --broker-list centos01:9092 --topic test
# 创建生产者
kafka-console-consumer.sh --bootstrap-server centos01:9092 --topic test bin/kafka-server-start.sh config/server.properties 
# 创建消费者
```

