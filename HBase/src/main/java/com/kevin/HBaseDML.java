package com.kevin;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.ColumnValueFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class HBaseDML {
    public static Connection connection = HBaseConnection.connection;

    /**
     * 插入数据
     *
     * @param namespace    命名空间
     * @param tableName    表名
     * @param rowKey       主键
     * @param columnFamily 列族
     * @param column       列名
     * @param value        值
     */
    public static void putCell(String namespace, String tableName, String rowKey, String columnFamily, String column, String value) throws IOException {
        //获取table
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));
        //调用相关的方法插入数据
        //创建put对象
        Put put = new Put(Bytes.toBytes(rowKey));
        //添加数据
        put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value));
        try {
            table.put(put);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //关闭table
        table.close();
    }

    /**
     * 读取数据
     *
     * @param namespace    命名空间
     * @param tableName    表名
     * @param rowKey       主键
     * @param columnFamily 列族
     * @param column       列名
     */
    public static void getCells(String namespace, String tableName, String rowKey, String columnFamily, String column) throws IOException {
        //获取table对象
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));
        //创建get方法
        Get get = new Get(Bytes.toBytes(rowKey));
        //添加对应的参数获取信息
        get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
        //获取版本
        get.readAllVersions();
        //读某一列的数据
        Result result = table.get(get);
        //处理数据
        Cell[] cells = result.rawCells();
        //直接打印测试数据
        for (Cell cell : cells) {
            String value = new String(CellUtil.cloneValue(cell));
            System.out.println(value);
        }
        table.close();

    }

    /**
     * 扫描数据
     *
     * @param namespace 命名空间
     * @param tableName 表名
     * @param startRow  开始的row 包含
     * @param endRow    结束的row 不包含
     */
    public static void scanRows(String namespace, String tableName, String startRow, String endRow) throws IOException {
        //获取table
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));
        //创建scan对象
        Scan scan = new Scan();
        //添加范围
        scan.withStartRow(Bytes.toBytes(startRow));
        scan.withStopRow(Bytes.toBytes(endRow));
        //读取多条数据获取scanner
        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert scanner != null;
        for (Result result : scanner) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                String value = new String(CellUtil.cloneValue(cell));
                System.out.print(value);
            }
        }
        table.close();
    }

    /**
     * 带过滤的扫描
     *
     * @param namespace    命名空间
     * @param tableName    表名
     * @param startRow     开始row
     * @param endRow       结束row
     * @param columnFamily 列族名称
     * @param column       列名
     * @param value        值
     */
    public static void filterScan(String namespace, String tableName, String startRow, String endRow, String columnFamily, String column, String value) throws IOException {
        //获取table
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));
        //创建scan对象
        Scan scan = new Scan();
        //添加范围
        scan.withStartRow(Bytes.toBytes(startRow));
        scan.withStopRow(Bytes.toBytes(endRow));
        //(1)单列数据过滤扫描 添加过滤条件
        FilterList filterList = new FilterList();
        //过滤条件
        ColumnValueFilter columnValueFilter = new ColumnValueFilter(
                Bytes.toBytes(columnFamily),
                Bytes.toBytes(column),
                CompareOperator.EQUAL,
                Bytes.toBytes(value)
        );
        //过滤列表添加过滤条件
        filterList.addFilter(filterList);

        //(2)整行数据过滤保留没有当前列的数据
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(
                Bytes.toBytes(columnFamily),
                Bytes.toBytes(column),
                CompareOperator.EQUAL,
                Bytes.toBytes(value)

        );
        filterList.addFilter(singleColumnValueFilter);


        //scan添加过滤列表
        scan.setFilter(filterList);
        //读取多条数据获取scanner
        ResultScanner scanner = null;
        try {
            scanner = table.getScanner(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert scanner != null;
        for (Result result : scanner) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                String val = new String(CellUtil.cloneValue(cell));
                System.out.print(val);
            }
        }
        table.close();
    }

    /**
     * 删除数据
     *
     * @param namespace    命名空间
     * @param tableName    表名
     * @param rowKey       主键
     * @param columnFamily 列族名称
     * @param column       列名
     */
    public static void deleteColumn(String namespace, String tableName, String rowKey, String columnFamily, String column) throws IOException {

        Table table = connection.getTable(TableName.valueOf(namespace, tableName));

        Delete delete = new Delete(Bytes.toBytes(rowKey));
        //删除一个版本 delete.addColumn()
        //删除所有版本 delete.addColumns()
        delete.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
        try {
            table.delete(delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        table.close();
    }

}
