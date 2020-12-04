package com.vunke.catv_push.base;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.lzy.okgo.OkGo;
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
        SharedPreferencesUtil.setBooleanValue(this,SharedPreferencesUtil.CleanSessionKey,true);
        OkGo.getInstance().init(this);
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
    }


}
