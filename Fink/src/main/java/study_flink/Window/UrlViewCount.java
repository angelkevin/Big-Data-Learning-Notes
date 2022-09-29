package study_flink.Window;

import java.sql.Timestamp;

public class UrlViewCount {
    public String url;
    public Long count;
    public Long start;
    public Long end;


    public UrlViewCount() {
    }

    public UrlViewCount(String url,  Long count,Long start, Long end) {
        this.url = url;
        this.start = start;
        this.end = end;
        this.count = count;
    }


    @Override
    public String toString() {
        return "UrlViewCount{" +
                "url='" + url + '\'' +
                ", count=" + count +
                ", start=" + new Timestamp(start) +
                ", end=" + new Timestamp(end) +
                '}';
    }
}
