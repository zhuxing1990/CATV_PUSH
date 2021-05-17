package com.vunke.catv_push.modle;

public class ChannelBean {

    /**
     * begin_time : string
     * channelId : string
     * channelName : string
     * channelNumber : string
     * end_time : string
     * programId : string
     * programName : string
     * status : 1
     * type : 1
     */

    private String begin_time;
    private String channelId;
    private String channelName;
    private String channelNumber;
    private String end_time;
    private String programId;
    private String programName;
    private int status;
    private int type;
    private String cardId;
    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(String channelNumber) {
        this.channelNumber = channelNumber;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    @Override
    public String toString() {
        return "ChannelBean{" +
                "begin_time='" + begin_time + '\'' +
                ", channelId='" + channelId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", channelNumber='" + channelNumber + '\'' +
                ", end_time='" + end_time + '\'' +
                ", programId='" + programId + '\'' +
                ", programName='" + programName + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", cardId='" + cardId + '\'' +
                '}';
    }
}
