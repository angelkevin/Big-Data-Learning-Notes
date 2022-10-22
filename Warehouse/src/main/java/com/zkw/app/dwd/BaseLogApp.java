package com.zkw.app.dwd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zkw.utils.DateFormatUtil;
import com.zkw.utils.MyKafkaUtil;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class BaseLogApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);//生产环境中为kafka的主题分区数

        String topic = "topic_log";
        String groupId = "dwd_app_log";

        DataStreamSource<String> kafkaDS = env.addSource(MyKafkaUtil.getFlinkKafkaConsumer(topic, groupId));
        //测输出流的标签
        OutputTag<String> dirty = new OutputTag<String>("Dirty") {
        };
        //过滤脏数据
        SingleOutputStreamOperator<JSONObject> jsonObjDS = kafkaDS.process(new ProcessFunction<String, JSONObject>() {
            @Override
            public void processElement(String value, ProcessFunction<String, JSONObject>.Context ctx, Collector<JSONObject> out) throws Exception {
                try {
                    JSONObject jsonObject = JSON.parseObject(value);
                    out.collect(jsonObject);
                    //测输出
                } catch (Exception e) {
                    ctx.output(dirty, value);
                }


            }
        });
        DataStream<String> dirtydata = jsonObjDS.getSideOutput(dirty);
//        dirtydata.print(">>>>>>");

        //用mid过滤
        KeyedStream<JSONObject, String> keyedStream = jsonObjDS.keyBy(json -> json.getJSONObject("common").getString("mid"));

        //使用状态编程做新老客户的标记

        SingleOutputStreamOperator<JSONObject> jsonObjWithNewFlag = keyedStream.map(new RichMapFunction<JSONObject, JSONObject>() {

            private ValueState<String> lastVisitState;

            @Override
            public void open(Configuration parameters) throws Exception {
                lastVisitState = getRuntimeContext().getState(new ValueStateDescriptor<String>("last-visit", String.class));
            }

            @Override
            public JSONObject map(JSONObject value) throws Exception {
                String is_new = value.getJSONObject("common").getString("is_new");
                Long ts = value.getLong("ts");
                //将时间戳转化成年月日
                String curDate = DateFormatUtil.toDate(ts);
                //获取状态中的日期
                String last = lastVisitState.value();
                if ("1".equals(is_new)) {
                    if (last == null) {
                        lastVisitState.update(curDate);
                    } else if (!last.equals(curDate)) {
                        value.getJSONObject("common").put("is_new", "0");
                    }
                } else if (last == null) {
                    lastVisitState.update(DateFormatUtil.toDate(ts - 24 * 60 * 60 * 1000L));
                }
                return value;
            }
        });
        //使用测输出流进行分流
        OutputTag<String> start = new OutputTag<String>("start") {
        };
        OutputTag<String> display = new OutputTag<String>("display") {
        };
        OutputTag<String> action = new OutputTag<String>("action") {
        };
        OutputTag<String> error = new OutputTag<String>("error") {
        };
        SingleOutputStreamOperator<String> pageDS = jsonObjWithNewFlag.process(new ProcessFunction<JSONObject, String>() {
            @Override
            public void processElement(JSONObject value, ProcessFunction<JSONObject, String>.Context ctx, Collector<String> out) throws Exception {

                //尝试获取错误信息
                String err = value.getString("err");
                if (err != null) {
                    ctx.output(error, value.toJSONString());
                }
                value.remove("err");
                String sta = value.getString("start");
                if (sta != null) {
                    ctx.output(start, value.toJSONString());
                } else {
                    String common = value.getString("common");
                    String pageId = value.getJSONObject("page").getString("page_id");
                    Long ts = value.getLong("ts");
                    JSONArray displays = value.getJSONArray("displays");
                    if (displays != null && displays.size() > 0) {
                        for (int i = 0; i < displays.size(); i++) {
                            JSONObject jsonObject = displays.getJSONObject(i);
                            jsonObject.put("common", common);
                            jsonObject.put("pageId", pageId);
                            jsonObject.put("ts", ts);
                            ctx.output(display, jsonObject.toJSONString());
                        }
                    }
                    JSONArray actions = value.getJSONArray("actions");
                    if (actions != null && actions.size() > 0) {
                        for (int i = 0; i < actions.size(); i++) {
                            JSONObject actionsJSONObject = actions.getJSONObject(i);
                            actionsJSONObject.put("common", common);
                            actionsJSONObject.put("pageId", pageId);
                            ctx.output(action, actionsJSONObject.toJSONString());
                        }
                    }
                    value.remove("displays");
                    value.remove("actions");
                    out.collect(value.toJSONString());
                }

            }
        });
        DataStream<String> startDS = pageDS.getSideOutput(start);
        DataStream<String> displayDS = pageDS.getSideOutput(display);
        DataStream<String> actionDS = pageDS.getSideOutput(action);
        DataStream<String> errorDS = pageDS.getSideOutput(error);

        pageDS.print("page>>>");
        startDS.print("start>>>");
        displayDS.print("display>>>");
        actionDS.print("action>>>");
        errorDS.print("error>>>");

        String page_topic = "dwd_traffic_page_log";
        String start_topic = "dwd_traffic_start_log";
        String display_topic = "dwd_traffic_display_log";
        String action_topic = "dwd_traffic_action_log";
        String error_topic = "dwd_traffic_error_log";


        pageDS.addSink(MyKafkaUtil.getFlinkKafkaProducer(page_topic));
        startDS.addSink(MyKafkaUtil.getFlinkKafkaProducer(start_topic));
        displayDS.addSink(MyKafkaUtil.getFlinkKafkaProducer(display_topic));
        actionDS.addSink(MyKafkaUtil.getFlinkKafkaProducer(action_topic));
        errorDS.addSink(MyKafkaUtil.getFlinkKafkaProducer(error_topic));


        env.execute("BaseLogApp");


    }
}
