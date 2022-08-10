package study_flink.FLink;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class BatchWordCount {
    public static void main(String[] args) throws Exception {
        // 1.创建环境
        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();
        // 2.从文件里读取数据
        DataSource<String> dataSource = environment.readTextFile("input//1.txt");
        // 3.将每行数据进行分词，然后转换成二元组类型
        FlatMapOperator<String, Tuple2<String, Long>> result = dataSource.flatMap((String line, Collector<Tuple2<String, Long>> out) -> {
            String[] words = line.split(",");
            for (String word : words) {
                out.collect(Tuple2.of(word, 1L));
            }
        }).returns(Types.TUPLE(Types.STRING, Types.LONG));
        // 安装word进行分组
        UnsortedGrouping<Tuple2<String, Long>> tuple2UnsortedGrouping = result.groupBy(0);
        // 分组后进行聚合
        AggregateOperator<Tuple2<String, Long>> re = tuple2UnsortedGrouping.sum(1);
        re.print();
    }
}
