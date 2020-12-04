package com.vunke.catv_push.modle;

/**
 * Created by zhuxi on 2020/8/8.
 */

public class PushLog {
    private long timestamp;
    private long acc_time;
    private long show_time;
    private long direct_time;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getAcc_time() {
        return acc_time;
    }

    public void setAcc_time(long acc_time) {
        this.acc_time = acc_time;
    }

    public long getShow_time() {
        return show_time;
    }

    public void setShow_time(long show_time) {
        this.show_time = show_time;
    }

    public long getDirect_time() {
        return direct_time;
    }

    public void setDirect_time(long direct_time) {
        this.direct_time = direct_time;
    }

    @Override
    public String toString() {
        return "PushLog{" +
                "timestamp=" + timestamp +
                ", acc_time=" + acc_time +
                ", show_time=" + show_time +
                ", direct_time=" + direct_time +
                '}';
    }
}
