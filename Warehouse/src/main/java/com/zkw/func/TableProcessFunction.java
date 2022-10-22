package com.zkw.func;

import com.alibaba.fastjson.JSONObject;
import com.zkw.bean.TableProcess;
import com.zkw.common.GmallConfig;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class TableProcessFunction extends BroadcastProcessFunction<JSONObject, String, JSONObject> {
    private Connection connection;
    private MapStateDescriptor<String, TableProcess> mapStateDescriptor;
    @Override
    public void processElement(JSONObject value, BroadcastProcessFunction<JSONObject, String, JSONObject>.ReadOnlyContext ctx, Collector<JSONObject> out) throws Exception {
        //获取广播的配置数据
        ReadOnlyBroadcastState<String, TableProcess> broadcastState = ctx.getBroadcastState(mapStateDescriptor);
        String table = value.getString("table");
        TableProcess tableProcess = broadcastState.get(table);
        if (tableProcess != null) {
            filterColumn(value.getJSONObject("data"), tableProcess.getSinkColumns());
            //补充SinkTable
            value.put("sinkTable", tableProcess.getSinkTable());
            out.collect(value);
        } else {
            System.out.println("找不到对应的key:" + table);
        }

    }

    public TableProcessFunction(MapStateDescriptor<String, TableProcess> mapStateDescriptor) {
        this.mapStateDescriptor = mapStateDescriptor;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        Class.forName(GmallConfig.PHOENIX_DRIVER);
        connection = DriverManager.getConnection(GmallConfig.PHOENIX_SERVER);
    }



    /**
     * 过滤字段
     *
     * @param data
     * @param sinkColumns
     */
    private void filterColumn(JSONObject data, String sinkColumns) {
        String[] colums = sinkColumns.split(",");
        List<String> columList = Arrays.asList(colums);
        Set<Map.Entry<String, Object>> entries = data.entrySet();
//        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Object> next = iterator.next();
//            if (!columList.contains(next.getKey())) {
//                iterator.remove();
//            }
//        }
        entries.removeIf(next -> !columList.contains(next.getKey()));


    }

    @Override
    public void processBroadcastElement(String value, BroadcastProcessFunction<JSONObject, String, JSONObject>.Context ctx, Collector<JSONObject> out) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(value);
        TableProcess tableprocess = jsonObject.getObject("after", TableProcess.class);

        chackTable(tableprocess.getSinkTable(), tableprocess.getSinkColumns(), tableprocess.getSinkPk(), tableprocess.getSinkExtend());
        BroadcastState<String, TableProcess> broadcastState = ctx.getBroadcastState(mapStateDescriptor);
        broadcastState.put(tableprocess.getSourceTable(), tableprocess);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    /**
     * 校验并且建表
     *
     * @param sinkTable
     * @param sinkColumns
     * @param sinkPk
     * @param sinkExtend
     */
    private void chackTable(String sinkTable, String sinkColumns, String sinkPk, String sinkExtend) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            if (sinkPk == null || "".equals(sinkPk)) {
                sinkPk = "id";
            }
            if (sinkExtend == null) {
                sinkExtend = "";
            }
            //拼接sql
            StringBuilder createtable = new StringBuilder("create table if not exists ").append(GmallConfig.HBASE_SCHEMA).append(".").append(sinkTable).append("(");
            String[] colums = sinkColumns.split(",");
            for (int i = 0; i < colums.length; i++) {
                String colum = colums[i];
                if (sinkPk.equals(colum)) {
                    createtable.append(colum).append(" varchar primary key");
                } else {
                    createtable.append(colum).append(" varchar");
                }
                if (i < colums.length - 1) {
                    createtable.append(",");
                }
            }

            createtable.append(")").append(sinkExtend);

            //编译SQL
            System.out.println(createtable.toString());
            preparedStatement = connection.prepareStatement(createtable.toString());
            //执行sql,建表
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException("建表失败" + sinkTable);
        } finally {
            //释放资源
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }
}
