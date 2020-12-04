package com.vunke.catv_push.modle;

import java.util.List;

/**
 * Created by zhuxi on 2020/8/4.
 */

public class PushDataBean {

    /**
     * success : true
     * msg : 操作成功
     * obj : {"subscribeList":[{"msId":1,"topicName":"test","qos":1,"topicType":1,"topicNote":null},{"msId":2,"topicName":"aa","qos":1,"topicType":1,"topicNote":null}],"broker":{"mbId":null,"host":"tcp://192.168.0.150:1883","userName":"test","password":"test","heartbeat":20,"timeout":10}}
     * attributes : null
     */

    private boolean success;
    private String msg;
    /**
     * subscribeList : [{"msId":1,"topicName":"test","qos":1,"topicType":1,"topicNote":null},{"msId":2,"topicName":"aa","qos":1,"topicType":1,"topicNote":null}]
     * broker : {"mbId":null,"host":"tcp://192.168.0.150:1883","userName":"test","password":"test","heartbeat":20,"timeout":10}
     */

    private ObjBean obj;
    private Object attributes;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ObjBean getObj() {
        return obj;
    }

    public void setObj(ObjBean obj) {
        this.obj = obj;
    }

    public Object getAttributes() {
        return attributes;
    }

    public void setAttributes(Object attributes) {
        this.attributes = attributes;
    }

    public static class ObjBean {
        /**
         * mbId : null
         * host : tcp://192.168.0.150:1883
         * userName : test
         * password : test
         * heartbeat : 20
         * timeout : 10
         */

        private BrokerBean broker;
        /**
         * msId : 1
         * topicName : test
         * qos : 1
         * topicType : 1
         * topicNote : null
         */

        private List<SubscribeListBean> subscribeList;

        public BrokerBean getBroker() {
            return broker;
        }

        public void setBroker(BrokerBean broker) {
            this.broker = broker;
        }

        public List<SubscribeListBean> getSubscribeList() {
            return subscribeList;
        }

        public void setSubscribeList(List<SubscribeListBean> subscribeList) {
            this.subscribeList = subscribeList;
        }

        @Override
        public String toString() {
            return "ObjBean{" +
                    "broker=" + broker +
                    ", subscribeList=" + subscribeList +
                    '}';
        }

        public static class BrokerBean {
            private Object mbId;
            private String host;
            private String userName;
            private String password;
            private int heartbeat;
            private int timeout;

            public Object getMbId() {
                return mbId;
            }

            public void setMbId(Object mbId) {
                this.mbId = mbId;
            }

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public int getHeartbeat() {
                return heartbeat;
            }

            public void setHeartbeat(int heartbeat) {
                this.heartbeat = heartbeat;
            }

            public int getTimeout() {
                return timeout;
            }

            public void setTimeout(int timeout) {
                this.timeout = timeout;
            }

            @Override
            public String toString() {
                return "BrokerBean{" +
                        "mbId=" + mbId +
                        ", host='" + host + '\'' +
                        ", userName='" + userName + '\'' +
                        ", password='" + password + '\'' +
                        ", heartbeat=" + heartbeat +
                        ", timeout=" + timeout +
                        '}';
            }
        }

        public static class SubscribeListBean {
            private int msId;
            private String topicName;
            private int qos;
            private int topicType;
            private Object topicNote;

            public int getMsId() {
                return msId;
            }

            public void setMsId(int msId) {
                this.msId = msId;
            }

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

            public int getTopicType() {
                return topicType;
            }

            public void setTopicType(int topicType) {
                this.topicType = topicType;
            }

            public Object getTopicNote() {
                return topicNote;
            }

            public void setTopicNote(Object topicNote) {
                this.topicNote = topicNote;
            }

            @Override
            public String toString() {
                return "SubscribeListBean{" +
                        "msId=" + msId +
                        ", topicName='" + topicName + '\'' +
                        ", qos=" + qos +
                        ", topicType=" + topicType +
                        ", topicNote=" + topicNote +
                        '}';
            }
        }
    }
}
