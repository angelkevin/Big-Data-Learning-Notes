package study_flink;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Calendar;
import java.util.Random;

public class MySource implements SourceFunction<Event> {
    public Boolean running = true;

    @Override
    public void run(SourceContext<Event> sourceContext) throws Exception {
        // 随机生成器
        Random random = new Random();
        // 定义字段选取的数据集
        String[] users = {"zkw", "jly", "phb", "gzn"};
        String[] urls = {"91", "pornhub", "taobao", "wzry"};
        while (running) {
            String user = users[random.nextInt(users.length)];
            String url = urls[random.nextInt(urls.length)];
            long timestamp = Calendar.getInstance().getTimeInMillis();
            sourceContext.collect(new Event(user, url, timestamp));
            Thread.sleep(5000);
        }
    }
    @Override
    public void cancel() {
        running = false;
    }
}
