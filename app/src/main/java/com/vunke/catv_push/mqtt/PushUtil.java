package com.vunke.catv_push.mqtt;

import android.content.Context;
import android.util.Log;

import com.vunke.catv_push.modle.MqttBean;
import com.vunke.catv_push.util.SharedPreferencesUtil;
import com.vunke.catv_push.util.Utils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by zhuxi on 2020/7/29.
 */

public class PushUtil {
    private static final String TAG = "PushUtil";
    private static PushUtil  instance;
    private MqttAndroidClient client;
    private MqttConnectOptions mMqttConnectOptions;
    public static PushUtil getInstance(Context mcontext){

        if (instance==null){
            instance = new PushUtil(mcontext);
        }
        return instance;
    }
    private Context context;
    public PushUtil(Context context){
        this.context = context;
    }

    private MqttCallback mqttCallback;
    private IMqttActionListener iMqttActionListener;
    public void initMqtt(MqttBean mqttBean, MqttCallback mqttCallback, IMqttActionListener iMqttActionListener){
        Log.i(TAG, "initMqtt: ");
        this.mqttCallback = mqttCallback;
        this.iMqttActionListener = iMqttActionListener;
        client = new MqttAndroidClient(context,mqttBean.getHost(),mqttBean.getClientId());
        client.setCallback(mqttCallback); //设置订阅消息的回调
        mMqttConnectOptions = new MqttConnectOptions();
        boolean hasSession = SharedPreferencesUtil.getBooleanValue(context, SharedPreferencesUtil.CleanSessionKey, true);
        if (hasSession){
            Log.i(TAG, "initMqtt: hasSession:"+hasSession);
            SharedPreferencesUtil.setBooleanValue(context, SharedPreferencesUtil.CleanSessionKey, false);
            mMqttConnectOptions.setCleanSession(true); //设置是否清除缓存
        }else{
            Log.i(TAG, "initMqtt: hasSession:"+hasSession);
            mMqttConnectOptions.setCleanSession(false); //设置是否清除缓存
        }
        mMqttConnectOptions.setConnectionTimeout(mqttBean.getConnectionTimeout()); //设置超时时间，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(mqttBean.getKeepAliveInterval()); //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setUserName(mqttBean.getUserName()); //设置用户名
        mMqttConnectOptions.setPassword(mqttBean.getPassWord().toCharArray()); //设置密码
        doClientConnection();
    }

    /**
     * 连接MQTT服务器
     */
    public void doClientConnection() {
        Log.d(TAG,"是否链接成功：" + client.isConnected());
        if (!client.isConnected() && Utils.isNetConnected(context)) {
            try {
                client.connect(mMqttConnectOptions, null, iMqttActionListener);
            } catch (MqttException e) {
                Log.d(TAG,"doClientConnection:" + e.getMessage());
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private boolean isConnectSuccess = false, isConnectionLost = true;

    public MqttConnectOptions getmMqttConnectOptions() {
        return mMqttConnectOptions;
    }

    public void setmMqttConnectOptions(MqttConnectOptions mMqttConnectOptions) {
        this.mMqttConnectOptions = mMqttConnectOptions;
    }

    public MqttAndroidClient getClient() {
        return client;
    }

    public void setClient(MqttAndroidClient client) {
        this.client = client;
    }
    //断开链接
    public void disconnect() {
        try {
            if (client != null){
                if (client.isConnected()){
                    Log.i(TAG, "disconnect: client");
                    client.disconnect();
                }
            }

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
