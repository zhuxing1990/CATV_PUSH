package com.vunke.catv_push.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.vunke.catv_push.base.BaseConfig;
import com.vunke.catv_push.manage.DevicesManager;
import com.vunke.catv_push.manage.PushManager;
import com.vunke.catv_push.manage.TopicManager;
import com.vunke.catv_push.modle.MqttBean;
import com.vunke.catv_push.modle.PushDataBean;
import com.vunke.catv_push.modle.PushInfoBean;
import com.vunke.catv_push.modle.PushLog;
import com.vunke.catv_push.modle.TopicBean;
import com.vunke.catv_push.mqtt.PushUtil;
import com.vunke.catv_push.util.PushInfoUtil;
import com.vunke.catv_push.util.TimeUtil;
import com.vunke.catv_push.view.WeatherDialog;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by zhuxi on 2020/7/29.
 */

public class PushServer extends Service implements MqttCallback,IMqttActionListener {
    private static final String TAG = "PushServer";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        initDeviceInfo();
//        initMqtt();
        initPush();
    }

    private void initPush() {
        Observable.interval(0,1, TimeUnit.HOURS)
//        Observable.interval(0,30,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        initRequest();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dispose();
                        initPush();
                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x1190:
                    initRequest();
                    break;
            }
        }
    };
    private void initRequest() {
        Log.i(TAG, "initRequest: ");
        if (System.currentTimeMillis() - requestTime >10000L){
            requestTime = System.currentTimeMillis();
            initMqtt();
        }else{
            Log.i(TAG, "initRequest: 10秒内重复请求");
            handler.sendEmptyMessageDelayed(0x1190,10000L);
        }
    }

//    private DeviceInfoBean deviceInfoBean;
//    private void initDeviceInfo() {
//        deviceInfoBean = new DeviceInfoBean();
//        DevicesManager.queryDevicesInfo(this, deviceInfoBean);
//        Log.i(TAG, "initDeviceInfo: deviiceInfoBean:" + deviceInfoBean.toString());
//    }
    private boolean isConnected = false;
    private PushUtil pushUtil;
    private PushDataBean pushDataBean;
    private long requestTime = 0;
    private void initMqtt() {
        try {
            PushManager.initPush(this, new PushManager.PushCallBack() {
                @Override
                public void onSuccess(PushDataBean bean) {
                    if (null!=bean){
                        pushDataBean = bean;
                        boolean success = pushDataBean.isSuccess();
                        if (success){
                            PushDataBean.ObjBean.BrokerBean broker = pushDataBean.getObj().getBroker();
                            if (null!=broker){
                                MqttBean mqttBean = new MqttBean();
                                mqttBean.setHost(broker.getHost());
                                mqttBean.setClientId(DevicesManager.getSystemData(BaseConfig.CA_CARD));
                                mqttBean.setUserName(broker.getUserName());
                                mqttBean.setPassWord(broker.getPassword());
                                mqttBean.setConnectionTimeout(broker.getTimeout());
                                mqttBean.setKeepAliveInterval(broker.getHeartbeat());
                                initConnect(mqttBean);
                            }else{
                                Log.i(TAG, "onSuccess: 获取推送服务器信息失败，无法访问推送服务器");
                            }
                        }else{
                            Log.i(TAG, "onSuccess: 获取推送信息失败,服务器拒绝访问");
                            handler.sendEmptyMessageDelayed(0x1190,10000L);
                        }
                    }

                }

                @Override
                public void onFailed(Response<String> response) {
                    Log.i(TAG, "onFailed: 获取推送数据为空");
                    handler.sendEmptyMessageDelayed(0x1190,10000L);
                }

                @Override
                public void onError(Response<String> response, Throwable exception) {
                    Log.i(TAG, "onError: "+response.message(),exception);
                    Log.i(TAG, "onError: 获取推送信息失败,访问异常");
                    handler.sendEmptyMessageDelayed(0x1190,10000L);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void initConnect(MqttBean mqttBean) {
        pushUtil = PushUtil.getInstance(getApplicationContext());
        if (null==pushUtil.getClient()){
            pushUtil.initMqtt(mqttBean,this,this);
        }else{
            if (pushUtil.getClient().isConnected()){
//                pushUtil.disconnect();
//                pushUtil.initMqtt(mqttBean,this,this);
                StartSubscribe();
            }else{
                ReConnect();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Log.i(TAG, "连接成功 ");
        isConnected = true;
        StartSubscribe();
    }

    private void StartSubscribe() {
        Log.i(TAG, "StartSubscribe: ");
        List<PushDataBean.ObjBean.SubscribeListBean> subscribeList = pushDataBean.getObj().getSubscribeList();
        List<TopicBean> topicList = TopicManager.queryTopicInfoList(this);
        if (subscribeList!=null&&subscribeList.size()!=0){
            Log.i(TAG, "StartSubscribe: subscribeList:"+subscribeList.toString());
            String[] topic = new String[subscribeList.size()];
            int[] qosArr = new int[subscribeList.size()];
            for (int i = 0; i < subscribeList.size(); i++) {
                String topicName = subscribeList.get(i).getTopicName();
                int qos = subscribeList.get(i).getQos();
                topic[i] = topicName;
                qosArr[i] = qos ;
                boolean hasTopic = hasTopic(topicList, topicName,qos);
                if (!hasTopic){
                    saveTopic(topicName, qos);
                }
            }
            try {
                pushUtil.getClient().subscribe(topic, qosArr, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, "client subscribe onSuccess: ");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i(TAG, "client subscribe onFailure: ");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveTopic(String topicName, int qos) {
        Log.i(TAG, "saveTopic: ");
        TopicBean topicBean = new TopicBean();
        topicBean.setQos(qos);
        topicBean.setTopicName(topicName);
        TopicManager.saveTopicInfo(this,topicBean);
    }

    private boolean hasTopic(List<TopicBean> topicList, String topicName, int qos) {
        boolean hasTopic = false;
        try {
            if (topicList!=null&&topicList.size()!=0){
                for (int j = 0; j < topicList.size(); j++) {
                    String topicName2 = topicList.get(j).getTopicName();
                    int qos2 = topicList.get(j).getQos();
                    if (topicName2.equals(topicName)){
                        hasTopic = true;
                        if (qos==qos2){
                            Log.i(TAG, "hasTopic: don't update");
                        }else{
                            Log.i(TAG, "hasTopic: update topic");
                            TopicBean topicBean = new TopicBean();
                            topicBean.setQos(qos);
                            topicBean.setTopicName(topicName);
                            TopicManager.updateTopicInfo(this,topicBean);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return hasTopic;
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        exception.printStackTrace();
        Log.i(TAG, "onFailure 连接失败:" + exception.getMessage());
        ReConnect();
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.i(TAG, "连接断开");
        isConnected = false;
        ReConnect();
    }
    private long ReConnectTime = 0L;
    DisposableObserver<Long> ReConnectObserver;
    private void ReConnect() {
        Log.i(TAG, "ReConnect: ");
        if (ReConnectObserver!=null){
            if (!ReConnectObserver.isDisposed()){
                ReConnectObserver.dispose();
            }
        }
         ReConnectObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                pushUtil.doClientConnection();
                onComplete();
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ", e);
                dispose();
                ReConnect();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
                dispose();
            }
        };
        Observable.interval(0,10, TimeUnit.SECONDS)
               .subscribeOn(Schedulers.io())
               .filter(new Predicate<Long>() {
                   @Override
                   public boolean test(Long aLong) throws Exception {
                       if (System.currentTimeMillis() -ReConnectTime >1000){
                           Log.i(TAG, "test: restart connect");
                           ReConnectTime = System.currentTimeMillis();
                           return true;
                       }else{
                           Log.i(TAG, "test: is not connect");
                           return false;
                       }
                   }
               })
//               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(ReConnectObserver);
//               .observeOn()
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
//        Log.i(TAG, "收到消息： " + new String(message.getPayload()) + "\tToString:" + message.toString());
        Log.i(TAG, "messageArrived: get topic:"+topic);
//        Log.i(TAG, "messageArrived: get message:"+message.toString());
        Log.i(TAG, "messageArrived: get message to UTF-8:"+new String(message.getPayload(),"UTF-8"));
        try {
            String msg =new String(message.getPayload(),"UTF-8");
            if (!TextUtils.isEmpty(msg)){
                Object object = new JSONTokener(msg).nextValue();
                if (object instanceof JSONObject){
//                    JSONObject json = (JSONObject)object;
                    PushInfoBean pushInfoBean = new Gson().fromJson(msg, PushInfoBean.class);
                    pushInfoBean.setPushName(topic);
                    pushInfoBean.setAcc_time(System.currentTimeMillis());
                    pushInfoBean.setPushStatus("2");
                    if (pushInfoBean.getPushType().equals("4")){
                        WeatherDialog weatherDialog = WeatherDialog.getInstance(getApplicationContext());
                        if (!weatherDialog.isShow()) {
                            weatherDialog.showView();
                        }
                        weatherDialog.addPushInfoBean(pushInfoBean);
                    }else{
                        PushInfoUtil.savePushInfo(this,pushInfoBean);
                        Intent intent = new Intent(this,ShowPushService.class);
                        intent.setAction(ShowPushService.UPDATE_ACTION);
                        startService(intent);
                    }
                    long time = System.currentTimeMillis();
                    long expireTime = pushInfoBean.getExpireTime();
                    boolean isExpire = TimeUtil.isExpire(time,expireTime);
                    if (!isExpire){
                        uploadLog(pushInfoBean);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadLog(PushInfoBean pushInfoBean) {
        PushLog pushLog = new PushLog();
        pushLog.setTimestamp(System.currentTimeMillis());
        pushLog.setAcc_time(System.currentTimeMillis());
        PushManager.UpLoadPushLog(this,pushInfoBean,pushLog);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i(TAG, "deliveryComplete");
    }
}
