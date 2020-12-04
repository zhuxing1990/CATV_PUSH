package com.vunke.catv_push.modle;

/**
 * Created by zhuxi on 2020/7/29.
 */

public class MqttBean {
    private String userName;
    private String passWord;;
    private String Host;
    private String clientId;
    private int KeepAliveInterval;
    private int ConnectionTimeout;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    public int getKeepAliveInterval() {
        return KeepAliveInterval;
    }

    public void setKeepAliveInterval(int keepAliveInterval) {
        KeepAliveInterval = keepAliveInterval;
    }

    public int getConnectionTimeout() {
        return ConnectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        ConnectionTimeout = connectionTimeout;
    }

    @Override
    public String toString() {
        return "MqttBean{" +
                "userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                ", Host='" + Host + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}
