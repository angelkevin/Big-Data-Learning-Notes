package com.kevin;

import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class HBaseDDL {

    public static Connection connection = HBaseConnection.connection;

    /**
     * 创建命名空间
     *
     * @param namespace 命名空间名称
     */
    public static void createNameSpace(String namespace) throws IOException {
        //1.获取admin
        //admin的连接是轻量级的,不是线程安全的,不建议池化或者缓存
        Admin admin = connection.getAdmin();
        //2.创建命名空间
        //   代码相对代码更加底层,需要填写一个完整的描述器
        //创建命名空间的建造者
        NamespaceDescriptor.Builder builder = NamespaceDescriptor.create(namespace);
        //给命名空间添加键值对
        builder.addConfiguration("user", "root");
        //使用build构造出对应的对象 完成创建
        try {
            admin.createNamespace(builder.build());
        } catch (IOException e) {
            System.out.println("命名空间已经存在");
            e.printStackTrace();
        }
        //关闭
        admin.close();
    }

    /**
     * 判断表格是否存在
     *
     * @param namespace 命名空间
     * @param tableName 表名
     * @return true表示存在
     */
    public static Boolean isTableExists(String namespace, String tableName) throws IOException {
        Admin admin = connection.getAdmin();

        //使用方法判断表格是否存在
        boolean exists = false;
        try {
            exists = admin.tableExists(TableName.valueOf(namespace, tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exists;

    }

    /**
     * 创建表格
     *
     * @param namespace      命名空间
     * @param tableName      表名
     * @param columnFamilies 列族
     */
    public static void createTable(String namespace, String tableName, String... columnFamilies) throws IOException {
        //判断是否有至少一个列族
        if(columnFamilies.length==0){
            System.out.println("创建表格至少有一个列族");
            return;
        }
        Admin admin = connection.getAdmin();
        //创建表描述器
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(namespace, tableName));
        //添加列族参数
        for (String columnFamily : columnFamilies) {
            //创建列族描述
            ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnFamily));
            //添加版本参数
            columnFamilyDescriptorBuilder.setMaxVersions(3);
            tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptorBuilder.build());

        }
        try {
            admin.createTable(tableDescriptorBuilder.build());
        } catch (IOException e) {
            System.out.println("表格存在");
            e.printStackTrace();
        }
        admin.close();


    }

    /**
     * 修改表格中一个列族的版本
     * @param namespace 命名空间
     * @param tableName 表名
     * @param columFamily 列族
     * @param version 版本
     */
    public static void modifyTable(String namespace,String tableName,String columFamily,int version) throws IOException {

        if (!isTableExists(namespace,tableName)) {
            System.out.println("表格不存在");
            return;
        }
        Admin admin = connection.getAdmin();
        //获取表格描述
        TableDescriptor tableDescriptor = admin.getDescriptor(TableName.valueOf(namespace, tableName));
        //创建表格描述
        //  表格描述建造者
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(tableDescriptor);
        //修改
        //创建一个列族描述
        ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columFamily));
        columnFamilyDescriptorBuilder.setMaxVersions(version);
        //创建列族描述
        tableDescriptorBuilder.modifyColumnFamily(columnFamilyDescriptorBuilder.build());
        //修改表格
        admin.modifyTable(tableDescriptorBuilder.build());
        //关闭连接
        admin.close();

    }

    /**
     * 删除表
     * @param namespace 命名空间
     * @param tableName 表名
     * @return true 删除成功
     */
    public static Boolean deleteTable(String namespace,String tableName) throws IOException {
        if (!isTableExists(namespace,tableName)) {
            System.out.println("表格不存在");
            return false;
        }
        Admin admin = connection.getAdmin();
        TableName tableName1 = TableName.valueOf(namespace, tableName);
        admin.disableTable(tableName1);
        admin.deleteTable(tableName1);
        admin.close();
        return true;
    }


    public static void main(String[] args) throws IOException {
        //测试创建命名空间
//        createNameSpace("zkw");
//        System.out.println(">>>>>>>>>");
//        createTable("zkw", "test", "c1");
//        System.out.println(">>>>>>>>>");
//        isTableExists("zkw","test");
//        System.out.println(">>>>>>>>>");
//        deleteTable("zkw", "test");
//        HBaseConnection.connection.close();
    }
}
