```shell
#!/bin/bash

case $1 in
"start"){
echo ================== 启动 集群 ==================

echo     启动 Zookeeper集群
zk.sh start

echo    启动 Hadoop集群
start-all.sh

echo   启动 Kafka采集集群
kfk.sh start
echo   启动 Flume采集集群
f1.sh start
echo      启动 Flume消费集群
f2.sh start
echo     启动 maxwell
mxw.sh start
echo   启动 Flink集群
start-cluster.sh
echo      启动 Hbase
start-hbase.sh

};;
"stop"){
echo ================== 停止 集群 ==================

echo       停止 Flume消费集群
f2.sh stop

echo    停止 Flume采集集群
f1.sh stop

echo      停止 Kafka采集集群
kfk.sh stop

echo     停止 Hadoop集群
stop-all.sh

echo     停止 Zookeeper集群
zk.sh stop
echo    停止 Maxwell
mxw.sh stop
echo    停止 Flink
stop-cluster.sh
echo    停止 Hbase
stop-hbase.sh


};;
esac

```

