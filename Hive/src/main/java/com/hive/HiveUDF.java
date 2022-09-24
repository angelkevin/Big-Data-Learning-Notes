package com.hive;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

public class HiveUDF extends GenericUDF {
    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {
        if(objectInspectors.length!=1){
            throw new UDFArgumentException("参数个数不为1");
        }
        return PrimitiveObjectInspectorFactory.javaIntObjectInspector;
    }
//    初始化

    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {

        String input = deferredObjects[0].get().toString();

        if (input== null){
            return 0;
        }

        return input.length();
    }
//    计算

    @Override
    public String getDisplayString(String[] strings) {
        return "";
    }
//    看sql的执行计划
}
