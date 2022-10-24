package study_flink.CEP;

public class LoginEvent {
    public String user;
    public String ip;
    public String eventType;
    public Long timestamps;

    public LoginEvent() {
    }

    public LoginEvent(String user, String ip, String eventType, Long timestamps) {
        this.user = user;
        this.ip = ip;
        this.eventType = eventType;
        this.timestamps = timestamps;
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
                "user='" + user + '\'' +
                ", ip='" + ip + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamps=" + timestamps +
                '}';
    }
}
