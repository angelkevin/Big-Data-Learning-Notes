# Flink

```shell
#启动Flink
start-cluster.sh
#命令行提交job
flink run -m centos01:8081 -c FLink.StreamWordCounts -p 2 ./******.jar
#命令行取消job
flink cancel JobID
```

```shell
#yarn模式
yarn-session.sh  -nm test
-nm		#任务名字
-d		#分离模式
-jm		#内存大小
-qu		#YARN队列名
-tm		#每个testmanager所使用的
```

一个fink程序，其实就是对DataStream的各种转换。具体来说，代码基本上由以下几种部分构成：

- 获取执行环境 execution environment
- 读取数据源 source
- 定义数据转换操作 transformtions
- 定义输出位置 sink
- 触发程序执行 execute

# Environment

```java 
package study_flink.Environment;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class env {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
//        智能环境
        LocalStreamEnvironment localEnvironment = StreamExecutionEnvironment.createLocalEnvironment();
//        本地环境
        StreamExecutionEnvironment remoteEnvironment = StreamExecutionEnvironment.createRemoteEnvironment("host", 123, "path");
//        程序执行环境 可以灵活设置
        executionEnvironment.setRuntimeMode(RuntimeExecutionMode.BATCH);
//        批处理 默认流处理
        executionEnvironment.execute("test");
    }
}
```

# Source

- kafkasource：调用addsource函数和kafka连接器

```java
package study_flink.Source;

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

```

- 自定义source：调用addsource函数，实现sourcefunction接口，实现接口中的run和cancel函数，并行度为1且不可调整

```java
package study_flink.Source;

import java.sql.Timestamp;

//类是公有的
//有一个无参的构造方法
//所有的属性都是可以序列化的和公有的


public class Event {
    public String user;
    public String url;
    public long timestamp;

    // 无参构造类
    public Event() {
    }

    public Event(String user, String url, long timestamp) {
        this.user = user;
        this.url = url;
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return "Event{" +
                "user='" + user + '\'' +
                ", url='" + url + '\'' +
                ", timestamp=" + new Timestamp(timestamp) +
                '}';
    }


}

```



```java
package study_flink.Source;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import study_flink.Source.Event;
import java.util.Calendar;
import java.util.Random;
public class MySource implements SourceFunction<Event> {
    public Boolean running = true;
    @Override
    public void run(SourceContext<Event> sourceContext) throws Exception {
        // 随机生成器
        Random random = new Random();
        // 定义字段选取的数据集
        String[] users = {"zkw", "jly", "phb", "gzn"};
        String[] urls = {"91", "pornhub", "taobao", "wzry"};
        while (running) {
            String user = users[random.nextInt(users.length)];
            String url = urls[random.nextInt(urls.length)];
            long timestamp = Calendar.getInstance().getTimeInMillis();
            sourceContext.collect(new Event(user, url, timestamp));
            Thread.sleep(5000);
        }
    }
    @Override
    public void cancel() {
        running = false;
    }
}

```

- 自定义多并行source：调用addsource函数，实现ParallelSourceFunction接口，实现接口中的run和cancel函数，并行度可调整

```java 
public static class ParallelCustomSource implements ParallelSourceFunction<Integer> {
        private boolean running = true;
        private final Random random = new Random();
        @Override
        public void run(SourceContext<Integer> sourceContext) throws Exception {
            while (running) {
                sourceContext.collect(random.nextInt());
            }
        }
        @Override
        public void cancel() {
            running = false;
        }
    }
```

# Transform

- 自定义map：实现MapFunction接口

``` java
public static class MyMapper implements MapFunction<Event, String> {
        @Override
        public String map(Event event) throws Exception {
            return event.user;
        }
    }
```

- 自定义fliter：实现 FilterFunction接口

```java
 public static class Myfilter implements FilterFunction<String> {
        @Override
        public boolean filter(String s) throws Exception {
            String[] split = s.split(",");
            return split[0].equals("Zyf");
        }
    }
```

- 自定义flatMap：实现FlatMapFunction接口

```java
public static class MYFlatMap implements FlatMapFunction<Event,String>{
        @Override
        public void flatMap(Event event, Collector<String> collector) throws Exception {
            collector.collect(event.user);
            collector.collect(event.url);
            collector.collect(String.valueOf(event.timestamp));
        }
    }
```

- 自定义keybay：实现keyselector接口

```java
 KeyedStream<Event, String> keyBy = eventDataStreamSource.keyBy(new KeySelector<Event, String>() {
            @Override
            public String getKey(Event event) throws Exception {
                return event.user;
            }
        });
```

- 自定义reduce：实现reducefunction接口,reduce是规约操作，递归

```java
 SingleOutputStreamOperator<Tuple2<String, Long>> reduce = eventDataStreamSource.map(new MapFunction<Event, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(Event event) throws Exception {
                return Tuple2.of(event.user, 1L);
            }
        }).keyBy(data -> data.f0).reduce(new ReduceFunction<Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> reduce(Tuple2<String, Long> stringLongTuple2, Tuple2<String, Long> t1) throws Exception {

                return Tuple2.of(stringLongTuple2.f0, stringLongTuple2.f1 + t1.f1);
            }
        });
```

- 自定义richmapfunction：继承RichMapFunction

```JAVA
 public static class MyRichMapper extends RichMapFunction<Event,Integer>{

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            System.out.println("666"+getRuntimeContext().getIndexOfThisSubtask());
        }

        @Override
        public void close() throws Exception {
            super.close();
            System.out.println("666"+getRuntimeContext().getIndexOfThisSubtask());
        }

        @Override
        public Integer map(Event event) throws Exception {
            return event.url.length();
        }
    }
```

- 物理分区，防止数据倾斜

```java 
package study_flink.Transform;

import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import study_flink.Source.Event;

public class TransformPartitionTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromElements(new Event("1", "a", 10000000L), new Event("2", "pornhub", 100000L), new Event("3", "pornhub.com", 6666666666L), new Event("4", "pornhub", 100000L), new Event("5", "pornhub", 100000L), new Event("6", "pornhub.com", 6666666666L), new Event("7", "pornhub", 100000L), new Event("8", "pornhub", 100000L));
//        executionEnvironment.addSource(new RichParallelSourceFunction<Integer>() {
//            @Override
//            public void run(SourceContext<Integer> sourceContext) throws Exception {
//                for (int i = 1; i < 9; i++) {
//                    if (i % 2 == getRuntimeContext().getIndexOfThisSubtask()) {
//                        sourceContext.collect(i);
//                    }
//
//                }
//            }
//
//            @Override
//            public void cancel() {
//
//            }
//        }).setParallelism(2).rebalance().print().setParallelism(4);
//      全局分区
//        eventDataStreamSource.global().print().setParallelism(4);
//      广播分区
//        eventDataStreamSource.broadcast().print().setParallelism(4);
//      随机分区
//        eventDataStreamSource.shuffle().print().setParallelism(4);
//      并行度改变
//      轮询分区
//        eventDataStreamSource.rebalance().print().setParallelism(2);
//      rescale重缩放分区
//        eventDataStreamSource.rescale().print().setParallelism(4);
//      自定义分区
        executionEnvironment.fromElements(1,2,3,4,5,6,7,8,9,10).partitionCustom(new Partitioner<Integer>() {
            @Override
            public int partition(Integer integer, int i) {
                return integer%2;
            }
        }, new KeySelector<Integer, Integer>() {
            @Override
            public Integer getKey(Integer integer) throws Exception {
                return integer;
            }
        }).print().setParallelism(4);
        executionEnvironment.execute();
    }
}
```



# Sink

- Sink to File：调用addsink函数，自定义StreamingFileSink因为其实protect所以要调用其下面的方法forRowFormat并传入参数等等中的.build方法来构建，中间也可以加一些回滚结束条件调用withRollingPolicy，然后传入DefaultRollingPolicy.builder()来构建这个类，然后就可以调用结束回滚条件.withMaxPartSize(1024*1024*1024).withRolloverInterval(TimeUnit.MINUTES.toMillis(15)).withInactivityInterval(TimeUnit.MINUTES.toMillis(5)).build()并最后使用build来构建

```java
package study_flink.Sink;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import study_flink.Source.Event;

import java.util.concurrent.TimeUnit;

public class FlieTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Event> eventDataStreamSource = env.fromElements(new Event("mk", "a", 10000000L),
                new Event("zkw", "pornhub", 100000L),
                new Event("zkw", "pornhub.com", 6666666666L),
                new Event("zkw", "pornhub", 100000L),
                new Event("zkw", "pornhub", 100000L));

        StreamingFileSink<String> build = StreamingFileSink.<String>forRowFormat(new Path("./output"), new SimpleStringEncoder<>("UTF-8"))
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withMaxPartSize(1024*1024*1024)
                                .withRolloverInterval(TimeUnit.MINUTES.toMillis(15))
                                .withInactivityInterval(TimeUnit.MINUTES.toMillis(5))
                                .build())
                .build();
        eventDataStreamSource.map(data -> data.toString()).addSink(build);
         env.execute();
    }
}

```

- kafka sink

```java 
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

```

- 自定义sink：实现RichSinkFunction<String>() 或者SinkFunction<String>()接口

# assignTimestampsAndWatermarks

- 水位线，分配时间戳和水位线，调用assignTimestampsAndWatermarks，接口WatermarkStrategy，调用里面的静态方法forBoundedOutOfOrderness(乱序)，forMonotonousTimestamps(有序)返回WatermarkStrategy，然后调用withTimestampAssigner重写SerializableTimestampAssigner里面的方法

```java 
eventDataStreamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forMonotonousTimestamps().withTimestampAssigner(
                new SerializableTimestampAssigner<Event>() {
                    //抽取时间戳
                    @Override
                    public long extractTimestamp(Event event, long l) {
                        return event.timestamp;
                    }
                }
        ));
        //乱序流的watermark
        //插入水位线逻辑，延迟设置
        eventDataStreamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO)
                .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
                    //抽取时间戳
                    @Override
                    public long extractTimestamp(Event event, long l) {
                        return event.timestamp;
                    }
                })
        );
```

- 自定义WatermarkStrategy，实现WatermarkGenerator(自定义waterMark并发射出去)return watermarkgenerator，实现里面的on Event（获取水位线）和onPeriodicEmit（发射水位线）调用其中的emitwatermark并new一个watermark，与TimestampAssigner(提取时间戳)return new SerializableTimestampAssigner 并实现里面的extractTimestamp，提取时间戳，告诉程序哪个是时间戳，

```java
 eventDataStreamSource.assignTimestampsAndWatermarks(new WatermarkStrategy<Event>() {
            @Override
            public WatermarkGenerator<Event> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
                return new WatermarkGenerator<Event>() {
                    private Long delayTime = 5000L;
                    private Long max = Long.MIN_VALUE+delayTime+1L;
                    @Override
                    public void onEvent(Event event, long l, WatermarkOutput watermarkOutput) {
                        max = Math.max(event.timestamp,max);
                    }
                    @Override
                    public void onPeriodicEmit(WatermarkOutput watermarkOutput) {
                        watermarkOutput.emitWatermark(new Watermark(max-delayTime-1L));      
                    }
                };
            }
            @Override
            public TimestampAssigner<Event> createTimestampAssigner(TimestampAssignerSupplier.Context context) {
                return new SerializableTimestampAssigner<Event>() {
                    @Override
                    public long extractTimestamp(Event event, long l) {
                        return event.timestamp; //告诉程序哪个字段是时间戳
                    }
                };
            }
        });
```



# Window窗口

>- 时间窗口
>- 计数窗口

>- 滚动窗口（Tumbling Window）：对数据进行均匀切片，时间或计数，定义窗口有多大
>- 滑动窗口（Sliding Window）：除去窗口大小，还有滑动步长，窗口会出现重叠， 
>- 会话窗口（Session Window）：基于会话对数据进行分析，设施会话超时时间
>- 全局窗口：自定义触发器
>

需要水位线，先keyby后才可以调用.window()方法，这个方法需要传入一个windowassigner作为参数，然后调用reduce，aggregate等方法实现



## reduce

```java
package study_flink.window;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import study_flink.Source.Event;

public class WindowTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.fromElements(
                new Event("mk", "a", 100000L),
                new Event("zkw", "pornhub", 100000L),
                new Event("zkw", "pornhub.com", 200000L),
                new Event("zkw", "pornhub", 100000L),
                new Event("zkw", "pornhub", 100000L));
        SingleOutputStreamOperator<Event> eventSingleOutputStreamOperator = eventDataStreamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forMonotonousTimestamps().withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long l) {
                return event.timestamp;
            }
        }));
        SingleOutputStreamOperator<Tuple2<String, Long>> reduce = eventSingleOutputStreamOperator.map(new MapFunction<Event, Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> map(Event event) throws Exception {
                        return Tuple2.of(event.user, 1L);
                    }
                })
                .keyBy(data -> data.f0)
                .window(TumblingEventTimeWindows.of(Time.seconds(100000))) //滚动事件时间窗口
                .reduce(new ReduceFunction<Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> reduce(Tuple2<String, Long> stringLongTuple2, Tuple2<String, Long> t1) throws Exception {
                        return Tuple2.of(stringLongTuple2.f0, stringLongTuple2.f1 + t1.f1);
                    }
                });
        reduce.print();
        executionEnvironment.execute();
    }
}

```



## aggregate

```java
 eventSingleOutputStreamOperator.keyBy(data -> data.user).window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .aggregate(
                        new AggregateFunction<Event, Tuple2<Long,Integer>, String>() {
                            // 初始化，只调用一次
                            @Override
                            public Tuple2<Long, Integer> createAccumulator() {
                                return Tuple2.of(0L,0);
                            }

                            @Override
                            public Tuple2<Long, Integer> add(Event event, Tuple2<Long, Integer> longIntegerTuple2) {
                                return Tuple2.of(longIntegerTuple2.f0+event.timestamp,longIntegerTuple2.f1+1);
                            }

                            @Override
                            public String getResult(Tuple2<Long, Integer> longIntegerTuple2) {
                                Timestamp timestamp = new Timestamp(longIntegerTuple2.f0 / longIntegerTuple2.f1);
                                return timestamp.toString();
                            }

                            @Override
                            public Tuple2<Long, Integer> merge(Tuple2<Long, Integer> longIntegerTuple2, Tuple2<Long, Integer> acc1) {
                                return Tuple2.of(longIntegerTuple2.f0+acc1.f0,longIntegerTuple2.f1+acc1.f1 );
                            }
                        }
                ).print();
```

```java
package study_flink.window;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.util.HashSet;

public class AggregateTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Event> streamSource = env.addSource(new MySource());
        SingleOutputStreamOperator<Event> watermarks = streamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forMonotonousTimestamps().withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long l) {
                return event.timestamp;
            }
        }));
        streamSource.print("data");
        watermarks.keyBy(data -> "key").window(TumblingEventTimeWindows.of(Time.seconds(10))).aggregate(
                new AggregateFunction<Event, Tuple2<Long, HashSet<String>>, Double>() {
                    @Override
                    public Tuple2<Long, HashSet<String>> createAccumulator() {
                        return Tuple2.of(0L,new HashSet<>());
                    }

                    @Override
                    public Tuple2<Long, HashSet<String>> add(Event event, Tuple2<Long, HashSet<String>> longHashSetTuple2) {
                        longHashSetTuple2.f1.add(event.user);


                        return Tuple2.of(longHashSetTuple2.f0+1,longHashSetTuple2.f1);
                    }

                    @Override
                    public Double getResult(Tuple2<Long, HashSet<String>> longHashSetTuple2) {
                        //输出的值
                        return (double) (longHashSetTuple2.f0 / longHashSetTuple2.f1.size());
                    }

                    @Override
                    public Tuple2<Long, HashSet<String>> merge(Tuple2<Long, HashSet<String>> longHashSetTuple2, Tuple2<Long, HashSet<String>> acc1) {
                        return null;
                    }
                }

        ).print();
        env.execute();

    }
}

```

##  ProcessWindowFunction

process后实现ProcessWindowsFunction<IN,OUT,KEY,WINDOW>

```java 
package study_flink.window;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.sql.Timestamp;
import java.util.HashSet;

public class AllWindowFunction {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Event> streamSource = env.addSource(new MySource());
        streamSource.print("input");
        SingleOutputStreamOperator<Event> watermarks = streamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forMonotonousTimestamps().withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long l) {
                return event.timestamp;
            }
        }));
        watermarks.keyBy(data -> true).window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .process(new ProcessWindowFunction<Event, String,  Boolean, TimeWindow>() {
                    @Override
                    public void process(Boolean aBoolean, ProcessWindowFunction<Event, String, Boolean, TimeWindow>.Context context, Iterable<Event> iterable, Collector<String> collector) throws Exception {
                        HashSet<String> hashSet = new HashSet<>();
                        // 从interable遍历数据进行操作
                        for (Event event : iterable) {
                            hashSet.add(event.user);
                        }
                        Long uv = (long) hashSet.size();
                        //
                        long start = context.window().getStart();
                        long end = context.window().getEnd();
                        collector.collect("窗口" + new Timestamp(start) + "~" + new Timestamp(end)
                        +"\n" + "UV:" + uv);

                    }
                }).print();
        env.execute();
    }
}

```

## Process and aggregate

两个一起调用相互弥补，Process 可以拿到窗口信息，aggregate可以流处理

```java
package study_flink.window;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.sql.Timestamp;
import java.util.HashSet;

public class Processandaggregate {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Event> streamSource = env.addSource(new MySource());
        streamSource.print("input");
        SingleOutputStreamOperator<Event> watermarks = streamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forMonotonousTimestamps().withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long l) {
                return event.timestamp;
            }
        }));

        watermarks.keyBy(data -> true).window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .aggregate(new Uvagg(),new UVCountResult()).print();
    env.execute();
    }

    // 自定义实现AggregateFunction<in,acc,out>in是输入数据类型，acc是累加器的类型，out是输出数据类型
    public static class Uvagg implements AggregateFunction<Event, HashSet<String>,Long> {
        @Override
        public HashSet<String> createAccumulator() { //初始化累加器
            return new HashSet<>();
        }

        @Override
        public HashSet<String> add(Event event, HashSet<String> strings) { //累加器进行累加
            strings.add(event.user);
            return strings;
        }

        @Override
        public Long getResult(HashSet<String> strings) { //输出结果
            return (long) strings.size();
        }
        @Override
        public HashSet<String> merge(HashSet<String> strings, HashSet<String> acc1) { // 不是会话窗口，没必要
            return null;
        }
    }
    //ProcessWindowFunction<in，out，key，window>
    public static class UVCountResult extends ProcessWindowFunction<Long,String, Boolean, TimeWindow>{

        @Override
        //process(KEY var1, ProcessWindowFunction<IN, OUT, KEY, W>.Context var2, Iterable<IN> var3, Collector<OUT> var4)
        public void process(Boolean aBoolean, ProcessWindowFunction<Long, String, Boolean, TimeWindow>.Context context, Iterable<Long> iterable, Collector<String> collector) throws Exception {
            long start = context.window().getStart();
            long end = context.window().getEnd();
            long uv = iterable.iterator().next();
            collector.collect("窗口" + new Timestamp(start) + "~" + new Timestamp(end)
                    +"\n" + "UV:" + uv); //输出
        }
    }
}

```

## 乱序数据

在flink中，处理乱序数据有延迟水位线，设置延迟等待，以及侧流等方法，延迟水位线相当于乘客把自己的表调慢，延迟等待相当于延迟发车时间

```java
package study_flink.window;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.time.Duration;

public class LateDataTest {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        SingleOutputStreamOperator<Event> map = env.socketTextStream("192.168.170.133", 7777).map(
                new MapFunction<String, Event>() {
                    @Override
                    public Event map(String s) throws Exception {
                        String[] split = s.split(",");
                        return new Event(split[0].trim(), split[1].trim(), Long.parseLong(split[2].trim()));
                    }
                }
        );
        SingleOutputStreamOperator<Event> watermarks = map.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(2)).withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long l) {
                return event.timestamp;
            }
        }));
        OutputTag<Event> late = new OutputTag<Event>("late") {
        };//设置延时标签
        SingleOutputStreamOperator<UrlViewCount> aggregate = watermarks.keyBy(data -> data.url)
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .allowedLateness(Time.minutes(1)) //允许延时
                .sideOutputLateData(late) //侧流输出
                .aggregate(new UrlCount(), new UrlInfo());
        aggregate.print("result");
        aggregate.getSideOutput(late).print();
        env.execute();
    }

    public static class UrlCount implements AggregateFunction<Event, Long, Long> {

        @Override
        public Long createAccumulator() {
            return 0L;
        }

        @Override
        public Long add(Event event, Long aLong) {
            return aLong + 1;
        }

        @Override
        public Long getResult(Long aLong) {
            return aLong;
        }

        @Override
        public Long merge(Long aLong, Long acc1) {
            return null;
        }
    }

    public static class UrlInfo extends ProcessWindowFunction<Long, UrlViewCount, String, TimeWindow> {
        @Override
        public void process(String s, ProcessWindowFunction<Long, UrlViewCount, String, TimeWindow>.Context context, Iterable<Long> iterable, Collector<UrlViewCount> collector) throws Exception {
            long start = context.window().getStart();
            long end = context.window().getEnd();
            long count = iterable.iterator().next();
            collector.collect(new UrlViewCount(s, count, start, end));

        }
    }

}
```

# ProcessFunction

更加底层，在datastream后调用process，实现processfunction，每来一次数据就调用一次，可以获得更加底层的东西他是继承richfunction

```java
package study_flink.ProcessFunctionTest;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.time.Duration;

public class ProcessFunctionTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Event> streamSource = env.addSource(new MySource());
        SingleOutputStreamOperator<Event> watermarks = streamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long l) {
                return event.timestamp;
            }
        }));

        watermarks.process(new ProcessFunction<Event, String>() {
            @Override
            public void processElement(Event event, ProcessFunction<Event, String>.Context context, Collector<String> out) throws Exception {
                if (event.user.equals("zkw")){
                    out.collect(event.user + event.url);
                }
                out.collect(event.toString());
                System.out.println(context.timestamp());
                System.out.println(context.timerService().currentWatermark());

            }
        }).print();


        env.execute();
    }
}
```

## Time Timer 定时器，触发器

分为事件时间触发器定时器和处理时间触发器定时器，基于KeyedStream，然后调用process方法，实现其中的KeyedProcessFunction方法

### 事件时间触发器

```java 
package study_flink.ProcessFunctionTest;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.sql.Timestamp;
import java.time.Duration;

public class EventTimeTimer {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Event> streamSource = env.addSource(new MySource());
        SingleOutputStreamOperator<Event> watermarks = streamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long l) {
                return event.timestamp;
            }
        }));

        watermarks.keyBy(data -> data.user).process(
                new KeyedProcessFunction<String, Event, String>() {
                    @Override
                    public void processElement(Event event, KeyedProcessFunction<String, Event, String>.Context context, Collector<String> collector) throws Exception {
                        long currTs = event.timestamp;
                        collector.collect(context.getCurrentKey() + "数据到达，时间戳:" + new Timestamp(currTs) + "waterMark：" + context.timerService().currentWatermark());
                        context.timerService().registerEventTimeTimer(currTs + 10 * 1000);
                    }

                    @Override
                    public void onTimer(long timestamp, KeyedProcessFunction<String, Event, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
                        out.collect(ctx.getCurrentKey() + "定时器触发，触发时间" + new Timestamp(timestamp));
                    }
                }
        ).print();
        env.execute();
    }
}

```

### 处理时间触发器

```java
package study_flink.ProcessFunctionTest;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;
import java.sql.Timestamp;

public class ProcessTimeTimerTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Event> streamSource = env.addSource(new MySource());
        streamSource.keyBy(data -> data.user).process(
                new KeyedProcessFunction<String, Event, String>() {
                    @Override
                    public void processElement(Event event, KeyedProcessFunction<String, Event, String>.Context context, Collector<String> collector) throws Exception {
                        long currTs = context.timerService().currentProcessingTime();
                        collector.collect(context.getCurrentKey()+"数据到达，到达时间:" + new Timestamp(currTs));
                        context.timerService().registerProcessingTimeTimer(currTs+10*1000L);
                    }
                    @Override
                    public void onTimer(long timestamp, KeyedProcessFunction<String, Event, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
                        out.collect(ctx.getCurrentKey()+"定时器触发，触发时间"+new Timestamp(timestamp));
                    }
                }
        ).print();
        env.execute();
    }
}
```

## TopN示例

使用windowall的开窗方法，在datastream后调用，并分配窗口生成器，之后调用aggregate方法实现里面的AggregateFunction接口和ProcessAllWindowFunction这个richfunction类用来包装输出

```java
package study_flink.ProcessFunctionTest;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class TopN {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Event> dataStreamSource = env.addSource(new MySource());
        SingleOutputStreamOperator<Event> watermarks = dataStreamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ofSeconds(0)).withTimestampAssigner(
                new SerializableTimestampAssigner<Event>() {
                    @Override
                    public long extractTimestamp(Event event, long l) {
                        return event.timestamp;
                    }
                }
        ));

        watermarks.map(data -> data.url)
                .windowAll(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                .aggregate(new count(),
                        new allwindowsprocess()).print();


        env.execute();
    }

    public static class count implements AggregateFunction<String, HashMap<String, Integer>, ArrayList<Tuple2<String, Integer>>> {
        @Override
        public HashMap<String, Integer> createAccumulator() {
            return new HashMap<>();
        }

        @Override
        public HashMap<String, Integer> add(String url, HashMap<String, Integer> stringIntegerHashMap) {
            if (stringIntegerHashMap.containsKey(url)) {
                int i = stringIntegerHashMap.get(url) + 1;
                stringIntegerHashMap.put(url, i);
            } else {
                stringIntegerHashMap.put(url, 1);
            }
            return stringIntegerHashMap;
        }

        @Override
        public ArrayList<Tuple2<String, Integer>> getResult(HashMap<String, Integer> stringIntegerHashMap) {
            ArrayList<Tuple2<String, Integer>> tuple2s = new ArrayList<>();
            for (String s : stringIntegerHashMap.keySet()) {
                tuple2s.add(Tuple2.of(s, stringIntegerHashMap.get(s)));
            }

            tuple2s.sort(new Comparator<Tuple2<String, Integer>>() {
                @Override
                public int compare(Tuple2<String, Integer> o1, Tuple2<String, Integer> o2) {
                    return o2.f1 - o1.f1;
                }
            });
            return tuple2s;
        }

        @Override
        public HashMap<String, Integer> merge(HashMap<String, Integer> stringIntegerHashMap, HashMap<String, Integer> acc1) {
            return null;
        }
    }

    public static class allwindowsprocess extends ProcessAllWindowFunction<ArrayList<Tuple2<String, Integer>>, String, TimeWindow> {
        @Override
        public void process(ProcessAllWindowFunction<ArrayList<Tuple2<String, Integer>>, String, TimeWindow>.Context context,
                            Iterable<ArrayList<Tuple2<String, Integer>>> iterable, Collector<String> collector) throws Exception {
            ArrayList<Tuple2<String, Integer>> list = iterable.iterator().next();
            collector.collect("窗口开始时间" + new Timestamp(context.window().getStart()) + "\n" + "TOP1：" + list.get(0).f0 + "浏览量为：" + list.get(0).f1 + "\n" +
                    "TOP2：" + list.get(1).f0 + "浏览量为：" + list.get(1).f1 + "\n" + "窗口结束时间" + new Timestamp(context.window().getEnd())+"\n");
        }
    }
}
```

# 分流&合流

## SplitStream	分流

```java
package study_flink.SplitStream;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.time.Duration;

public class SplitStream {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Event> eventDataStreamSource = executionEnvironment.addSource(new MySource());
        SingleOutputStreamOperator<Event> eventSingleOutputStreamOperator = eventDataStreamSource
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Event>forBoundedOutOfOrderness(Duration.ZERO)
                        .withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event element, long recordTimestamp) {
                return element.timestamp;
            }
        }));

        OutputTag<Tuple2<String, String>> zkw = new OutputTag<Tuple2<String, String>>("zkw"){};

        SingleOutputStreamOperator<Event> zkw1 = eventSingleOutputStreamOperator.process(new ProcessFunction<Event, Event>() {
            @Override
            public void processElement(Event value, ProcessFunction<Event, Event>.Context ctx, Collector<Event> out) throws Exception {
                if (value.user.equals("zkw")) {
                    ctx.output(zkw, Tuple2.of(value.user, value.url));
                }
                else
                    out.collect(value);
            }
        });

        zkw1.print("else");
        zkw1.getSideOutput(zkw).print("zkw");
        executionEnvironment.execute();
    }
}

```

创建一个OutputTag对象并实现匿名类，然后再processfunction里面直接调用output方法，然后传入OutputTag，就可以分流

## Connect	合流

两个数据类型不同的datastream可以调用Connect进行合并，stream1.connect（stream2），然后调用process实现里面的coprocerssfunction进行合并操作

```java
package study_flink.SplitStream;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class Connect {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<Tuple3<String, String, Long>> stream1 = env.fromElements(
                Tuple3.of("order1", "app", 1000L),
                Tuple3.of("order2", "app", 2000L),
                Tuple3.of("order3", "app", 7000L)

        ).assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple3<String, String, Long>>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(
                new SerializableTimestampAssigner<Tuple3<String, String, Long>>() {
                    @Override
                    public long extractTimestamp(Tuple3<String, String, Long> element, long recordTimestamp) {
                        return element.f2;
                    }
                }
        ));

        SingleOutputStreamOperator<Tuple4<String, String, String, Long>> stream2 = env.fromElements(
                Tuple4.of("order1", "third-party", "success", 3000L),
                Tuple4.of("order3", "third-party", "success", 4000L)

        ).assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple4<String, String, String, Long>>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(
                new SerializableTimestampAssigner<Tuple4<String, String, String, Long>>() {
                    @Override
                    public long extractTimestamp(Tuple4<String, String, String, Long> element, long recordTimestamp) {
                        return element.f3;
                    }
                }
        ));
//        检测同一支付单在两条流中是否匹配，不匹配就报警
        stream1.connect(stream2).keyBy(data -> data.f0, data1 -> data1.f0)
                .process(new OrderMatchResult()).print();


        env.execute();
    }
    public static class OrderMatchResult extends CoProcessFunction<Tuple3<String, String, Long>, Tuple4<String, String, String, Long>, String> {
        //定义状态变量，用来保存已经到达的事件
        private ValueState<Tuple3<String, String, Long>> app;
        private ValueState<Tuple4<String, String, String, Long>> third;

        @Override
        public void open(Configuration parameters) throws Exception {
            app = getRuntimeContext().getState(new ValueStateDescriptor<Tuple3<String, String, Long>>("app", Types.TUPLE(Types.STRING, Types.STRING, Types.LONG)));
            third = getRuntimeContext().getState(new ValueStateDescriptor<Tuple4<String, String, String, Long>>("third", Types.TUPLE(Types.STRING, Types.STRING, Types.STRING, Types.LONG)));
        }

        @Override
        public void processElement1(Tuple3<String, String, Long> value, Context ctx, Collector<String> out) throws Exception {
            if (third.value() != null) {
                out.collect("对账成功" + value + third.value());
                third.clear();
            } else {
                app.update(value);
                //注册一个五秒的定时器
                ctx.timerService().registerEventTimeTimer(value.f2 +5000L);
            }

        }

        @Override
        public void processElement2(Tuple4<String, String, String, Long> value, Context ctx, Collector<String> out) throws Exception {

            if (app.value() != null) {
                out.collect("对账成功" + value + app.value());
                app.clear();
            } else {
                third.update(value);
                ctx.timerService().registerEventTimeTimer(value.f3);
            }
        }
        @Override
        public void onTimer(long timestamp, CoProcessFunction<Tuple3<String, String, Long>, Tuple4<String, String, String, Long>, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
            if (app.value() != null) {
                System.out.println(666);
                out.collect("第三方失败");
            }
            if (third.value() != null) {
                out.collect("app失败");
            }
            app.clear();
            third.clear();

        }
    }
}

```

## Union 合流

用来合并两个相同数据类型的数据流

## WindowJion合并窗口

datastream1 join datastream2 先 where 传入一个 keyselector 再 equalTo 传入一个 keyselector ，然后开窗，最后得到的是两个流再这个窗口里面的数据的笛卡尔积

stream1.join(stream2).where(keyseletor).equalto(keyselector).window().apply

```java
stream1.join(stream2).where(data -> data.f0).equalTo(data1 -> data1.f0).window(TumblingEventTimeWindows.of(Time.seconds(5)))
        .apply(new JoinFunction<Tuple3<String, String, Long>, Tuple4<String, String, String, Long>, String>() {
            @Override
            public String join(Tuple3<String, String, Long> first, Tuple4<String, String, String, Long> second) throws Exception {
                return first + "," + second;
            }
        });
```

## intervaljoin间隔连接窗口

stream1.keyby(keyselector).intervaljion(stream2.keyby(keyseleter)).process(new processjoinfunction())

```java
package study_flink.SplitStream;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class IntervalJoin {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SingleOutputStreamOperator<Tuple3<String, String, Long>> stream1 = env.fromElements(
                Tuple3.of("zkw", "app", 1000L),
                Tuple3.of("jly", "app", 2000L),
                Tuple3.of("zkw", "app", 3500L)
        ).assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple3<String, String, Long>>forBoundedOutOfOrderness(Duration.ofSeconds(0)).withTimestampAssigner(
                new SerializableTimestampAssigner<Tuple3<String, String, Long>>() {
                    @Override
                    public long extractTimestamp(Tuple3<String, String, Long> element, long recordTimestamp) {
                        return element.f2;
                    }
                }
        ));
        SingleOutputStreamOperator<Tuple4<String, String, String, Long>> stream2 = env.fromElements(
                Tuple4.of("zkw", "third-party", "success", 2000L),
                Tuple4.of("zkw", "third-party", "success", 3000L),
                Tuple4.of("jly", "third-party", "success", 4000L)
        ).assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple4<String, String, String, Long>>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(
                new SerializableTimestampAssigner<Tuple4<String, String, String, Long>>() {
                    @Override
                    public long extractTimestamp(Tuple4<String, String, String, Long> element, long recordTimestamp) {
                        return element.f3;
                    }
                }
        ));

        stream1.keyBy(data -> data.f0).intervalJoin(stream2.keyBy(data1 -> data1.f0)).between(Time.seconds(-2), Time.seconds(2)).process(
                new ProcessJoinFunction<Tuple3<String, String, Long>, Tuple4<String, String, String, Long>, String>() {
                    @Override
                    public void processElement(Tuple3<String, String, Long> left, Tuple4<String, String, String, Long> right, ProcessJoinFunction<Tuple3<String, String, Long>, Tuple4<String, String, String, Long>, String>.Context ctx, Collector<String> out) throws Exception {
                        out.collect(right + "=>" + left);
                    }
                }
        ).print();


        env.execute();

    }
}

```

## cogroup窗口组连接

stream1.coGroup(stream2).where(keyseletor).equalto(keyselector).window().apply()

一个集合匹配另外一个集合

# 状态

在流处理中，数据是连续不断的到来和处理。每个任务进行计算的时候，可以基于当前数据直接得到转换输出结果，也可以作为一些依赖，这些由一个任务维护并且用来输出计算结果的所有数据就叫做状态。

在流处理中,数据是连续不断的到来的和处理的,每个任务进行计算和处理的时候,可以基于当前数据直接转换给出结果;也可以依赖一些别的数据。这些由一个任务维护,并且用来计算出结果的所有数据,就叫作这个任务的状态。

在Flink中,算子任务可以分为有状态和无状态的两种情况

- 无状态只需观察每个独立的事件，根据当前输入的数据直接转化输出结果，如flatMap，map，filter，计算时不依赖其他的数据，叫做无状态

- 有状态的算子任务是除了当前的数据之外，还需要一些其他的数据来得到计算结果，这里的其他数据就是所谓的状态，最常见的是之前到达的数据，或者由之前数据计算出的某个结果，比如sum，聚合算子，窗口算子

直接把状态保存到内存里面来保存性能，并且通过分布式扩展来提高吞吐量。

## 状态的分类

- 托管状态：由Flink状态统一管理，由Runtime进行托管，在配置容错机制后，全部由Flink来管理，状态内容有：值状态valuestate，列表状态liststate，映射状态mapstate，聚合状态aggregatestate等状态，也可以通过复函数类中的上下文在自定义状态。
- 原始状态：需要我们自己去定义。

### 算子状态和按键分区状态

1.算子状态：状态的作用范围为当前的算子任务实例，也就是值对当前并行子任务实例有效，这就意味着，每一个并行子任务占据了一个分区，他处理的所有数据都会访问到相同的状态，状态对于一个任务而言是共享的。算子状态可以用在所有的算子上，和本地变量没有什么区别，因为本地的变量的作用域也就是当前的任务实例中，在使用中，我们还要进一步实现checkpiontfunction

2.按键分区状态：是根据输入流中的键来维护和访问，所有只能定义在按键分区流中，也就是keyby后才可以使用，按键分区状态应用广泛。所有的聚合算子必须在keybay后才可以使用，因为聚合的结果就是以按键分区状态保存的。在底层，所有状态都会和key保存成键值对的形式，具有相同的key的值可以访问到相同的状态，而不同的是被隔离的。

3.他们都是在本地实例上维护的，每个并行子任务维护对应的状态。keyedstate按照key维护一组状态。

## 按键分区状态keyedstate

```java
package study_flink.State;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.time.Duration;

public class valueState<E> {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        SingleOutputStreamOperator<Event> stream = env.addSource(new MySource()).assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long recordTimestamp) {
                return event.timestamp;
            }
        }));
        stream.keyBy(data -> data.user).flatMap(new MyFlatMap()).print();


        env.execute();
    }

    //实现自定义的RichFlatMapFunction
    public static class MyFlatMap extends RichFlatMapFunction<Event, String> {

        //定义状态
        ValueState<Event> myValueState;
        ListState<Event> myListState;
        MapState<String, Long> myMapState;
        ReducingState<Event> myReducingState;
        AggregatingState<Event, String> myAggregatingState;

        @Override
        public void open(Configuration parameters) throws Exception {
            myValueState = getRuntimeContext().getState(new ValueStateDescriptor<Event>("myValueState", Event.class));
            myListState = getRuntimeContext().getListState(new ListStateDescriptor<Event>("myListState", Event.class));
            myMapState = getRuntimeContext().getMapState(new MapStateDescriptor<String, Long>("myMapState", String.class, Long.class));
            myReducingState = getRuntimeContext().getReducingState(new ReducingStateDescriptor<Event>("myReducingState", new ReduceFunction<Event>() {
                @Override
                public Event reduce(Event value1, Event value2) throws Exception {
                    return new Event(value1.user, value2.url, value1.timestamp);
                }
            }, Event.class));
            myAggregatingState = getRuntimeContext().getAggregatingState(new AggregatingStateDescriptor<Event, Long, String>("myAggregatingState", new AggregateFunction<Event, Long, String>() {
                @Override
                public Long createAccumulator() {

                    return 0L;
                }

                @Override
                public Long add(Event value, Long accumulator) {
                    return accumulator + 1;
                }

                @Override
                public String getResult(Long accumulator) {
                    return "count:" + accumulator;
                }

                @Override
                public Long merge(Long a, Long b) {
                    return a + b;
                }
            }, Long.class));


        }

        @Override
        public void flatMap(Event value, Collector<String> out) throws Exception {


            myValueState.update(value);
            System.out.println("myValueState: " + myValueState.value());

            myListState.add(value);
            System.out.println("myListState: " + myListState.toString());

            myMapState.put(value.user, myMapState.get(value.user) == null ? 1 : myMapState.get(value.user) + 1);
            System.out.println("myMapState: " + myMapState.get(value.user));

            myAggregatingState.add(value);
            System.out.println("myAggregatingState: " + myAggregatingState.get());

            myReducingState.add(value);
            System.out.println("myReducingState: " + myReducingState.get());

        }
    }
}
```

#### ListState

```java
package study_flink.State;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

public class TwoStreamJoin {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        SingleOutputStreamOperator<Tuple3<String, String, Long>> stream1 = env
                .fromElements(
                        Tuple3.of("a", "stream-1", 1000L),
                        Tuple3.of("b", "stream-1", 2000L)
                )
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Tuple3<String, String, Long>>forMonotonousTimestamps()
                                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, String, Long>>() {
                                    @Override
                                    public long extractTimestamp(Tuple3<String,
                                            String, Long> t, long l) {
                                        return t.f2;
                                    }
                                })
                );
        SingleOutputStreamOperator<Tuple3<String, String, Long>> stream2 = env
                .fromElements(
                        Tuple3.of("a", "stream-2", 3000L),
                        Tuple3.of("b", "stream-2", 4000L)
                )
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Tuple3<String, String, Long>>forMonotonousTimestamps()
                                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, String, Long>>() {
                                    @Override
                                    public long extractTimestamp(Tuple3<String,
                                            String, Long> t, long l) {
                                        return t.f2;
                                    }
                                })
                );

        stream1.keyBy(s -> s.f0).connect(stream2.keyBy(d -> d.f0)).process(new CoProcessFunction<Tuple3<String, String, Long>, Tuple3<String, String, Long>, String>() {
            ListState<Tuple3<String, String, Long>> listState1;
            ListState<Tuple3<String, String, Long>> listState2;

            @Override
            public void open(Configuration parameters) throws Exception {
                listState1 = getRuntimeContext().getListState(new ListStateDescriptor<>("list1", Types.TUPLE(Types.STRING, Types.STRING, Types.LONG)));
                listState2 = getRuntimeContext().getListState(new ListStateDescriptor<>("list2", Types.TUPLE(Types.STRING, Types.STRING, Types.LONG)));
            }

            @Override
            public void processElement1(Tuple3<String, String, Long> value, CoProcessFunction<Tuple3<String, String, Long>, Tuple3<String, String, Long>, String>.Context ctx, Collector<String> out) throws Exception {
                //获取另一条的数据,配对输出
                for (Tuple3<String, String, Long> right : listState2.get()) {
                    out.collect(value + "=>" + right);
                }
                listState1.add(value);
            }

            @Override
            public void processElement2(Tuple3<String, String, Long> value, CoProcessFunction<Tuple3<String, String, Long>, Tuple3<String, String, Long>, String>.Context ctx, Collector<String> out) throws Exception {
                //获取另一条的数据,配对输出
                for (Tuple3<String, String, Long> left : listState1.get()) {
                    out.collect(left + "=>" + value);
                }
                listState2.add(value);
            }
        }).print();
        env.execute();


    }
}

```

#### MapSrate

```java
package study_flink.State;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.sql.Timestamp;
import java.time.Duration;

public class FakeWindow {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        SingleOutputStreamOperator<Event> stream = env.addSource(new MySource()).assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(
                new SerializableTimestampAssigner<Event>() {
                    @Override
                    public long extractTimestamp(Event element, long recordTimestamp) {
                        return element.timestamp;
                    }
                }
        ));
        stream.keyBy(data -> data.url).process(new KeyedProcessFunction<String, Event, String>() {


            MapState<Long, Long> windowUrlCountState;


            @Override
            public void open(Configuration parameters) throws Exception {
                windowUrlCountState = getRuntimeContext().getMapState(new MapStateDescriptor<>("window", Long.class, Long.class));
            }

            @Override
            public void processElement(Event value, KeyedProcessFunction<String, Event, String>.Context ctx, Collector<String> out) throws Exception {
                //每来一条数据，根据时间戳判断他属于哪个窗口
                Long windowStart = value.timestamp/10000L*10000L;
                Long windowEnd = windowStart + 10000L;
                //注册end减一的定时器
                ctx.timerService().registerEventTimeTimer(windowEnd - 1L);
                //更新状态，进行聚合
                if (windowUrlCountState.contains(windowStart)) {
                    Long count = windowUrlCountState.get(windowStart);
                    windowUrlCountState.put(windowStart, count + 1);
                } else {
                    windowUrlCountState.put(windowStart, 1L);
                }
            }

            @Override
            public void onTimer(long timestamp, KeyedProcessFunction<String, Event, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
                Long windowEnd = timestamp + 1;
                Long windowStart = windowEnd - 10000L;
                Long count = windowUrlCountState.get(windowStart);
                out.collect("窗口 :" + new Timestamp(windowStart) + "~" + new Timestamp(windowEnd) + "Url = " + ctx.getCurrentKey() + "count =" + count);

                windowUrlCountState.remove(windowStart);


            }
        }).print();
        env.execute();


    }
}

```

#### AggregatingState

```java
package study_flink.State;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.sql.Timestamp;
import java.time.Duration;

public class AverageTimestamp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        SingleOutputStreamOperator<Event> stream = env.addSource(new MySource()).assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(
                new SerializableTimestampAssigner<Event>() {
                    @Override
                    public long extractTimestamp(Event element, long recordTimestamp) {
                        return element.timestamp;
                    }
                }
        ));

        stream.keyBy(data -> data.user)
                .flatMap(new AvgTsResult())
                .print();
        env.execute();
    }

    public static class AvgTsResult extends RichFlatMapFunction<Event, String> {
        // 定义聚合状态，用来计算平均时间戳
        AggregatingState<Event, Long> avgTsAggState;
        // 定义一个值状态，用来保存当前用户访问频次
        ValueState<Long> countState;

        @Override
        public void open(Configuration parameters) throws Exception {
            avgTsAggState = getRuntimeContext().getAggregatingState(new
                    AggregatingStateDescriptor<Event, Tuple2<Long, Long>, Long>(
                    "avg-ts",
                    new AggregateFunction<Event, Tuple2<Long, Long>, Long>() {
                        @Override
                        public Tuple2<Long, Long> createAccumulator() {
                            return Tuple2.of(0L, 0L);
                        }

                        @Override
                        public Tuple2<Long, Long> add(Event value, Tuple2<Long, Long>
                                accumulator) {
                            return Tuple2.of(accumulator.f0 + value.timestamp,
                                    accumulator.f1 + 1);
                        }

                        @Override
                        public Long getResult(Tuple2<Long, Long> accumulator) {
                            return accumulator.f0 / accumulator.f1;
                        }

                        @Override
                        public Tuple2<Long, Long> merge(Tuple2<Long, Long> a,
                                                        Tuple2<Long, Long> b) {
                            return null;
                        }
                    },
                    Types.TUPLE(Types.LONG, Types.LONG)
            ));
            countState = getRuntimeContext().getState(new
                    ValueStateDescriptor<Long>("count", Long.class));
        }

        @Override
        public void flatMap(Event value, Collector<String> out) throws Exception {
            Long count = countState.value();
            if (count == null) {
                count = 1L;
            } else {
                count++;

            }
            countState.update(count);
            avgTsAggState.add(value);
            // 达到 5 次就输出结果，并清空状态
            if (count == 5) {
                out.collect(value.user + " 平均时间戳： " + new
                        Timestamp(avgTsAggState.get()));
                countState.clear();
            }
        }
    }
}


```

#### ValueState

```java
package study_flink.State;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.time.Duration;

public class myValueState {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        SingleOutputStreamOperator<Event> stream = env.addSource(new MySource()).assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long recordTimestamp) {
                return event.timestamp;
            }
        }));


        stream.keyBy(data -> data.user).process(new KeyedProcessFunction<String, Event, String>() {
            ValueState<Long> countCount;
            ValueState<Long> timerTsState;

            @Override
            public void open(Configuration parameters) throws Exception {
                countCount = getRuntimeContext().getState(new ValueStateDescriptor<Long>("count", Long.class));
                timerTsState = getRuntimeContext().getState(new ValueStateDescriptor<>("ts", Long.class));

            }

            //每来一条数据都会调用
            @Override
            public void processElement(Event value, KeyedProcessFunction<String, Event, String>.Context ctx, Collector<String> out) throws Exception {
                Long count = countCount.value();
                countCount.update(count == null ? 1 : count + 1);

                if (timerTsState.value() == null) {
                    ctx.timerService().registerEventTimeTimer(value.timestamp + 10 * 1000L);
                    timerTsState.update(value.timestamp + 10 * 1000L);
                }

            }

            @Override
            public void onTimer(long timestamp, KeyedProcessFunction<String, Event, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
                out.collect("定时器触发：" + ctx.getCurrentKey() + " " + "PV:" + countCount.value());
                timerTsState.clear();
            }
        }).print();


        env.execute();
    }
}

```

### TTL状态生存时间

在实际应用中，很多状态会随着时间的推移逐渐增长，如果不加以限制，最终就会导致存 储空间的耗尽。一个优化的思路是直接在代码中调用.clear()方法去清除状态，但是有时候我们 的逻辑要求不能直接清除。这时就需要配置一个状态的“生存时间”（time-to-live，TTL），当 状态在内存中存在的时间超出这个值时，就将它清除。

具体实现上，如果用一个进程不停地扫描所有状态看是否过期，显然会占用大量资源做无 用功。状态的失效其实不需要立即删除，所以我们可以给状态附加一个属性，也就是状态的“失 效时间”。状态创建的时候，设置 失效时间 = 当前时间 + TTL；之后如果有对状态的访问和 修改，我们可以再对失效时间进行更新；当设置的清除条件被触发时（比如，状态被访问的时 候，或者每隔一段时间扫描一次失效状态），就可以判断状态是否失效、从而进行清除了。 配置状态的 TTL 时，需要创建一个 StateTtlConfig 配置对象，然后调用状态描述器 的.enableTimeToLive()方法启动 TTL 功能。

```java
// 在open生命周期函数里面或者在获得状态控制句柄的时候进行配置
StateTtlConfig ttlConfig = StateTtlConfig
    .newBuilder(Time.seconds(10))
    .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
    .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
    .build();
ValueStateDescriptor<String> stateDescriptor = new ValueStateDescriptor<>("mystate", String.class);
stateDescriptor.enableTimeToLive(ttlConfig);
```

- .newBuilder() 状态 TTL 配置的构造器方法，必须调用，返回一个 Builder 之后再调用.build()方法就可以 得到 StateTtlConfig 了。方法需要传入一个 Time 作为参数，这就是设定的状态生存时间。 
- .setUpdateType() 设置更新类型。更新类型指定了什么时候更新状态失效时间，这里的 OnCreateAndWrite 表示只有创建状态和更改状态（写操作）时更新失效时间。另一种类型 OnReadAndWrite 则表 示无论读写操作都会更新失效时间，也就是只要对状态进行了访问，就表明它是活跃的，从而 延长生存时间。这个配置默认为 OnCreateAndWrite。
- .setStateVisibility() 设置状态的可见性。所谓的“状态可见性”，是指因为清除操作并不是实时的，所以当状 态过期之后还有可能基于存在，这时如果对它进行访问，能否正常读取到就是一个问题了。这 里设置的 NeverReturnExpired 是默认行为，表示从不返回过期值，也就是只要过期就认为它已 经被清除了，应用不能继续读取；这在处理会话或者隐私数据时比较重要。对应的另一种配置 是 ReturnExpireDefNotCleanedUp，就是如果过期状态还存在，就返回它的值。

## 算子状态（Operator State）

算子状态（Operator State）就是一个算子并行实例上定义的状态，作用范围被限定为当前 算子任务。算子状态跟数据的 key 无关，所以不同 key 的数据只要被分发到同一个并行子任务， 就会访问到同一个 Operator State。

算子状态也支持不同的结构类型，主要有三种：**ListState**、**UnionListState** 和**BroadcastState**。

1. **列表状态（ListState）** 与 Keyed State 中的 ListState 一样，将状态表示为一组数据的列表。当算子并行度进行缩放调整时，算子的列表状态中的所有元素项会被统一收集起来，相当 于把多个分区的列表合并成了一个“大列表”，然后再均匀地分配给所有并行任务。这种“均 匀分配”的具体方法就是“轮询”（round-robin），与之前介绍的 rebanlance 数据传输方式类似， 是通过逐一“发牌”的方式将状态项平均分配的。这种方式也叫作“平均分割重组”（even-split redistribution）。

2. **联合列表状态（UnionListState）** 与 ListState 类似，联合列表状态也会将状态表示为一个列表。它与常规列表状态的区别 在于，算子并行度进行缩放调整时对于状态的分配方式不同。UnionListState 的重点就在于“联合”（union）。在并行度调整时，常规列表状态是轮询分 配状态项，而联合列表状态的算子则会直接广播状态的完整列表。这样，并行度缩放之后的并 行子任务就获取到了联合后完整的“大列表”，可以自行选择要使用的状态项和要丢弃的状态 项。这种分配也叫作“联合重组”（union redistribution）。如果列表中状态项数量太多，为资源 和效率考虑一般不建议使用联合重组的方式

3. **广播状态（BroadcastState）** 有时我们希望算子并行子任务都保持同一份“全局”状态，用来做统一的配置和规则设定。 这时所有分区的所有数据都会访问到同一个状态，状态就像被“广播”到所有分区一样，这种 特殊的算子状态，就叫作广播状态（BroadcastState）。因为广播状态在每个并行子任务上的实例都一样，所以在并行度调整的时候就比较简单， 只要复制一份到新的并行任务就可以实现扩展；而对于并行度缩小的情况，可以将多余的并行 子任务连同状态直接砍掉——因为状态都是复制出来的，并不会丢失。

#### 列表状态（ListState）

```JAVA
package study_flink.Operatortate;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import study_flink.Source.Event;
import study_flink.Source.MySource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BufferingSink {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Event> streamSource = env.addSource(new MySource());

        SingleOutputStreamOperator<Event> stream = streamSource.assignTimestampsAndWatermarks(WatermarkStrategy.<Event>forBoundedOutOfOrderness(Duration.ZERO).withTimestampAssigner(new SerializableTimestampAssigner<Event>() {
            @Override
            public long extractTimestamp(Event event, long l) {
                return event.timestamp;
            }
        }));

        stream.addSink(new MyBufferingSink(10));


        env.execute();


    }

    public static class MyBufferingSink implements SinkFunction<Event>, CheckpointedFunction {
        //定义当前类的属性，批量阈值
        private final int threshold;
        private List<Event> list;

        //定义算子状态
        ListState<Event> checkpointState;

        public MyBufferingSink(int threshold) {
            this.threshold = threshold;
            this.list = new ArrayList<>();
        }


        @Override
        public void invoke(Event value, Context context) throws Exception {
            list.add(value);
            if (list.size() == threshold) {
                //打印到控制台来模拟
                for (Event event : list) {
                    System.out.println(event);
                }
                System.out.println("===========================");
                list.clear();
            }
        }

        //保存状态快照到检查点时，调用这个方法
        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            //先清空状态
            checkpointState.clear();


            //对状态进行持久化的状态，复制缓存的列表到列表状态
            for (Event event : list) {
                checkpointState.add(event);
            }

        }


        //初始化算子状态。也会在恢复状态时调用
        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            //定义算子状态
            ListStateDescriptor<Event> descriptor = new ListStateDescriptor<>("list", Event.class);
            checkpointState = context.getOperatorStateStore().getListState(descriptor);
            //如果从故障恢复，需要将ListState中的所有元素复制到列表中
            if (context.isRestored()) {
                for (Event event : checkpointState.get()) {
                    list.add(event);
                }
            }

        }

    }

}

```

#### 广播状态（Broadcast State）

```java
package study_flink.Operatortate;

import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

public class BC {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //用户的行为数据
        DataStreamSource<Action> actionStream = env.fromElements(new Action("zkw", "login"),
                new Action("zkw", "pay"),
                new Action("jly", "login"),
                new Action("jly", "order"));

        DataStreamSource<Pattern> patternStream = env.fromElements(new Pattern("login", "pay"),
                new Pattern("login", "order"));

        //创建描述器，构建广播流
        MapStateDescriptor<Void, Pattern> descriptor = new MapStateDescriptor<>("pattern", Types.VOID, Types.POJO(Pattern.class));
        BroadcastStream<Pattern> broadcast = patternStream.broadcast(descriptor);
        
        //连接广播流
        actionStream.keyBy(data ->data.id).connect(broadcast).process(new KeyedBroadcastProcessFunction<String, Action, Pattern, String>() {

            ValueState<String> valueState;

            @Override
            public void open(Configuration parameters) throws Exception {
                valueState = getRuntimeContext().getState(new ValueStateDescriptor<String>("zkw",String.class));

            }

            @Override
            public void processElement(Action value, KeyedBroadcastProcessFunction<String, Action, Pattern, String>.ReadOnlyContext ctx, Collector<String> out) throws Exception {
                ReadOnlyBroadcastState<Void, Pattern> zz = ctx.getBroadcastState(new MapStateDescriptor<>("pattern", Types.VOID, Types.POJO(Pattern.class)));

                Pattern pattern = zz.get(null);
//                上一次的行为
                String value1 = valueState.value();
                if (value1!=null&&pattern!=null){
                    if (pattern.action1.equals(value1)&&pattern.action2.equals(value.action)){
                        out.collect(value.id+pattern);
                    }
                }else {
                    valueState.update(value.action);
                }
            }

            @Override
            public void processBroadcastElement(Pattern value, KeyedBroadcastProcessFunction<String, Action, Pattern, String>.Context ctx, Collector<String> out) throws Exception {
                BroadcastState<Void, Pattern> zz = ctx.getBroadcastState(new MapStateDescriptor<Void, Pattern>("pattern", Types.VOID, Types.POJO(Pattern.class)));
                zz.put(null,value);

            }
        }).print();
        env.execute();

    }


    //定义用户行为和模式的POJO类
    public static class Action {
        public String id;
        public String action;

        public Action() {

        }

        public Action(String id, String action) {
            this.id = id;
            this.action = action;
        }

        @Override
        public String toString() {
            return "Action{" +
                    "id='" + id + '\'' +
                    ", action='" + action + '\'' +
                    '}';
        }
    }

    public static class Pattern {
        public String action1;
        public String action2;

        public Pattern(String action1, String action2) {
            this.action1 = action1;
            this.action2 = action2;
        }

        public Pattern() {

        }

        @Override
        public String toString() {
            return "Pattern{" +
                    "action1='" + action1 + '\'' +
                    ", action2='" + action2 + '\'' +
                    '}';
        }
    }


}
```

## 状态持久化和状态后端

在Flink的状态管理机制中，很重要的一个功能就是对状态进行持久化（ persistence）保存，这样就可以在发生故障后进行重启恢复。 Flink对状态进行持久化的方式，就是将当前所有分布式状态进行“快照”保存，写入一个“检查点”（ checkpoint）或者保存点 savepoint保存到外部存储系统中。具体的存储介质，一般是分布式文件系统（ distributed file system）。

### 状态后端（ State Backends)

![image-20221013125446535](D:\java\img\image-20221013125446535.png)



**哈希表状态后端** HashMapStateBackend这种方式就是我们之前所说的，把状态存放在内存里。具体实现上，哈希表状态后端在内部会直接把状态当作对象（ objects），保存在 Taskmanager的 JVM堆（ heap）上。普通的状态以及窗口中收集的数据和触发器（ triggers），都会以键值对 key value）的形式存储起来，所以底层是一个哈希表（ HashMap），这种状态后端也因此得名。

**内嵌 RocksDB状态后端**（ EmbeddedRocksDBState）BackendRocksDB是一种内嵌的 key value存储介质，可以把数据持久化到本地硬盘。配置EmbeddedRocksDBStateBackend后，会将处理中的数据全部放入 RocksDB数据库中， RocksDB默 认存储在 TaskManager的本地数据目录里。
