package com.vunke.catv_push.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.vunke.catv_push.R;
import com.vunke.catv_push.modle.PushInfoBean;
import com.vunke.catv_push.util.PushInfoUtil;
import com.vunke.catv_push.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class WeatherDialog {
    private static final String TAG = "weatherDialog";
    private static WeatherDialog instance;
    private PushInfoBean oldPushInfo;
    private PushInfoBean DisPlayPushInfo;
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

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
                    if (DisPlayPushInfo!=null){
                        Log.i(TAG, "handleMessage: has long term display");
                        if (oldPushInfo!=null){
                            if (DisPlayPushInfo.toString().equals(oldPushInfo.toString())){
                                Log.i(TAG, "handleMessage: 获取数据和当前推送内容一致");
                            }else{
                                oldPushInfo = DisPlayPushInfo;
                                showData(DisPlayPushInfo);
                            }
                        }else{
                            Log.i(TAG, "handleMessage: get old push is null");
                            oldPushInfo = DisPlayPushInfo;
                            showData(DisPlayPushInfo);
                        }
                    }
                    break;
            }
        }
    };
    public WeatherDialog(Context context){
        this.context = context;
        windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.LEFT| Gravity.TOP;
        // 设置图片格式，效果为背景透明
        layoutParams.format = PixelFormat.RGBA_8888;
//        pushList=new ArrayList<>();
    }
    public void savePushInfoBean(PushInfoBean pushInfoBean){
        Log.i(TAG, "savePushInfoBean: ");
        if (pushInfoBean!=null){
            if (1==pushInfoBean.getShowRules()){
                Log.i(TAG, "savePushInfoBean: has long term display");
                DisPlayPushInfo = pushInfoBean;
            }
        }
        if (null==oldPushInfo){
            oldPushInfo = pushInfoBean;
            showData(oldPushInfo);
            return;
        }
        if (pushInfoBean.toString().equals(oldPushInfo.toString())){
            Log.i(TAG, "savePushInfoBean: 获取数据和当前推送内容一致");
            return;
        }else{
            oldPushInfo = pushInfoBean;
            showData(oldPushInfo);
            return;
        }
    }
    private FrameLayout frameLayout;
    private ImageView imageView;
    private MarqueeTextView textView;
    public void showView(){
        Log.i(TAG, "showView: ");
        if (!isShow){
            frameLayout = new FrameLayout(context);
            FrameLayout.LayoutParams fragmeLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
            frameLayout.setLayoutParams(fragmeLayoutParams);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_weather,null);
            imageView= view.findViewById(R.id.weather_img);
            textView = view.findViewById(R.id.weather_textview);
            frameLayout.addView(view);
            windowManager.addView(frameLayout,layoutParams);
            isShow = true;
        }
    }
    private DisposableObserver<Long> showDataObserver;
    private void showData(final PushInfoBean pushInfoBean) {
        Log.i(TAG, "shwoData: ");
        if (showDataObserver!=null){
            if (!showDataObserver.isDisposed()){
                showDataObserver.dispose();
            }
        }
        long intervalTime = pushInfoBean.getIntervalTime();
        Log.i(TAG, "shwoData: 间隔时间:"+intervalTime+"分钟");
        Observable<Long> observable = Observable.interval(0,intervalTime,TimeUnit.MINUTES)
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
                                    }else{
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
                                if (DisPlayPushInfo!=null&&pushInfoBean.toString().equals(DisPlayPushInfo.toString())){
                                    DisPlayPushInfo = null;
                                }
                                SendShowImage(false);
                                SendShowText(false);
                                pushInfoBean.setPushStatus("4");
                                PushInfoUtil.uploadPushLog(pushInfoBean,context);
                                PushInfoUtil.deletePushInfo(context,pushInfoBean.getPushId());
                                SendShowLongTerm();
                                return false;
                            }
                        }else{
                            Log.i(TAG, "shwoData:test this push info status is stop,don't show");
                            return false;
                        }
                    }
                });
        showDataObserver = new DisposableObserver<Long>() {
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
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(showDataObserver);

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
    private boolean isShow= false;
    public boolean isShow(){
        return isShow;
    }

    public void dismiss(){
        try {
            Log.i(TAG, "dismiss: ");
            if (isShow) {
                isShow = false;
                windowManager.removeView(frameLayout);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
