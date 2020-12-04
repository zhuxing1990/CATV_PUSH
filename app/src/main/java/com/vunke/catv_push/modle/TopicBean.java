package com.vunke.catv_push.modle;

/**
 * Created by zhuxi on 2020/8/13.
 */

public class TopicBean {
    private String topicName;
    private int qos;
    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    @Override
    public String toString() {
        return "TopicBean{" +
                "topicName='" + topicName + '\'' +
                ", qos=" + qos +
                '}';
    }
}
