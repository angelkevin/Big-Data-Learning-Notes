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

