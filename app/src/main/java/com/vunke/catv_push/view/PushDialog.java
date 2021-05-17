package com.vunke.catv_push.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.vunke.catv_push.R;
import com.vunke.catv_push.base.BaseConfig;
import com.vunke.catv_push.db.PushTable;
import com.vunke.catv_push.modle.PushInfoBean;
import com.vunke.catv_push.modle.PushInfoListBean;
import com.vunke.catv_push.ui.PushWebActivity;
import com.vunke.catv_push.util.PushInfoUtil;
import com.vunke.catv_push.util.SharedPreferencesUtil;
import com.vunke.catv_push.util.TimeUtil;
import com.vunke.catv_push.web.WebViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuxi on 2020/8/8.
 */

public class PushDialog implements View.OnKeyListener{
    private static final String TAG = "PushDialog";
    private static PushDialog pushDialog;
    private PushInfoBean pushInfoBean;

    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private LayoutInflater inflater;
    private String channelId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public static PushDialog getInstance(Context context){
        if (pushDialog==null){
            pushDialog = new PushDialog(context);
        }
        return pushDialog;
    }

    public PushDialog(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
        windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.gravity = Gravity.LEFT| Gravity.TOP;
        // 设置图片格式，效果为背景透明
        layoutParams.format = PixelFormat.RGBA_8888;
    }

    private FrameLayout frameLayout;
    private List<PushInfoListBean> pushList = new ArrayList<>();

    public void addPushInfo(PushInfoBean pushInfoBean){
        Log.i(TAG, "addPushInfo: ");
        if (pushList!=null&&pushList.size()!=0){
            boolean hasPush = hasPushInfo(pushList,pushInfoBean);
            if (hasPush){
                Log.i(TAG, "addPushInfo: has push ,don't add push");
            }else{
                Log.i(TAG, "addPushInfo: not push info ,add push");
                initData(pushInfoBean);
            }
        }else{
            Log.i(TAG, "addPushInfo: list is null");
            initData(pushInfoBean);
        }
    }

    private void initData(PushInfoBean pushInfoBean) {
        Log.i(TAG, "initData: ");
        String showScene = pushInfoBean.getShowScene();
        if (TextUtils.isEmpty(showScene)){
            Log.i(TAG, "initData: is default push");
            showView(pushInfoBean);
            PushInfoUtil.deletePushInfo(context,pushInfoBean.getGroupId());
        }else {
            Log.i(TAG, "initData: is channel push");
            if ("0".equals(showScene)){
                Log.i(TAG, "initData: is all channel push");
                if (!TextUtils.isEmpty(channelId)) {
                    Log.i(TAG, "initData: has channel play");
                    String isPush = SharedPreferencesUtil.getStringValue(context,PushTable.GROUP_ID+pushInfoBean.getGroupId(),"");
                    if (TextUtils.isEmpty(isPush)){
                        Log.i(TAG, "initData: is push is null");
                        showView(pushInfoBean);
                        long intervalTime = pushInfoBean.getIntervalTime();
                        if (intervalTime==0){
                            Log.i(TAG, "initData: save");
                            SharedPreferencesUtil.setStringValue(context,PushTable.GROUP_ID+pushInfoBean.getGroupId(),"true");
                        }
                    }else{
                        Log.i(TAG, "initData: isPush ,not show");
                    }
                }else{
                    Log.i(TAG, "initData: get sp channel");
                    String channel_id = SharedPreferencesUtil.getStringValue(context, BaseConfig.CHANNEL_ID, null);
                    Log.i(TAG, "initData: channel_id:"+channel_id);
                    if (!TextUtils.isEmpty(channel_id)){
                        Log.i(TAG, "initData: has channel play2");
                            showView(pushInfoBean);
                        long intervalTime = pushInfoBean.getIntervalTime();
                        if (intervalTime==0){
                            Log.i(TAG, "initData: save");
                            SharedPreferencesUtil.setStringValue(context,PushTable.GROUP_ID+pushInfoBean.getGroupId(),"true");
                        }
                    }
                }
            }else if("1".equals(showScene)){
                Log.i(TAG, "initData: no't all channel push");
                String dvb = pushInfoBean.getDvbChannelIds();
                String ip = pushInfoBean.getIpChannelIds();
                String channel_id = SharedPreferencesUtil.getStringValue(context, BaseConfig.CHANNEL_ID, null);
                if (!TextUtils.isEmpty(channelId)){
                    Log.i(TAG, "initData: has channel play");
                    if (channelId.equals(dvb)) {
                        Log.i(TAG, "initData: is dvb channel");
                        String isPush = SharedPreferencesUtil.getStringValue(context,PushTable.GROUP_ID+pushInfoBean.getGroupId(),"");
                        if (TextUtils.isEmpty(isPush)){
                            Log.i(TAG, "initData: is push is null");
                            showView(pushInfoBean);
                            long intervalTime = pushInfoBean.getIntervalTime();
                            if (intervalTime==0){
                                Log.i(TAG, "initData: save");
                                SharedPreferencesUtil.setStringValue(context,PushTable.GROUP_ID+pushInfoBean.getGroupId(),"true");
                            }
                        }else{
                            Log.i(TAG, "initData: isPush ,not show");
                        }
                    } else if (channelId.equals(ip)) {
                        Log.i(TAG, "initData: is ip channel");
                        String isPush = SharedPreferencesUtil.getStringValue(context,PushTable.GROUP_ID+pushInfoBean.getGroupId(),"");
                        if (TextUtils.isEmpty(isPush)){
                            Log.i(TAG, "initData: is push is null");
                            showView(pushInfoBean);
                            long intervalTime = pushInfoBean.getIntervalTime();
                            if (intervalTime==0){
                                Log.i(TAG, "initData: save");
                                SharedPreferencesUtil.setStringValue(context,PushTable.GROUP_ID+pushInfoBean.getGroupId(),"true");
                            }
                        }
                    }
                }else if (!TextUtils.isEmpty(channel_id)) {
                    Log.i(TAG, "initData: has sp");
                    if (channel_id.equals(dvb)) {
                        Log.i(TAG, "initData: is dvb channel");
                        String isPush = SharedPreferencesUtil.getStringValue(context,PushTable.GROUP_ID+pushInfoBean.getGroupId(),"");
                        if (TextUtils.isEmpty(isPush)){
                            Log.i(TAG, "initData: is push is null");
                            showView(pushInfoBean);
                            long intervalTime = pushInfoBean.getIntervalTime();
                            if (intervalTime==0){
                                Log.i(TAG, "initData: save");
                                SharedPreferencesUtil.setStringValue(context,PushTable.GROUP_ID+pushInfoBean.getGroupId(),"true");
                            }
                        }else{
                            Log.i(TAG, "initData: isPush ,not show");
                        }
                    } else if (channel_id.equals(ip)) {
                        Log.i(TAG, "initData: is ip channel");
                        String isPush = SharedPreferencesUtil.getStringValue(context,PushTable.GROUP_ID+pushInfoBean.getGroupId(),"");
                        if (TextUtils.isEmpty(isPush)){
                            Log.i(TAG, "initData: is push is null");
                            showView(pushInfoBean);
                            long intervalTime = pushInfoBean.getIntervalTime();
                            if (intervalTime==0){
                                Log.i(TAG, "initData: save");
                                SharedPreferencesUtil.setStringValue(context,PushTable.GROUP_ID+pushInfoBean.getGroupId(),"true");
                            }
                        }else{
                            Log.i(TAG, "initData: isPush ,not show");
                        }
                    }
                } else {
                    Log.i(TAG, "addPushInfo: get channel_id is null,don't show");
                }
            }
        }
        intervalPush(pushInfoBean);
    }



    private void intervalPush(final PushInfoBean pushInfoBean) {
        Log.i(TAG, "intervalPush: ");
        PushInfoListBean pushInfoListBean = new PushInfoListBean();
        long intervalTime = pushInfoBean.getIntervalTime();
        Log.i(TAG, "intervalPush: intervalTime:"+intervalTime);
        if (intervalTime!=0){
            Observable<Long> observable = Observable.interval(intervalTime,intervalTime,TimeUnit.MINUTES)//------------正式  根据间隔时间，每1分钟轮询1次
//        Observable<Long> observable = Observable.interval(0,intervalTime,TimeUnit.SECONDS)//------------测试
                    .filter(new Predicate<Long>() { //每1分钟过滤一次数据
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
                                        return PushInfoUtil.isShow(time, pushInfoBean);
                                    }else if (show_start_time == show_end_time) { //判断开始时间是否等于结束时间，等于则通过
                                        return PushInfoUtil.isShow(time, pushInfoBean);
                                    } else{
                                        boolean isShow =TimeUtil.isEffectiveDate(time,show_start_time,show_end_time);//判断是否在显示的时间段内
                                        Log.i(TAG, "shwoData:test isShow:"+isShow);
                                        if (isShow){ //如果在显示的时间段内
                                            if (pushInfoBean.getStartTime()==0&&pushInfoBean.getEndTime()==0){ //判断开始时间和结束时间是否都等于0 。等于0则通过
                                                return true;
                                            }else if (pushInfoBean.getStartTime() == pushInfoBean.getEndTime()) { //判断开始时间是否等于结束时间，等于则通过
                                                return true;
                                            } else{
                                                return PushInfoUtil.isShow(time, pushInfoBean);
                                            }
                                        }else{
                                            return false;
                                        }
                                    }
                                }else{//否则当前数据失效
                                    Log.i(TAG, "test this push message is expire");
                                    //发现当前数据已经失效，开始删除存储的数据
                                    MessageExpire(pushInfoBean);
                                    dismiss();
                                    removePushInfo(pushInfoBean);
                                    return false;
                                }
                            }else{//否则推送的数据的状态异常，不显示
                                Log.i(TAG, "shwoData:test this push info status is stop,don't show");
                                return false;
                            }
                        }
                    });
            DisposableObserver<Long> timeObservable = new DisposableObserver<Long>() {
                @Override
                public void onNext(Long aLong) {
                    Log.i(TAG, "intervalPush onNext: ");
                    String showScene = pushInfoBean.getShowScene();
                    if (TextUtils.isEmpty(showScene)){
                        Log.i(TAG, "intervalPush: is default push");
                            showView(pushInfoBean);
                    }else {
                        Log.i(TAG, "intervalPush: is channel push");
                        if ("0".equals(showScene)){
                            Log.i(TAG, "intervalPush: is all channel push");
                            if (!TextUtils.isEmpty(channelId)) {
                                Log.i(TAG, "initData: has channel play");
                                    showView(pushInfoBean);
                            }else{
                                Log.i(TAG, "intervalPush: get sp channel");
                                String channel_id = SharedPreferencesUtil.getStringValue(context, BaseConfig.CHANNEL_ID, null);
                                Log.i(TAG, "intervalPush: channel_id:"+channel_id);
                                if (!TextUtils.isEmpty(channel_id)){
                                    Log.i(TAG, "initData: has channel play2");
                                    showView(pushInfoBean);
                                }
                            }
                        }else if("1".equals(showScene)){
                            Log.i(TAG, "intervalPush: no't all channel push");
                            String dvb = pushInfoBean.getDvbChannelIds();
                            String ip = pushInfoBean.getIpChannelIds();
                            String channel_id = SharedPreferencesUtil.getStringValue(context, BaseConfig.CHANNEL_ID, null);
                            if (!TextUtils.isEmpty(channelId)){
                                Log.i(TAG, "intervalPush: has channel play");
                                if (channelId.equals(dvb)) {
                                    Log.i(TAG, "intervalPush: is dvb channel");
                                        showView(pushInfoBean);
                                } else if (channelId.equals(ip)) {
                                    Log.i(TAG, "intervalPush: is ip channel");
                                        showView(pushInfoBean);
                                }
                            }else if (!TextUtils.isEmpty(channel_id)) {
                                Log.i(TAG, "intervalPush: has sp");
                                if (channel_id.equals(dvb)) {
                                    Log.i(TAG, "intervalPush: is dvb channel");
                                        showView(pushInfoBean);
                                } else if (channel_id.equals(ip)) {
                                    Log.i(TAG, "intervalPush: is ip channel");
                                        showView(pushInfoBean);
                                }
                            } else {
                                Log.i(TAG, "intervalPush: get channel_id is null,don't show");
                            }
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "onError: ",e );
                    dispose();
                }

                @Override
                public void onComplete() {
                    Log.i(TAG, "onComplete: ");
                    dispose();
                }
            };
            pushInfoListBean.setTimeObservable(timeObservable);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pushInfoListBean.getTimeObservable());//订阅
        }else{
            Log.i(TAG, "intervalPush: not  interval ");
        }
        pushInfoListBean.setPushInfoBean(pushInfoBean);
        pushList.add(pushInfoListBean);
    }
    /**
     * 消息内容已经失效，删除当前消息内容
     * @param pushInfoBean
     */
    private void MessageExpire(PushInfoBean pushInfoBean) {
        Log.i(TAG, "MessageExpire: ");
        pushInfoBean.setPushStatus("4");//设置推送状态为4 失效
        removePushInfo(pushInfoBean);
        PushInfoUtil.uploadPushLog(pushInfoBean, context);//上传失效日志
        PushInfoUtil.deletePushInfo(context, pushInfoBean.getGroupId());//删除推送数据
    }

    public void removePushInfo(PushInfoBean pushInfoBean){
        if (pushList!=null&&pushList.size()!=0){
            for (int i = 0; i < pushList.size(); i++) {
                PushInfoBean bean = pushList.get(i).getPushInfoBean();
                if (bean!=null){
                    String groupId = pushInfoBean.getGroupId();
                    String old_groupId = bean.getGroupId();
                    if (groupId.equals(old_groupId)){
                        Log.i(TAG, "removePushInfo: has push info ,remove push");
                        pushList.remove(bean);
                        DisposableObserver<Long> timeObservable = pushList.get(i).getTimeObservable();
                        if (timeObservable!=null){
                            if (!timeObservable.isDisposed()){
                                timeObservable.dispose();
                            }
                        }
                    }else{
                        Log.i(TAG, "removePushInfo:  not push");
                    }
                }
            }
        }
        if (this.pushInfoBean!=null&&pushInfoBean.equals(this.pushInfoBean)){
            Log.i(TAG, "RemovePush: dismiss dialog");
            dismiss();
        }
    }


    private boolean hasPushInfo( List<PushInfoListBean> pushList,PushInfoBean pushInfoBean) {
        boolean hasList = false;
        for (int i = 0; i < pushList.size(); i++) {
            PushInfoListBean pushInfoListBean = pushList.get(i);
            PushInfoBean bean = pushInfoListBean.getPushInfoBean();
            if (pushInfoBean.equals(bean)){
                hasList = true;
            }
            String old_groupId = bean.getGroupId();
            String groupId = pushInfoBean.getGroupId();
            if (groupId.equals(old_groupId)){
                Log.i(TAG, "hasPushInfo: has old_groupId ");
                DisposableObserver<Long> timeObservable = pushInfoListBean.getTimeObservable();
                if (timeObservable!=null&&!timeObservable.isDisposed()){
                    Log.i(TAG, "hasPushInfo: disposed timeObservable");
                    timeObservable.dispose();
                }
                Log.i(TAG, "hasPushInfo: delete pushInfoListBean");
                pushList.remove(pushInfoListBean);
            }
        }
        return hasList;
    }

    public void showView(final PushInfoBean pushInfoBean){
        pushInfoBean.setPushStatus("3");
        PushInfoUtil.uploadPushLog(pushInfoBean,context);
        this.pushInfoBean = pushInfoBean;
        layoutParams.width = pushInfoBean.getWidth();
        layoutParams.height = pushInfoBean.getHeight();
        layoutParams.x = this.pushInfoBean.getMarginLeft();
        layoutParams.y =  this.pushInfoBean.getMarginTop();
        if (isShow){
            dismiss();
        }
        frameLayout = new FrameLayout(context);
        FrameLayout.LayoutParams fragmeLayoutParams = new FrameLayout.LayoutParams(pushInfoBean.getWidth(),pushInfoBean.getHeight());
        frameLayout.setLayoutParams(fragmeLayoutParams);
        String push_type = pushInfoBean.getPushType();
        if ("0".equals(push_type)){
            Log.i(TAG, "showView: 获取推送类型为文字");
            View view = inflater.inflate(R.layout.activity_textonfo, null);
            TextView push_text_title  = view.findViewById(R.id.push_text_title);
            TextView push_text_info  = view.findViewById(R.id.push_text_info);
            TextView push_text_time = view.findViewById(R.id.push_text_time);
            push_text_time.bringToFront();
            push_text_title.setText(pushInfoBean.getPushTitle());
            push_text_info.setText(pushInfoBean.getPushMessage());
            frameLayout.addView(view);
            frameLayout.setFocusable(true);
            frameLayout.setOnKeyListener(this);
            initTimeOut(push_text_time);
            windowManager.addView(frameLayout,layoutParams);
            isShow = true;
        }else if ("1".equals(push_type)){
            Log.i(TAG, "showView: 获取推送类型为网页");
            String push_url = pushInfoBean.getPushUrl();
            Intent intent = new Intent(context, PushWebActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("LoadUrl",push_url);
            intent.putExtra(PushTable.SHOW_TIMES,pushInfoBean.getShowTimes());
            intent.putExtra(PushTable.WIDTH,pushInfoBean.getWidth());
            intent.putExtra(PushTable.HEIGHT,pushInfoBean.getHeight());
            intent.putExtra(PushTable.MARGIN_TOP,pushInfoBean.getMarginTop());
            intent.putExtra(PushTable.MARGIN_LEFT,pushInfoBean.getMarginLeft());
            context.startActivity(intent);
        }else if("2".equals(push_type)){
            Log.i(TAG, "showView: 获取推送类型为视频");
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            String push_url = pushInfoBean.getPushUrl();
            View view = inflater.inflate(R.layout.activity_push_video,null);
            VideoView push_videoview = view.findViewById(R.id.push_videoview);
            TextView push_video_time = view.findViewById(R.id.push_video_time);
            push_video_time.bringToFront();
            push_videoview.setVideoPath(push_url);
            push_videoview.setFocusable(true);
            push_videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(false);
                    mp.start();
                }
            });
            frameLayout.addView(view);
            frameLayout.setFocusable(true);
            frameLayout.setOnKeyListener(this);
            initTimeOut(push_video_time);
            windowManager.addView(frameLayout,layoutParams);
            isShow = true;
        }else if ("3".equals(push_type)){
            Log.i(TAG, "showView: 获取推送类型为无法聚焦的网页");
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            final String push_url = pushInfoBean.getPushUrl();
            View view = inflater.inflate(R.layout.activity_pushweb2,null);
            WebView push_webview =view.findViewById(R.id.push_webview);
            final TextView push_web_time = view.findViewById(R.id.push_web_time);
            push_web_time.bringToFront();
            WebViewUtil.initView(push_webview);
            if (!TextUtils.isEmpty(push_url)){
                push_webview.loadUrl(push_url);
            }
            push_webview.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                    Log.i(TAG, "shouldOverrideUrlLoading: 监控界面正在加载的url为:"+s);
                    if (s.contains("action://closeWeb")){
                        dismiss();
                    }else{
                        webView.loadUrl(s);
                    }
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.i(TAG, "onPageFinished: 网页加载结束");
                    if (push_url.contains(url)){
                        long showTimes = pushInfoBean.getShowTimes();
                        if (showTimes!=-1){
                            initTimeOut(push_web_time);
                        }
                    }
                }
            });
            push_webview.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress < 100 ) {
//                    tvTaskProgress.setVisibility(View.VISIBLE);
                        view.setVisibility(View.GONE);
                    } else {
//                    if (MainTaskFragment.this.isVisible()) {
//                        tvTaskProgress.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
//                    }
                    }
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    if (!TextUtils.isEmpty(title)){
                        if (title.contains("404")||title.contains("500")||title.contains("Error")||title.contains("找不到网页")||title.contains("网页无法打开")){
                            Log.i(TAG, "onReceivedTitle: webview load url failed");
                            dismiss();
                        }
                    }
                }
            });
            frameLayout.addView(view);
            windowManager.addView(frameLayout,layoutParams);
            isShow = true;
        }

    }
   private DisposableObserver<Long> timeOutObserver;
    private void initTimeOut(final TextView textView) {
        Log.i(TAG, "initTimeOut: ");
        final long showTimes = pushInfoBean.getShowTimes();
        Log.i(TAG, "initTimeOut: showTimes"+showTimes);
        closeTimeOut();
        timeOutObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                if (aLong > 0) {
                    Log.i(TAG, "initTimeOut onNext: " + aLong);
                    textView.setText(aLong.toString());
                } else {
                    dismiss();
                    onComplete();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "initTimeOut onError: ");
                dispose();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "initTimeOut onComplete: ");
                dispose();
            }
        };
        Observable.interval(0,1, TimeUnit.SECONDS)
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return aLong <= showTimes;
                    }
                })
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return -(aLong-showTimes);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeOutObserver);
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
            closeTimeOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closeTimeOut() {
        if (timeOutObserver!=null){
            if (!timeOutObserver.isDisposed()){
                timeOutObserver.dispose();
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            dismiss();
            return true;
        }
        return false;
    }
    public void clearPush(){
        Log.i(TAG, "clearPush: ");
        try {
            if (pushList!=null&&pushList.size()!=0){
                for (int i = 0; i < pushList.size(); i++) {
                    DisposableObserver<Long> timeObservable = pushList.get(i).getTimeObservable();
                    if (timeObservable!=null&&!timeObservable.isDisposed()){
                        timeObservable.dispose();
                    }
                }
                pushList.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
