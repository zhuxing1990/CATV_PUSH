package com.vunke.catv_push.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vunke.catv_push.modle.PushInfoBean;
import com.vunke.catv_push.util.PushInfoUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuxi on 2020/8/6.
 */

public class ShowPushService extends Service {
    private static final String TAG = "ShowPushService";
    public static final String UPDATE_ACTION = "com.vunke.update.action";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null){
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)){
                if (UPDATE_ACTION.equals(action)){
                    Log.i(TAG, "onStartCommand: get action:"+action);
                    disposeObserver();
                }
            }
            initTimer();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void disposeObserver() {
        if (disposableObserver != null) {
            if (!disposableObserver.isDisposed()){
                Log.i(TAG, "disposeObserver: dispose disposableObserver");
                disposableObserver.dispose();
            }
        }
    }


    private DisposableObserver<Long> timerObserver;
    private void initTimer() {
        Log.i(TAG, "initTimer: ");
        if (timerObserver!=null){
            if (!timerObserver.isDisposed()){
                Log.i(TAG, "initTimer: dispose timerObserver");
                timerObserver.dispose();
            }
        }
        timerObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                Log.i(TAG, "initTimer onNext: ");
                startQuery();
            }

            @Override
            public void onError(Throwable e) {
                dispose();
                Log.i(TAG, "initTimer onError: ");
                initTimer();
            }

            @Override
            public void onComplete() {
                dispose();
                initTimer();
            }
        };
        Observable.interval(2,300, TimeUnit.SECONDS)
//        Observable.interval(0,10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(timerObserver);
    }
    DisposableObserver<PushInfoBean> disposableObserver;
    private void startQuery() {
        Log.i(TAG, "startQuery: ");
        disposeObserver();
        List<PushInfoBean> pushInfoList = PushInfoUtil.queryPushInfo(getApplicationContext());
        if (null!=pushInfoList&& pushInfoList.size()!=0) {
            Observable<PushInfoBean> pushInfoBeanObservable = Observable.fromIterable(pushInfoList)
                    .filter(new Predicate<PushInfoBean>() {
                        @Override
                        public boolean test(PushInfoBean pushInfoBean) throws Exception {
//                            Log.i(TAG, "test: "+pushInfoBean.toString());
                            String pushStatus = pushInfoBean.getPushStatus();
                            if (!TextUtils.isEmpty(pushStatus)||"0".equals(pushStatus)||"1".equals(pushStatus)||"2".equals(pushStatus)){
                                Log.i(TAG, "test: push info status ok");
                                return true;
                            }else {
                                Log.i(TAG, "test: this push info status is stop,don't show");
                                return false;
                            }
                        }
                    });
            Observable<Long> interval = Observable.interval(0,1, TimeUnit.MINUTES);
            Observable<PushInfoBean> zipObservable = Observable.zip(pushInfoBeanObservable, interval, new BiFunction<PushInfoBean, Long, PushInfoBean>() {
                        @Override
                        public PushInfoBean apply(PushInfoBean pushInfoBean, Long aLong) throws Exception {
                            Log.i(TAG, "apply: ");
                            return pushInfoBean;
                }
            });
            disposableObserver = new DisposableObserver<PushInfoBean>() {
                @Override
                public void onNext(PushInfoBean pushInfoBean) {
                    Log.i(TAG, "startQuery onNext: "+pushInfoBean);
                    PushInfoUtil.initShow(ShowPushService.this,pushInfoBean);
                }

                @Override
                public void onError(Throwable e) {
                    Log.i(TAG, "startQuery default push onError: ");
                    dispose();
                }

                @Override
                public void onComplete() {
                    Log.i(TAG, "startQuery default push onComplete: ");
                    dispose();
                }
            };
            zipObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(disposableObserver);
        }else {
            Log.i(TAG, "startQuery: query over,not message");
        }
    }




}
