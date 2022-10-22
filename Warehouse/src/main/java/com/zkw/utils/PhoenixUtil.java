package com.zkw.utils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;
import com.zkw.common.GmallConfig;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

public class PhoenixUtil {
    public static void upsertValue(DruidPooledConnection connection, String sinkTable, JSONObject data) throws SQLException {
//        拼接Sql
        Set<String> columns = data.keySet();
        Collection<Object> values = data.values();
        String sql = "upsert into " + GmallConfig.HBASE_SCHEMA + "." + sinkTable + "(" + StringUtils.join(columns, ",") + ") values ('" + StringUtils.join(values, "','") + "')";
        System.out.println(sql);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.execute();
        connection.commit();

        preparedStatement.close();


    }
}
