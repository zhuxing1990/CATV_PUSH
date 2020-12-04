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
import com.vunke.catv_push.db.PushTable;
import com.vunke.catv_push.modle.PushInfoBean;
import com.vunke.catv_push.ui.PushWebActivity;
import com.vunke.catv_push.web.WebViewUtil;

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
//    public void updateInfo(PushInfoBean pushInfoBean){
//        this.pushInfoBean = pushInfoBean;
//    }
    private FrameLayout frameLayout;
    public void showView(final PushInfoBean pushInfoBean){
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
}
