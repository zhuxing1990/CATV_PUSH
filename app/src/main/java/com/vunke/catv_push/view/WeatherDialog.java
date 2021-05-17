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
import com.vunke.catv_push.util.Utils;

import java.text.ParseException;
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
                case 0x1890://接收常显的消息
                    try {
                        if (disPlayWeatherInfoBean!=null&&disPlayWeatherInfoBean.getPushInfoBean()!=null){//判断当前显示的内容是否为空
                            Log.i(TAG, "handleMessage: has long term display");
                            Log.i(TAG, "handleMessage: get displayinfo:"+disPlayWeatherInfoBean.getPushInfoBean().toString());
                            int showRules = disPlayWeatherInfoBean.getPushInfoBean().getShowRules();//判断该数据是否为常显
                            if (0==showRules){//如果当前的数据不为常显，则设置当前显示的数据为空
                                Log.i(TAG, "handleMessage: this message not disPlay,delete disPlayInfo");
                                disPlayWeatherInfoBean.setPushInfoBean(null);
                                return;
                            }
                            boolean isExpire = getExpire(disPlayWeatherInfoBean.getPushInfoBean());//判断显示的数据是否已经失效
                            if (isExpire){//如果失效则设置当前显示的数据为空
                                Log.i(TAG, "handleMessage: get displayinfo is expire");
                                disPlayWeatherInfoBean.setPushInfoBean(null);
                                disPlayWeatherInfoBean.setShow(false);//设置当前显示的数据为不显示
                                return;
                            }
                            if (oldWeatherInfoBean!=null&&oldWeatherInfoBean.getPushInfoBean()!=null){//判断当前老的数据是否为空
                                if (disPlayWeatherInfoBean.getPushInfoBean().toString().equals(oldWeatherInfoBean.getPushInfoBean().toString())){//判断当前显示的数据是否和老数据是否一致
                                    Log.i(TAG, "handleMessage: 获取数据和当前推送内容一致");
                                    if (!oldWeatherInfoBean.isShow()||!disPlayWeatherInfoBean.isShow()){//如果老数据或者当前显示的数据都没有显示
                                        Log.i(TAG, "handleMessage: show disPlayInfo ");
                                        showData(disPlayWeatherInfoBean);//显示需要常显的数据
                                    }else{//当前显示的数据在显示，不处理
                                        Log.i(TAG, "handleMessage: disPlayInfo is Show");
                                    }
                                }else{
                                    oldWeatherInfoBean = disPlayWeatherInfoBean;//设置老数据为当前显示的数据
                                    if (disPlayWeatherInfoBean.isShow()==true){//如果当前显示的数据正在显示
                                        Log.i(TAG, "handleMessage: 获取当前数据正在显示");
                                    }else{//否则 显示需要常显的数据
                                        Log.i(TAG, "handleMessage: 获取当前常显数据没有显示");
                                        showData(disPlayWeatherInfoBean);
                                    }
//                                showData(disPlayWeatherInfoBean);
                                }
                            }else{//否则老数据为空
                                Log.i(TAG, "handleMessage: get old push is null");
                                oldWeatherInfoBean = disPlayWeatherInfoBean;//设置老数据为当前显示的数据
                                if (disPlayWeatherInfoBean.isShow()==true){//如果当前显示的数据正在显示
                                    Log.i(TAG, "handleMessage: 获取当前数据正在显示");
                                }else{//否则 显示需要常显的数据
                                    Log.i(TAG, "handleMessage: 获取当前常显数据没有显示");
                                    showData(disPlayWeatherInfoBean);
                                }
//                            showData(disPlayWeatherInfoBean);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 0x1990:
                    try {
                        PushInfoBean pushInfoBean = (PushInfoBean) msg.obj;
                        Log.i(TAG, "handleMessage: delayed send pushinfo:"+pushInfoBean.toString());
                        startQuery(pushInfoBean);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 0x1991:
                    try {
                        Log.i(TAG, "handleMessage: message Delayed over ,start show");
                        WeatherInfoBean weatherInfoBean = (WeatherInfoBean)msg.obj;
                        showData(weatherInfoBean);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void addPushInfoBean(PushInfoBean pushInfoBean){
        Log.i(TAG, "addPushInfoBean: ");
        if (pushInfoBean!=null){
            try {
                if (disPlayWeatherInfoBean!=null&&disPlayWeatherInfoBean.getPushInfoBean()!=null){//判断当前显示的数据是否为空
                    if (pushInfoBean.getGroupId().equals(disPlayWeatherInfoBean.getPushInfoBean().getGroupId())){//判断ID是否一致
                        Log.i(TAG, "addPushInfoBean: disPlay is update,delete pushinfo");
                        if (0==pushInfoBean.getShowRules()){//判断当前数据是否常显，如非常显，则清空当前显示的数据
                            disPlayWeatherInfoBean.setPushInfoBean(null);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if (1==pushInfoBean.getShowRules()){//如数据为常显数据，则更新 当前显示的数据
                Log.i(TAG, "addPushInfoBean: has long term display");
                disPlayWeatherInfoBean.setPushInfoBean(pushInfoBean);
            }

        }
        Log.i(TAG, "addPushInfoBean: get push "+pushInfoBean.toString());
        if (null==oldWeatherInfoBean.getPushInfoBean()){//判断 老的推送数据是否为空
            oldWeatherInfoBean.setPushInfoBean(pushInfoBean);//如未空则设置老数据
        }else if (pushInfoBean.toString().equals(oldWeatherInfoBean.getPushInfoBean().toString())){//如果推送内容一致则不更新数据
            Log.i(TAG, "addPushInfoBean: 获取数据和当前推送内容一致");
        }else{//否则则更新数据
            oldWeatherInfoBean.setPushInfoBean(pushInfoBean);
        }
        //开始查询当前数据是否需要更新
        QueryPushInfo(pushInfoBean);
    }

    private void QueryPushInfo(final PushInfoBean pushInfoBean) {
        Log.i(TAG, "QueryPushInfo: ");
        try {
            if(weatherList!=null&&weatherList.size()!=0){//判断集合的数据是否为空，不为空则延迟5秒推送当前数据
                Log.i(TAG, "QueryPushInfo: weatherList size:"+ weatherList.size());
               Message msg = Message.obtain();
               msg.what= 0x1990;
               msg.obj = pushInfoBean;
               handler.sendMessageDelayed(msg,5000L);
//               handler.sendMessage(msg);
            }else{
                Log.i(TAG, "QueryPushInfo: weatherList is null");
                if (pushInfoBean!=null){//判断数据是否为空，如不为空，这直接开始显示当前数据
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


    /**
     * 显示当前数据
     * @param weatherInfoBean
     */
    private void showData(final WeatherInfoBean weatherInfoBean) {
        Log.i(TAG, "shwoData: ");
        boolean isAuth = Utils.isAuth(context);
        if (isAuth){//判断当前的界面是否在认证APK 界面上，如果在则延迟5秒发送数据
            Log.i(TAG, "weatherDialog: isAuth or push message is show,don't show");
            Message msg = Message.obtain();
            msg.what = 0x1991;
            msg.obj = weatherInfoBean;
            handler.sendMessageDelayed(msg,5000L);
            return;
        }
        Log.i(TAG, "weatherDialog: start show");
        disposedShowDataObservable(weatherInfoBean);//取消订阅当前的显示轮询
        final PushInfoBean pushInfoBean = weatherInfoBean.getPushInfoBean();
        long intervalTime = pushInfoBean.getIntervalTime();//获取推送间隔
        Log.i(TAG, "shwoData: 间隔时间:"+intervalTime+"分钟");
        Observable<Long> observable = Observable.interval(0,intervalTime,TimeUnit.MINUTES)//------------正式  根据间隔时间，每1分钟轮询1次
//        Observable<Long> observable = Observable.interval(0,intervalTime,TimeUnit.SECONDS)//------------测试
                .filter(new Predicate<Long>() {//每1分钟过滤一次数据
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        Log.i(TAG, "shwoData test: ");
                        String pushStatus = pushInfoBean.getPushStatus();//获取推送数据的状态
                        if (!TextUtils.isEmpty(pushStatus)||"0".equals(pushStatus)||"1".equals(pushStatus)||"2".equals(pushStatus)){//推送状态不为空，或者推送状态在 0 1 2 中则通过
                            long time = System.currentTimeMillis();//获取系统时间
                            long expire_time = pushInfoBean.getExpireTime();//获取失效时间
                            Log.i(TAG, "shwoData new time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,time));//打印当前时间
                            Log.i(TAG, "shwoData expire_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,expire_time));//打印失效的时间
                            boolean isExpire = TimeUtil.isExpire(time,expire_time);//验证当前数据是否失效
                            if (!isExpire){ //如果数据不失效
                                Log.i(TAG, "shwoData: this push message not expire");
                                long show_start_time = pushInfoBean.getShowStartTime();//获取显示开始的时间
                                Log.i(TAG, "shwoData show_start_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,show_start_time));
                                long show_end_time = pushInfoBean.getShowEndTime();//获取显示结束的时间
                                Log.i(TAG, "shwoData show_end_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,show_end_time));
                                if (0==show_start_time&&0==show_end_time){ //判断开始时间和结束时间是否都等于0 。等于0则通过
                                    return isShow(time, pushInfoBean, weatherInfoBean);
                                }else if (show_start_time ==show_end_time) { //判断开始时间是否等于结束时间，等于则通过
                                    return isShow(time, pushInfoBean, weatherInfoBean);
                                } else{
                                    boolean isShow =TimeUtil.isEffectiveDate(time,show_start_time,show_end_time);//判断是否在显示的时间段内
                                    Log.i(TAG, "shwoData:test isShow:"+isShow);
                                    if (isShow){ //如果在显示的时间段内
                                        return isShow(time, pushInfoBean, weatherInfoBean);
                                    }else{
                                        return false;
                                    }
                                }
                            }else{//否则当前数据失效
                                Log.i(TAG, "test this push message is expire");
                                //发现当前数据已经失效，开始删除存储的数据
                                MessageExpire(pushInfoBean, weatherInfoBean);
                                return false;
                            }
                        }else{//否则推送的数据的状态异常，不显示
                            Log.i(TAG, "shwoData:test this push info status is stop,don't show");
                            return false;
                        }
                    }
                });
        DisposableObserver<Long> showDataObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {//显示数据
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
                PushInfoUtil.uploadPushLog(pushInfoBean, context);//上传显示日志
                hideView(pushInfoBean);//根据显示时间定时，到时间则隐藏显示
                weatherInfoBean.setShow(true);//设置显示的数据为显示
                oldWeatherInfoBean.setShow(true);//设置老数据为显示
                if(1==pushInfoBean.getShowRules()){//如果当前的数据等于常显
                   disPlayWeatherInfoBean.setShow(true);//设置当前显示的数据为显示
                }
            }

            @Override
            public void onError(Throwable e) {//轮询出现异常
                Log.i(TAG, "shwoData OnError: ");
                e.printStackTrace();
                dispose();
            }

            @Override
            public void onComplete() {//轮询结束，取消订阅
                Log.i(TAG, "shwoData onComplete: ");
                dispose();
            }
        };
        weatherInfoBean.setShowDataObserver(showDataObserver);//设置当前数据的轮询间隔观察者
//        oldWeatherInfoBean.setShowDataObserver(showDataObserver);
//        if (1==weatherInfoBean.getPushInfoBean().getShowRules()){
//            disPlayWeatherInfoBean.setShowDataObserver(showDataObserver);
//        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherInfoBean.getShowDataObserver());//订阅
        //设置失效的轮询
        getExpireObservable(weatherInfoBean);
    }

    private boolean isShow(long time, PushInfoBean pushInfoBean, WeatherInfoBean weatherInfoBean) throws ParseException {
        long startTime = pushInfoBean.getStartTime();//获取开始时间
        Log.i(TAG, "isShow startTime: "+ TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,startTime));
        long endTime = pushInfoBean.getEndTime();//获取结束时间
        Log.i(TAG, "isShow endTime: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,endTime));
        if (0==startTime&&0==endTime){
            return true;
        }else if (startTime == endTime){
            return true;
        }else{
            SimpleDateFormat tf = new SimpleDateFormat(TimeUtil.dateFormatHMS);//格式化当前时间 设置成 小时
            Date currentTime = tf.parse(tf.format(time));
            long timeTime = currentTime.getTime();//获取当前时间
            Log.i(TAG, "isShow timeTime: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,timeTime));
            boolean effectiveDate = TimeUtil.isEffectiveDate(timeTime, startTime, endTime);//判断 当前时间 是否在 开始时间和结束时间内
            Log.i(TAG, "isShow: effectiveDate:"+effectiveDate);
            if(effectiveDate){ //如果是 ，则允许显示
                return true;
            }else{//否则则 不允许显示
                //获取当前显示的数据，判断是否需要关闭当前显示的内容
                getEffectiveDate(pushInfoBean,weatherInfoBean);
                return false;
            }
        }

    }

    /**
     * 获取当前显示的数据，判断是否需要关闭当前显示的内容
     * @param pushInfoBean
     * @param weatherInfoBean
     */
    private void getEffectiveDate(PushInfoBean pushInfoBean,WeatherInfoBean weatherInfoBean) {
        Log.i(TAG, "getEffectiveDate: ");
        try {
            Log.i(TAG, "getEffectiveDate: pushInfoBean:"+pushInfoBean.toString());
            if(disPlayWeatherInfoBean!=null&&disPlayWeatherInfoBean.getPushInfoBean()!=null){//如果当前显示的数据不为空
                Log.i(TAG, "getEffectiveDate: disPlayWeatherInfoBean:"+disPlayWeatherInfoBean.getPushInfoBean().toString());
                if (pushInfoBean.toString().equals(disPlayWeatherInfoBean.getPushInfoBean().toString())){//如果当前数据等于当前显示的数据
                    Log.i(TAG, "getEffectiveDate: pushInfoBean =  disPlayWeatherInfoBean");
                    if (disPlayWeatherInfoBean.isShow()){//如果当前显示的数据正在显示，隐藏显示内容
                        Log.i(TAG, "getEffectiveDate: disPlay message is show,hide this");
                        SendShowImage(false);
                        SendShowText(false);
                        disPlayWeatherInfoBean.setShow(false);
                        weatherInfoBean.setShow(false);
                    }else{
                        Log.i(TAG, "getEffectiveDate: not show");
                    }
                }else{
                    Log.i(TAG, "getEffectiveDate: pushInfoBean !=  disPlayWeatherInfoBean");
                }
            }
            if(oldWeatherInfoBean!=null&&oldWeatherInfoBean.getPushInfoBean()!=null){//如果老数据不为空
                Log.i(TAG, "getEffectiveDate: oldWeatherInfoBean:"+oldWeatherInfoBean.getPushInfoBean().toString());
                if (pushInfoBean.toString().equals(oldWeatherInfoBean.getPushInfoBean().toString())){//如果当前数据等于老数据
                    Log.i(TAG, "getEffectiveDate: pushInfoBean =  oldWeatherInfoBean");
                    if (oldWeatherInfoBean.isShow()){//如果老的数据正在显示，隐藏显示内容
                        Log.i(TAG, "getEffectiveDate: old message is show,hide this");
                        SendShowImage(false);
                        SendShowText(false);
                        oldWeatherInfoBean.setShow(false);
                        weatherInfoBean.setShow(false);
                    }else{
                        Log.i(TAG, "getEffectiveDate: not show");
                    }
                }else{
                    Log.i(TAG, "getEffectiveDate: pushInfoBean !=  oldWeatherInfoBean");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除 当前数据
     * @param weatherInfoBean
     */
    private void RemoveExpireMessage(WeatherInfoBean weatherInfoBean) {
        try {
            Log.i(TAG, "RemoveExpireMessage: get weatherList size = "+weatherList.size());
            Log.i(TAG, "RemoveExpireMessage: remove weatherInfo ");
            weatherList.remove(weatherInfoBean);
            Log.i(TAG, "RemoveExpireMessage: remove over get weatherList size = "+weatherList.size());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 轮询检测失效时间   失效轮询
     * @param weatherInfoBean
     */
    private void getExpireObservable(final WeatherInfoBean weatherInfoBean) {
        Log.i(TAG, "getExpireObservable: ");
        disposedExpireDataObservable(weatherInfoBean);//取消订阅当前数据的失效轮询
        final PushInfoBean pushInfoBean = weatherInfoBean.getPushInfoBean();
        DisposableObserver<Long> expirePbservable = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                Log.i(TAG, "getExpireObservable: onNext");
//                getEffectiveDate(pushInfoBean,weatherInfoBean);
                boolean isExpire = getExpire(pushInfoBean);//判断内容是否失效
                if (isExpire) {//如果失效则删除失效内容
                    Log.i(TAG, "test this push message is expire");
                    MessageExpire(pushInfoBean, weatherInfoBean);
                    onComplete();
                }else{
                    Log.i(TAG, "getExpireObservable: this push message not expire");
                    try {
                        long time = System.currentTimeMillis();//获取当前时间
                        long show_start_time = pushInfoBean.getShowStartTime();
                        Log.i(TAG, "getExpireObservable show_start_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,show_start_time));
                        long show_end_time = pushInfoBean.getShowEndTime();
                        Log.i(TAG, "getExpireObservable show_end_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,show_end_time));
                        if (0==show_start_time&&0==show_end_time){ //判断开始时间和结束时间是否都等于0 。等于0则通过
                            getEffectiveDate(pushInfoBean,weatherInfoBean);
                        }else if (show_start_time== show_end_time) { //判断开始时间是否等于结束时间，等于则通过
                            getEffectiveDate(pushInfoBean,weatherInfoBean);
                        }else{
                            boolean isShow =TimeUtil.isEffectiveDate(time,show_start_time,show_end_time);
                            Log.i(TAG, "getExpireObservable: isShow:"+isShow);
                            if (!isShow){//如果不在显示的时间段内
                                getEffectiveDate(pushInfoBean,weatherInfoBean);
                            }else{//否则在显示的时间内
                                if (pushInfoBean.getStartTime()==0&&pushInfoBean.getEndTime()==0){//判断开始时间和结束时间是否都等于0 。等于0则通过
                                    Log.i(TAG, "getExpireObservable: StartTime and EndTime == 0 ");
                                }else if (pushInfoBean.getStartTime() == pushInfoBean.getEndTime()){//判断开始时间是否等于结束时间，等于则通过
                                    Log.i(TAG, "getExpireObservable: StartTime == EndTime");
                                }else{
                                    long startTime = pushInfoBean.getStartTime();
                                    Log.i(TAG, "getExpireObservable startTime: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,startTime));
                                    long endTime = pushInfoBean.getEndTime();
                                    Log.i(TAG, "getExpireObservable endTime: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,endTime));
                                    SimpleDateFormat tf = new SimpleDateFormat(TimeUtil.dateFormatHMS);
                                    Date currentTime = tf.parse(tf.format(time));
                                    long timeTime = currentTime.getTime();
                                    Log.i(TAG, "getExpireObservable timeTime: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,timeTime));
                                    boolean effectiveDate = TimeUtil.isEffectiveDate(timeTime, startTime, endTime);
                                    Log.i(TAG, "getExpireObservable: effectiveDate:"+effectiveDate);
                                    if(!effectiveDate){
                                        getEffectiveDate(pushInfoBean,weatherInfoBean);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.i(TAG, "getExpireObservable onError:  get expire  has error");
                dispose();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "getExpireObservable onComplete: message expire");
                dispose();
            }
        };
        weatherInfoBean.setExpirePbservable(expirePbservable);
//        if (1==weatherInfoBean.getPushInfoBean().getShowRules()){
//            disPlayWeatherInfoBean.setExpirePbservable(expirePbservable);
//        }
        Observable.interval(1, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherInfoBean.getExpirePbservable());
    }

    /**
     * 消息内容已经失效，删除当前消息内容
     * @param pushInfoBean
     * @param weatherInfoBean
     */
    private void MessageExpire(PushInfoBean pushInfoBean, WeatherInfoBean weatherInfoBean) {
        Log.i(TAG, "MessageExpire: ");
        SendShowImage(false);//隐藏图片
        SendShowText(false);//隐藏文字
        pushInfoBean.setPushStatus("4");//设置推送状态为4 失效
        RemoveExpireMessage(weatherInfoBean);//删除存储在集合里面的数据
        PushInfoUtil.uploadPushLog(pushInfoBean, context);//上传失效日志
        PushInfoUtil.deletePushInfo(context, pushInfoBean.getGroupId());//删除推送数据
        weatherInfoBean.setShow(false);//设置当前数据为不显示
        disposedExpireDataObservable(weatherInfoBean);//取消 检测是否失效轮询
        disposedShowDataObservable(weatherInfoBean);//取消 显示间隔的轮询
        if (disPlayWeatherInfoBean.getPushInfoBean() != null && pushInfoBean.toString().equals(disPlayWeatherInfoBean.getPushInfoBean().toString())) {//判断失效的数据是否和当前的数据一致
            Log.i(TAG, "onNext: this message and disPlay message equals ,set disPlay message is null");
            disPlayWeatherInfoBean.setPushInfoBean(null);//设置显示的数据为空
            disPlayWeatherInfoBean.setShow(false);//设置显示的数据不显示
        }
        if (oldWeatherInfoBean.getPushInfoBean() != null && pushInfoBean.toString().equals(oldWeatherInfoBean.getPushInfoBean().toString())) {//判断失效的数据是否和老的数据是否一致
            Log.i(TAG, " onNext: this message and old message equals ,set old message is null");
            oldWeatherInfoBean.setPushInfoBean(null);//设置老的数据为空
            oldWeatherInfoBean.setShow(false);//设置老的数据不显示
        }
        //发送 常显的消息
        SendShowLongTerm();
    }

    /**
     * 取消订阅根据间隔时间显示的观察者
     * @param weatherInfoBean
     */
    private void disposedShowDataObservable(WeatherInfoBean weatherInfoBean) {
        if (weatherInfoBean.getShowDataObserver()!=null){
            if (!weatherInfoBean.getShowDataObserver().isDisposed()){
                Log.i(TAG, "disposedShowDataObservable: ");
                weatherInfoBean.getShowDataObserver().dispose();
            }
        }
    }

    /**
     * 取消订阅 判断是否失效的观察者
     * @param weatherInfoBean
     */
    private void disposedExpireDataObservable(WeatherInfoBean weatherInfoBean) {
        if (weatherInfoBean.getExpirePbservable()!=null){
            if (!weatherInfoBean.getExpirePbservable().isDisposed()){
                Log.i(TAG, "disposedExpireDataObservable: ");
                weatherInfoBean.getExpirePbservable().dispose();
            }
        }
    }

    /**
     *  发送常显的消息
     */
    private void SendShowLongTerm() {
        Log.i(TAG, "SendShowLongTerm: ");
       handler.sendEmptyMessage(0x1890);
    }
    private boolean getExpire(PushInfoBean pushInfoBean){
       boolean isExpire = false;
       try {
           long time = System.currentTimeMillis();
           long expire_time = pushInfoBean.getExpireTime();
           Log.i(TAG, "getExpire "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,time));
           Log.i(TAG, "getExpire: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,expire_time));
           isExpire = TimeUtil.isExpire(time, expire_time);
       }catch (Exception e){
           e.printStackTrace();
       }
         return isExpire;
    }
    DisposableObserver<Long> disposableObserver;

    /**
     * 根据显示时间后隐藏显示的内容
     * @param pushInfoBean
     */
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
                if (oldWeatherInfoBean!=null&&oldWeatherInfoBean.getPushInfoBean()!=null){
                        Log.i(TAG, "onNext: set old info  not show");
                        oldWeatherInfoBean.setShow(false);
                }
                if (disPlayWeatherInfoBean!=null&&disPlayWeatherInfoBean.getPushInfoBean()!=null){
                    if (!pushInfoBean.toString().equals(disPlayWeatherInfoBean.getPushInfoBean().toString())){
                        Log.i(TAG, "onNext: set display info  not show");
                        disPlayWeatherInfoBean.setShow(false);
                    }
                }
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
