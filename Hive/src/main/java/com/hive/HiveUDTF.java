package com.hive;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 输入 hello，world
 * 输出 hello
 * world
 */

public class HiveUDTF extends GenericUDTF {


    private final ArrayList<String> output = new ArrayList<>();

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) throws UDFArgumentException {
//        输出数据的默认列名，可以被覆盖
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("word");
//        输出数据类型
        List<ObjectInspector> fieldOIs = new ArrayList<>();
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
//        最终的返回值
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
    }

    @Override
    public void process(Object[] objects) throws HiveException {
        String input = objects[0].toString();
        String[] words = input.split(",");
        for (String word : words) {
            output.clear();
            output.add(word);
//            写出
            forward(output);
        }


    }

    @Override
    public void close() throws HiveException {

    }
}
