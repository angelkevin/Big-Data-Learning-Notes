```shell
flume-ng agent --name a1 --conf conf --conf-file /opt/softwares/apache-flume-1.9.0-bin/conf/flume-kafka.properties -Dflume.root.logger=INFO,console
flume-ng agent --name a2 --conf conf --conf-file /opt/softwares/apache-flume-1.9.0-bin/conf/flume-hdfs.properties -Dflume.root.logger=INFO,console
```



# Flume自定义拦截器

```java
package org.example;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class JSON {
    public static boolean isjson(String log){
        boolean flag = false;
        try {
            JSONObject.parseObject(log);
            flag = true;
        }catch (JSONException ignored){

        }
        return flag;
    }
}
```



```java
package org.example;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/**
 * 1.实现接口Interceptor
 * 2.4个构造方法
 * 3.静态内部类builder
 */
public class ETLInterceptor implements Interceptor {
    public static boolean isjson(String log) {
        boolean flag = false;
        try {
            JSONObject.parseObject(log);
            flag = true;
        } catch (JSONException ignored) {

        }
        return flag;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        byte[] body = event.getBody();
        String log = new String(body, StandardCharsets.UTF_8);

        boolean flag = isjson(log);

        return flag ? event : null;
    }

    @Override
    public List<Event> intercept(List<Event> list) {
        Iterator<Event> iterator = list.iterator();
        while (iterator.hasNext()) {
            Event event = iterator.next();
            if (intercept(event) == null) {
                iterator.remove();
            }
        }

        return list;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {

        @Override
        public Interceptor build() {
            return new ETLInterceptor();
        }

        @Override
        public void configure(Context context) {

        }
    }
}
```



# Flume自定义文件

### Flume-Kafka. Properties

```shell
#定义组件
a1.sources=r1
a1.channels=c1
a1.sinks=k1
#配置source
a1.sources.r1.type = TAILDIR
a1.sources.r1.positionFile = /opt/softwares/applog/log/taildir_position.json
a1.sources.r1.filegroups = f1
a1.sources.r1.filegroups.f1 = /opt/softwares/applog/log/app.*
a1.sources.r1.interceptors = i1
a1.sources.r1.interceptors.i1.type = org.example.ETLInterceptor$Builder
#配置channel
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100
#配置sink
a1.sinks.k1.type = org.apache.flume.sink.kafka.KafkaSink
a1.sinks.k1.kafka.topic = test
a1.sinks.k1.kafka.bootstrap.servers = centos01:9092
#拼接组件
a1.sources.r1.channels=c1
a1.sinks.k1.channel = c1
```

### Flume-HDFS.properties

```shell
#定义组件
a2.sources = r2
a2.channels = c2
a2.sinks = k2
#配置source
a2.sources.r2.type = org.apache.flume.source.kafka.KafkaSource
a2.sources.r2.kafka.bootstrap.servers = centos01:9092
a2.sources.r2.kafka.topics=test
#配置channel
a2.channels.c2.type = file
#配置sink
a2.sinks.k2.type = hdfs
a2.sinks.k2.hdfs.path = hdfs://192.168.170.133:9000/flume/%y-%m-%d
a2.sinks.k2.hdfs.fileType=DataStream
#拼接组件
a2.sinks.k2.channel = c2
a2.sources.r2.channels=c2
```
