package study_flink;

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
