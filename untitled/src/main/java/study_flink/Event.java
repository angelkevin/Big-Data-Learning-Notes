package study_flink;

import java.sql.Timestamp;

//类是公有的
//有一个无参的构造方法
//所有的属性都是可以序列化的和公有的



public class Event {
    public String user;
    public String url;
    public long timestamp;

    public Event(String user, String url, long timestamp) {
        this.user = user;
        this.url = url;
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return "Event{" +
                "user='" + user + '\'' +
                ", url='" + url + '\'' +
                ", timestamp=" + new Timestamp(timestamp) +
                '}';
    }


}
