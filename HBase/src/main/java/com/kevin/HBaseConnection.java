package com.kevin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;


public class HBaseConnection {
    //声明一个静态属性
    public static Connection connection = null;

    static {
        try {
            connection = ConnectionFactory.createConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void close() throws IOException {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * 重量级的连接,一个线程最好创建一个,是线程安全的,不建议缓存和池化
     */
    public static void main(String[] args) throws IOException {
        //直接使用静态连接不要再main线程里面单独创建
        System.out.println(HBaseConnection.connection);
        HBaseConnection.close();

    }
}
