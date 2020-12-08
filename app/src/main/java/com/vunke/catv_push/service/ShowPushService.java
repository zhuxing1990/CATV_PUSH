package com.vunke.catv_push.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vunke.catv_push.modle.PushInfoBean;
import com.vunke.catv_push.util.PushInfoUtil;
import com.vunke.catv_push.util.TimeUtil;
import com.vunke.catv_push.util.Utils;
import com.vunke.catv_push.view.PushDialog;
import com.vunke.catv_push.view.WeatherDialog;

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
//        initDeviceInfo();
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
        if (disposableObserver2!=null){
            if (!disposableObserver2.isDisposed()){
                Log.i(TAG, "disposeObserver: dispose disposableObserver2");
                disposableObserver.dispose();
            }
        }
    }

//    private DeviceInfoBean deviceInfoBean;
//    private void initDeviceInfo() {
//        deviceInfoBean = new DeviceInfoBean();
//        DevicesManager.queryDevicesInfo(this, deviceInfoBean);
//        Log.i(TAG, "initDeviceInfo: deviiceInfoBean:" + deviceInfoBean.toString());
//    }
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
    DisposableObserver<PushInfoBean> disposableObserver2;
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
                    String pushType = pushInfoBean.getPushType();
                    if ("4".equals(pushType)) {
                        Log.i(TAG, "startQuery: get pushtype is 4");
                        boolean isAuth = Utils.isAuth(ShowPushService.this);
                        if (isAuth){
                            Log.i(TAG, "weatherDialog: isAuth or push message is show,don't show");
                        }else{
                            Log.i(TAG, "weatherDialog: start show");
                            WeatherDialog weatherDialog = WeatherDialog.getInstance(getApplicationContext());
                            if (!weatherDialog.isShow()) {
                                weatherDialog.showView();
                            }
                            weatherDialog.addPushInfoBean(pushInfoBean);
                        }
                    }else{
                        Log.i(TAG, "startQuery: get pushtype is default ");
                        initShow(pushInfoBean);
                    }
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
//            Observable<PushInfoBean> pushInfoBeanObservable2 = Observable.fromIterable(pushInfoList)
//                    .filter(new Predicate<PushInfoBean>() {
//                        @Override
//                        public boolean test(PushInfoBean pushInfoBean) throws Exception {
//                            String pushType = pushInfoBean.getPushType();
//                            if ("4".equals(pushType)){
//                                Log.i(TAG, "test: get pushtype is 4");
//                                return true;
//                            }else{
//                                return false;
//                            }
//                        }
//                    });
//            disposableObserver2 = new DisposableObserver<PushInfoBean>() {
//                @Override
//                public void onNext(PushInfoBean pushInfoBean) {
//                    Log.i(TAG, "startQuery pushType = 4 onNext: "+pushInfoBean.toString());
//                    WeatherDialog weatherDialog = WeatherDialog.getInstance(getApplicationContext());
//                    if (!weatherDialog.isShow()){
//                        weatherDialog.showView();
//                    }
//                    weatherDialog.savePushInfoBean(pushInfoBean);
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    Log.i(TAG, "onError: ");
//                    e.printStackTrace();
//                    dispose();
//                }
//
//                @Override
//                public void onComplete() {
//                    Log.i(TAG, "onComplete: ");
//                    dispose();
//                }
//            };
//            pushInfoBeanObservable2.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(disposableObserver2);
        }else {
            Log.i(TAG, "startQuery: query over,not message");
        }
    }

    private void initShow(PushInfoBean pushInfoBean) {
        Log.i(TAG, "initShow: ");
        long time = System.currentTimeMillis();
        long expire_time = pushInfoBean.getExpireTime();
        Log.i(TAG, "initShow new time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,time));
        Log.i(TAG, "initShow expire_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,expire_time));
        boolean isExpire = TimeUtil.isExpire(time,expire_time);
        if (!isExpire){
            Log.i(TAG, "onNext: this push message not expire");
            long show_start_time = pushInfoBean.getShowStartTime();
            Log.i(TAG, "initShow show_start_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,show_start_time));
            long show_end_time = pushInfoBean.getShowEndTime();
            Log.i(TAG, "initShow show_end_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,show_end_time));
            boolean isShow =TimeUtil.isEffectiveDate(time,show_start_time,show_end_time);
            if (isShow){
                Log.i(TAG, "onNext: this push message can show");
//                String pushType = pushInfoBean.getPushType();
//                if ("4".equals(pushType)){
//                    Log.i(TAG, "initShow: pushType is 4 ,don't show");
//                }else{
//                    Log.i(TAG, "initShow: pushType !=4");
                    boolean isMangguoPlayer = Utils.isMangguoPlayer(this);
                    if (isMangguoPlayer){
                        Log.i(TAG, "initShow: mang guo is play video or push message is show,don't show");
                    }else{
                        try {
                            PushDialog instance = PushDialog.getInstance(this);
                            if (!instance.isShow()){
                                Log.i(TAG, "initShow: start show");
                                instance.showView(pushInfoBean);
                                pushInfoBean.setPushStatus("3");
                                PushInfoUtil.uploadPushLog(pushInfoBean,this);
                                PushInfoUtil.deletePushInfo(this,pushInfoBean.getPushId());
                            }else{
                                Log.i(TAG, "initShow:  dialog is show ,don't show");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
//                }

            }else{
                Log.i(TAG, "onNext: this push message can't show");
                boolean expire = TimeUtil.isExpire(time, show_end_time);
                if (expire){
                    Log.i(TAG, "initShow: show_end_time is expire");
                    PushInfoUtil.deletePushInfo(this,pushInfoBean.getPushId());
                }
            }
        }else{
            Log.i(TAG, "onNext: this push message is expire");
            PushInfoUtil.deletePushInfo(this,pushInfoBean.getPushId());
        }
    }




}
