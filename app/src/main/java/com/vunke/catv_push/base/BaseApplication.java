package com.vunke.catv_push.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.vunke.catv_push.receiver.ChannelReceiver;
import com.vunke.catv_push.service.PushServer;
import com.vunke.catv_push.service.ShowPushService;
import com.vunke.catv_push.util.SharedPreferencesUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";
    public static BaseApplication instance;
    BaseApplication getInstance(){
        if (instance==null){
            instance = new BaseApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        OkGo.getInstance().init(this);
        Log.i(TAG, "onCreate: clear channel_id");
        SharedPreferencesUtil.setStringValue(this,BaseConfig.CHANNEL_ID,"");
        Log.i(TAG, "onCreate: clear clean session ");
        SharedPreferencesUtil.setBooleanValue(this,SharedPreferencesUtil.CleanSessionKey,true);
        startService(new Intent(this, PushServer.class));
        Observable.interval(20, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .subscribe(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        Log.i(TAG, "onNext: start showPushService");
                        startService(new Intent(getApplicationContext(), ShowPushService.class));
                        onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: ");
                        startService(new Intent(getApplicationContext(), ShowPushService.class));
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        dispose();
                    }
                });
        registerBroadcastReceiver(this);
    }
//注册广播
    private void registerBroadcastReceiver(Context context) {
        Log.i(TAG, "registerBroadcastReceiver: ");
        ChannelReceiver channelReceiver = new ChannelReceiver();
        IntentFilter myIntentFilter =new IntentFilter();
        myIntentFilter.addAction(ChannelReceiver.ACTION_NAME);
        context.registerReceiver(channelReceiver, myIntentFilter);
    }

}
