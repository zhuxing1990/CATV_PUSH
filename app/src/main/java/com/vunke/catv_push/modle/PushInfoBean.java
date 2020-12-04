package com.vunke.catv_push.modle;

/**
 * Created by zhuxi on 2020/8/5.
 */

public class PushInfoBean {
    private String pushId;
    private String pushName;
    private long createTimes;
    private long expireTime;
    private long showStartTime;
    private long showEndTime;
    private long showTimes;
    private String pushStatus;
    private String pushType;
    private String pushTitle;
    private String pushUrl;
    private String pushLink;
    private String pushMessage;
    private int width;
    private int height;
    private int marginLeft;
    private int marginTop;
    private long acc_time;
    private String taskId;
    private String topicType;
    private long duration;
    private String groupId;
    private long intervalTime;
    private int showRules;
    private long systemId;
    private long startTime;
    private long endTime;
    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getPushName() {
        return pushName;
    }

    public void setPushName(String pushName) {
        this.pushName = pushName;
    }

    public long getCreateTimes() {
        return createTimes;
    }

    public void setCreateTimes(long createTimes) {
        this.createTimes = createTimes;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getShowStartTime() {
        return showStartTime;
    }

    public void setShowStartTime(long showStartTime) {
        this.showStartTime = showStartTime;
    }

    public long getShowEndTime() {
        return showEndTime;
    }

    public void setShowEndTime(long showEndTime) {
        this.showEndTime = showEndTime;
    }

    public long getShowTimes() {
        return showTimes;
    }

    public void setShowTimes(long showTimes) {
        this.showTimes = showTimes;
    }

    public String getPushStatus() {
        return pushStatus;
    }

    public void setPushStatus(String pushStatus) {
        this.pushStatus = pushStatus;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getPushTitle() {
        return pushTitle;
    }

    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getPushLink() {
        return pushLink;
    }

    public void setPushLink(String pushLink) {
        this.pushLink = pushLink;
    }

    public String getPushMessage() {
        return pushMessage;
    }

    public void setPushMessage(String pushMessage) {
        this.pushMessage = pushMessage;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public long getAcc_time() {
        return acc_time;
    }

    public void setAcc_time(long acc_time) {
        this.acc_time = acc_time;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public int getShowRules() {
        return showRules;
    }

    public void setShowRules(int showRules) {
        this.showRules = showRules;
    }

    public long getSystemId() {
        return systemId;
    }

    public void setSystemId(long systemId) {
        this.systemId = systemId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "PushInfoBean{" +
                "pushId='" + pushId + '\'' +
                ", pushName='" + pushName + '\'' +
                ", createTimes=" + createTimes +
                ", expireTime=" + expireTime +
                ", showStartTime=" + showStartTime +
                ", showEndTime=" + showEndTime +
                ", showTimes=" + showTimes +
                ", pushStatus='" + pushStatus + '\'' +
                ", pushType='" + pushType + '\'' +
                ", pushTitle='" + pushTitle + '\'' +
                ", pushUrl='" + pushUrl + '\'' +
                ", pushLink='" + pushLink + '\'' +
                ", pushMessage='" + pushMessage + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", marginLeft=" + marginLeft +
                ", marginTop=" + marginTop +
                ", acc_time=" + acc_time +
                ", taskId='" + taskId + '\'' +
                ", topicType='" + topicType + '\'' +
                ", duration=" + duration +
                ", groupId='" + groupId + '\'' +
                ", intervalTime=" + intervalTime +
                ", showRules=" + showRules +
                ", systemId=" + systemId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
