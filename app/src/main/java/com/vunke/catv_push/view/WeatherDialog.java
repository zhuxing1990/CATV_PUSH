package com.vunke.catv_push.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.vunke.catv_push.modle.PushInfoBean;
import com.vunke.catv_push.modle.WeatherInfoBean;
import com.vunke.catv_push.util.PushInfoUtil;
import com.vunke.catv_push.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class WeatherDialog extends  WeatherBaseDialog{
    private static final String TAG = "weatherDialog";
    private static WeatherDialog instance;
    private WeatherInfoBean oldWeatherInfoBean= new WeatherInfoBean();
    private WeatherInfoBean disPlayWeatherInfoBean= new WeatherInfoBean();
    private List<WeatherInfoBean> weatherList;

    public WeatherDialog(Context context) {
        super(context);
        weatherList = new ArrayList<>();
    }

    public static WeatherDialog getInstance(Context context) {
        if (instance==null){
            instance = new WeatherDialog(context);
        }
        return instance;
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x1888:
                    boolean isShow1 = (boolean) msg.obj;
                    showImage(isShow1);
                    break;
                case 0x1889:
                    boolean isShow2 = (boolean) msg.obj;
                    showText(isShow2);
                    break;
                case 0x1890:
                    if (disPlayWeatherInfoBean.getPushInfoBean()!=null){
                        Log.i(TAG, "handleMessage: has long term display");
                        if (oldWeatherInfoBean.getPushInfoBean()!=null){
                            if (disPlayWeatherInfoBean.getPushInfoBean().toString().equals(oldWeatherInfoBean.getPushInfoBean().toString())){
                                Log.i(TAG, "handleMessage: 获取数据和当前推送内容一致");
                            }else{
                                oldWeatherInfoBean = disPlayWeatherInfoBean;
                                if (disPlayWeatherInfoBean.isShow()==true){
                                    Log.i(TAG, "handleMessage: 获取当前数据正在显示");
                                }else{
                                    Log.i(TAG, "handleMessage: 获取当前常显数据没有显示");
                                    showData(disPlayWeatherInfoBean);
                                }
//                                showData(disPlayWeatherInfoBean);
                            }
                        }else{
                            Log.i(TAG, "handleMessage: get old push is null");
                            oldWeatherInfoBean = disPlayWeatherInfoBean;
                            if (disPlayWeatherInfoBean.isShow()==true){
                                Log.i(TAG, "handleMessage: 获取当前数据正在显示");
                            }else{
                                Log.i(TAG, "handleMessage: 获取当前常显数据没有显示");
                                showData(disPlayWeatherInfoBean);
                            }
//                            showData(disPlayWeatherInfoBean);
                        }
                    }
                    break;
                case 0x1990:
                    PushInfoBean pushInfoBean = (PushInfoBean) msg.obj;
                    startQuery(pushInfoBean);
                    break;
            }
        }
    };

    public void addPushInfoBean(PushInfoBean pushInfoBean){
        Log.i(TAG, "addPushInfoBean: ");
        if (pushInfoBean!=null){
            if (1==pushInfoBean.getShowRules()){
                Log.i(TAG, "addPushInfoBean: has long term display");
                disPlayWeatherInfoBean.setPushInfoBean(pushInfoBean);
            }
        }
        Log.i(TAG, "addPushInfoBean: get push "+pushInfoBean.toString());
        if (null==oldWeatherInfoBean.getPushInfoBean()){
            oldWeatherInfoBean.setPushInfoBean(pushInfoBean);
        }else if (pushInfoBean.toString().equals(oldWeatherInfoBean.getPushInfoBean().toString())){
            Log.i(TAG, "addPushInfoBean: 获取数据和当前推送内容一致");
        }else{
            oldWeatherInfoBean.setPushInfoBean(pushInfoBean);
        }
        QueryPushInfo(pushInfoBean);
    }

    private void QueryPushInfo(final PushInfoBean pushInfoBean) {
        Log.i(TAG, "QueryPushInfo: ");
        try {
            if(weatherList!=null&&weatherList.size()!=0){
                Log.i(TAG, "QueryPushInfo: weatherList size:"+ weatherList.size());
               Message msg = Message.obtain();
               msg.what= 0x1990;
               msg.obj = pushInfoBean;
               handler.sendMessageDelayed(msg,10000);
//               handler.sendMessage(msg);
            }else{
                Log.i(TAG, "QueryPushInfo: weatherList is null");
                if (pushInfoBean!=null){
                    WeatherInfoBean weatherInfoBean = new WeatherInfoBean();
                    weatherInfoBean.setPushInfoBean(pushInfoBean);
                    weatherList.add(weatherInfoBean);
                    showData(weatherInfoBean);
                }
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void startQuery(final PushInfoBean pushInfoBean) {
        Log.i(TAG, "startQuery: ");
        boolean hasNewMessage = true;
        for (int i = 0; i < weatherList.size(); i++) {
            WeatherInfoBean weatherInfoBean = weatherList.get(i);
            PushInfoBean oldbean = weatherInfoBean.getPushInfoBean();
//            Log.i(TAG, "startQuery: oldbean:"+oldbean.toString());
            if (oldbean.toString().equals(pushInfoBean.toString())){
                Log.i(TAG, "startQuery: has pushinfo to weatherList");
            }else{
                if (oldbean.getGroupId().equals(pushInfoBean.getGroupId())){
                    Log.i(TAG, "startQuery: message is change,update message ");
                    hasNewMessage = false;
                    weatherInfoBean.setPushInfoBean(pushInfoBean);
                    oldbean=pushInfoBean;
                    Log.i(TAG, "startQuery: set oldbean:"+oldbean.toString());
                    showData(weatherInfoBean);
                }
            }
        }
        if (hasNewMessage){
            Log.i(TAG, "startQuery: has new message");
            WeatherInfoBean weatherInfoBean = new WeatherInfoBean();
            weatherInfoBean.setPushInfoBean(pushInfoBean);
            weatherList.add(weatherInfoBean);
            showData(weatherInfoBean);
        }
    }

    private void disposedShowDataObservable(WeatherInfoBean weatherInfoBean) {
        if (weatherInfoBean.getShowDataObserver()!=null){
            if (!weatherInfoBean.getShowDataObserver().isDisposed()){
                weatherInfoBean.getShowDataObserver().dispose();
            }
        }
    }
    private void disposedExpireDataObservable(WeatherInfoBean weatherInfoBean) {
        if (weatherInfoBean.getExpirePbservable()!=null){
            if (!weatherInfoBean.getExpirePbservable().isDisposed()){
                weatherInfoBean.getExpirePbservable().dispose();
            }
        }
    }


    private void showData(final WeatherInfoBean weatherInfoBean) {
        Log.i(TAG, "shwoData: ");
        disposedShowDataObservable(weatherInfoBean);
        final PushInfoBean pushInfoBean = weatherInfoBean.getPushInfoBean();
        long intervalTime = pushInfoBean.getIntervalTime();
        Log.i(TAG, "shwoData: 间隔时间:"+intervalTime+"分钟");
        Observable<Long> observable = Observable.interval(0,intervalTime,TimeUnit.MINUTES)//------------正式
//        Observable<Long> observable = Observable.interval(0,intervalTime,TimeUnit.SECONDS)//------------测式
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        Log.i(TAG, "shwoData test: ");
                        String pushStatus = pushInfoBean.getPushStatus();
                        if (!TextUtils.isEmpty(pushStatus)||"0".equals(pushStatus)||"1".equals(pushStatus)||"2".equals(pushStatus)){
                            long time = System.currentTimeMillis();
                            long expire_time = pushInfoBean.getExpireTime();
                            boolean isExpire = TimeUtil.isExpire(time,expire_time);
                            Log.i(TAG, "shwoData new time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,time));
                            Log.i(TAG, "shwoData expire_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,expire_time));
                            if (!isExpire){
                                Log.i(TAG, "shwoData: this push message not expire");
                                long show_start_time = pushInfoBean.getShowStartTime();
                                Log.i(TAG, "shwoData show_start_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,show_start_time));
                                long show_end_time = pushInfoBean.getShowEndTime();
                                Log.i(TAG, "shwoData show_end_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,show_end_time));
                                boolean isShow =TimeUtil.isEffectiveDate(time,show_start_time,show_end_time);
                                Log.i(TAG, "shwoData:test isShow:"+isShow);
                                if (isShow){
                                    if (pushInfoBean.getStartTime()==0&&pushInfoBean.getEndTime()==0){
                                        return true;
                                    }else if (pushInfoBean.getStartTime() == pushInfoBean.getEndTime())
                                        return true;
                                     else{
                                        long startTime = pushInfoBean.getStartTime();
                                        Log.i(TAG, "shwoData startTime: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,startTime));
                                        long endTime = pushInfoBean.getEndTime();
                                        Log.i(TAG, "shwoData endTime: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,endTime));
                                        SimpleDateFormat tf = new SimpleDateFormat(TimeUtil.dateFormatHMS);
                                        Date currentTime = tf.parse(tf.format(time));
                                        long timeTime = currentTime.getTime();
                                        Log.i(TAG, "shwoData timeTime: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,timeTime));
                                        boolean effectiveDate = TimeUtil.isEffectiveDate(timeTime, startTime, endTime);
                                        Log.i(TAG, "shwoData: effectiveDate:"+effectiveDate);
                                        return effectiveDate;
                                    }
                                }else{
                                    return false;
                                }
                            }else{
                                Log.i(TAG, "shwoData:test this push message is expire");
                                if (disPlayWeatherInfoBean.getPushInfoBean()!=null&&pushInfoBean.toString().equals(disPlayWeatherInfoBean.getPushInfoBean().toString())){
                                    disPlayWeatherInfoBean.setPushInfoBean(null);
                                    disPlayWeatherInfoBean.setShow(false);
                                    disposedShowDataObservable(disPlayWeatherInfoBean);
                                    disposedExpireDataObservable(disPlayWeatherInfoBean);
                                }
                                SendShowImage(false);
                                SendShowText(false);
                                pushInfoBean.setPushStatus("4");
                                PushInfoUtil.uploadPushLog(pushInfoBean,context);
                                PushInfoUtil.deletePushInfo(context,pushInfoBean.getPushId());
                                weatherInfoBean.setShow(false);
                                RemoveExpireMessage(weatherInfoBean);
                                SendShowLongTerm();
                                return false;
                            }
                        }else{
                            Log.i(TAG, "shwoData:test this push info status is stop,don't show");
                            return false;
                        }
                    }
                });
        DisposableObserver<Long> showDataObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                Log.i(TAG, "shwoData onNext: ");
                String pushUrl = pushInfoBean.getPushUrl();
                String pushMessage = pushInfoBean.getPushMessage();
                Glide.with(context).load(pushUrl)
//                                .skipMemoryCache(true) // 不使用内存缓存
//                                .diskCacheStrategy(DiskCacheStrategy.NONE)// 不使用磁盘缓存
                        .into(imageView);
                textView.setText(pushMessage);
                SendShowImage(true);
                SendShowText(true);
                pushInfoBean.setPushStatus("3");
                PushInfoUtil.uploadPushLog(pushInfoBean, context);
                hideView(pushInfoBean);
                weatherInfoBean.setShow(true);
                oldWeatherInfoBean.setShow(true);
                if(1==pushInfoBean.getShowRules()){
                   disPlayWeatherInfoBean.setShow(true);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "shwoData OnError: ");
                e.printStackTrace();
                dispose();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "shwoData onComplete: ");
                dispose();
            }
        };
        weatherInfoBean.setShowDataObserver(showDataObserver);
        oldWeatherInfoBean.setShowDataObserver(showDataObserver);
        if (1==weatherInfoBean.getPushInfoBean().getShowRules()){
            disPlayWeatherInfoBean.setShowDataObserver(showDataObserver);
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherInfoBean.getShowDataObserver());
        getExpireObservable(weatherInfoBean);
    }

    private void RemoveExpireMessage(WeatherInfoBean weatherInfoBean) {
        try {
            weatherList.remove(weatherInfoBean);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getExpireObservable(final WeatherInfoBean weatherInfoBean) {
        Log.i(TAG, "getExpireObservable: ");
        disposedExpireDataObservable(weatherInfoBean);
        final PushInfoBean pushInfoBean = weatherInfoBean.getPushInfoBean();
        DisposableObserver<Long> expirePbservable = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                long time = System.currentTimeMillis();
                long expire_time = pushInfoBean.getExpireTime();
                boolean isExpire = TimeUtil.isExpire(time, expire_time);
                Log.i(TAG, "shwoData new time: " + TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese, time));
                Log.i(TAG, "shwoData expire_time: " + TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese, expire_time));
                if (isExpire) {
                    Log.i(TAG, "shwoData:test this push message is expire");
                    if (disPlayWeatherInfoBean.getPushInfoBean() != null && pushInfoBean.toString().equals(disPlayWeatherInfoBean.getPushInfoBean().toString())) {
                        disPlayWeatherInfoBean.setPushInfoBean(null);
                        disPlayWeatherInfoBean.setShow(false);
                        disPlayWeatherInfoBean.getShowDataObserver().dispose();
                    }
                    SendShowImage(false);
                    SendShowText(false);
                    pushInfoBean.setPushStatus("4");
                    PushInfoUtil.uploadPushLog(pushInfoBean, context);
                    PushInfoUtil.deletePushInfo(context, pushInfoBean.getPushId());
                    SendShowLongTerm();
                    dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.i(TAG, "onComplete:  get expire  has error");
                dispose();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: message expire");
                dispose();
            }
        };
        weatherInfoBean.setExpirePbservable(expirePbservable);
        if (1==weatherInfoBean.getPushInfoBean().getShowRules()){
            disPlayWeatherInfoBean.setExpirePbservable(expirePbservable);
        }
        Observable.interval(1, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherInfoBean.getExpirePbservable());
    }

    private void SendShowLongTerm() {
        Log.i(TAG, "SendShowLongTerm: ");
       handler.sendEmptyMessage(0x1890);
    }

    DisposableObserver<Long> disposableObserver;
    private void hideView(final PushInfoBean pushInfoBean){
        if (disposableObserver!=null){
            if (!disposableObserver.isDisposed()){
                disposableObserver.dispose();
            }
        }
        Log.i(TAG, "hideView: ");
        long showTimes = pushInfoBean.getShowTimes();
        Log.i(TAG, "hideView: 显示时间:"+showTimes+"秒");
        disposableObserver= new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                Log.i(TAG, "hideView onNext: ");
                SendShowText(false);
                int showRules = pushInfoBean.getShowRules();
                if (0 == showRules) {
                    SendShowImage(false);
                    SendShowLongTerm();
                }
                onComplete();
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "hideView onError: ");
                e.printStackTrace();
                dispose();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "hideView onComplete: ");
                dispose();
            }
        };
        Observable.interval(showTimes,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
    }

    private void showImage(boolean show){
        Log.i(TAG, "showImage: :"+show);
        if (null!=imageView){
            if (show){
                if (imageView.getVisibility()!=View.VISIBLE){
                    imageView.setVisibility(View.VISIBLE);
                }
            }else{
                if (imageView.getVisibility()!=View.GONE){
                    imageView.setVisibility(View.GONE);
                }
            }
        }
    }
    private void showText(boolean show){
        Log.i(TAG, "showText: :"+show);
        if (null!=textView){
            if (show){
                if (textView.getVisibility()!=View.VISIBLE){
                    textView.setVisibility(View.VISIBLE);
                }
            }else{
                if (textView.getVisibility()!=View.GONE){
                    textView.setVisibility(View.GONE);
                }
            }
        }
    }
    private void SendShowImage(boolean isShow){
        Message msg = Message.obtain();
        msg.obj= isShow;
        msg.what = 0x1888;
        handler.sendMessage(msg);
    }
    private void SendShowText(boolean isShow){
        Message msg = Message.obtain();
        msg.obj= isShow;
        msg.what = 0x1889;
        handler.sendMessage(msg);
    }

}
