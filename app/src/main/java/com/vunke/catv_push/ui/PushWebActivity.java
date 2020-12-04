package com.vunke.catv_push.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.vunke.catv_push.R;
import com.vunke.catv_push.modle.StartAppsBean;
import com.vunke.catv_push.util.DensityUtil;
import com.vunke.catv_push.util.OpenUtil;
import com.vunke.catv_push.web.JavaScriptObject;
import com.vunke.catv_push.web.WebViewUtil;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuxi on 2020/8/6.
 */

public class PushWebActivity extends AppCompatActivity {
    private static final String TAG = "PushWebActivity";
    private WebView push_webview;
    private TextView push_web_time;
    private FrameLayout push_web_fr;
    private long showTimes = 15L;
    private int width = -1,height = -1,marginTop=0,marginLeft=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushweb);
        initView();
        initData(getIntent());
    }
    @SuppressLint("JavascriptInterface")
    private void initView() {
        push_web_fr = findViewById(R.id.push_web_fr);
        push_webview = new WebView(this);
//        push_web_time = findViewById(R.id.push_web_time);
        WebViewUtil.initView(push_webview);
        push_webview.addJavascriptInterface(new JavaScriptObject(this, push_webview), "AppFunction");
        //该界面打开更多链接
        push_webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                Log.i(TAG, "shouldOverrideUrlLoading: 监控界面正在加载的url为:"+s);
                if (s.contains("action://closeWeb")){
                        finish();
                }else{
                    webView.loadUrl(s);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "onPageFinished: 网页加载结束");
                if (LoadUrl.contains(url)){
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
                        finish();
                    }
                }
            }
        });
        push_web_fr.addView(push_webview);
        initClostTextView();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData(intent);
    }
    private String LoadUrl;
    private void initData(Intent intent) {
        Log.i(TAG, "getAppIntent: ");
        if (intent.hasExtra("startApp")){
            String jsonData = intent.getStringExtra("startApp");
            Log.i(TAG, "getAppIntent: jsonData:"+jsonData);
            try {
                StartAppsBean startAppBean = new Gson().fromJson(jsonData, StartAppsBean.class);
                StartAppsBean.StartAppBean startApp= startAppBean.getStartApp();
                String s = "";
                if (startApp.getJsonData()!=null){
                    s = new Gson().toJson(startApp.getJsonData());
                }
                Log.i(TAG, "getAppIntent: jsonData:"+s);
                OpenUtil.StartAPP(startApp.getPackageName(),startApp.getClassName(),s,getApplicationContext());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (intent.hasExtra("LoadUrl")){
            LoadUrl = intent.getStringExtra("LoadUrl");
            Log.i(TAG, "getAppIntent: LoadUrl:"+LoadUrl);
            push_webview.loadUrl(LoadUrl);
        }
        if(intent.hasExtra("mangoJson")){
            String manggoJson = intent.getStringExtra("mangoJson");
            Log.i(TAG, "getAppIntent: get manggoJson:"+manggoJson);
            try {
                if(!TextUtils.isEmpty(manggoJson)){
                    JSONObject json = new JSONObject(manggoJson);
                    if(json.has("loadURL")){
                        LoadUrl = json.getString("loadURL");
//					LoadUrl = "http://124.232.135.225:8082/AppStoreTV4/service/branchPage/newPage/Sta_exchange/Exchange.jsp";
                        Log.i(TAG, "getAppIntent: LoadUrl:"+LoadUrl);
                        push_webview.loadUrl(LoadUrl);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(intent.hasExtra("jsonData")){
            String jsonData = intent.getStringExtra("jsonData");
            Log.i(TAG, "getAppIntent: get jsonData:"+jsonData);
            try {
                JSONObject json = new JSONObject(jsonData);
                if(json.has("loadURL")){
                    LoadUrl = json.getString("loadURL");
//					LoadUrl = "http://124.232.135.225:8082/AppStoreTV4/service/branchPage/newPage/Sta_exchange/Exchange.jsp";
                    Log.i(TAG, "getAppIntent: LoadUrl:"+LoadUrl);
                    push_webview.loadUrl(LoadUrl);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (intent.hasExtra("show_times")){
            showTimes = intent.getLongExtra("show_times",showTimes);
        }
        if (intent.hasExtra("width")){
            width = intent.getIntExtra("width",width);
        }
        if (intent.hasExtra("height")){
            height= intent.getIntExtra("height",height);
        }
        if (intent.hasExtra("marginTop")){
            marginTop= intent.getIntExtra("marginTop",marginTop);
        }
        if (intent.hasExtra("marginLeft")){
            marginLeft= intent.getIntExtra("marginLeft",marginLeft);
        }
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            Set<String> set = bundle.keySet();
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                String key = (String) iterator.next();
                Object value = bundle.get(key);
                Log.i("getAppIntent", "key:" + key + " value:" + value);
            }
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width,height);
        layoutParams.setMargins(marginLeft,marginTop,0,0);
        layoutParams.gravity  = Gravity.LEFT| Gravity.TOP;
        push_web_fr.setLayoutParams(layoutParams);
    }

    private void initClostTextView() {
        push_web_time = new TextView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
        layoutParams.setMargins(0, 10, 10, 0);
        push_web_time.setLayoutParams(layoutParams);
        push_web_time.setPadding(20,10,20,10);
        push_web_time.setTextSize(DensityUtil.px2sp(this, 28));
//            closeTimeText.setTextColor(Color.parseColor("#FF0000"));
        push_web_time.setTextColor(Color.parseColor("#FFFFFF"));
        push_web_time.setBackgroundColor(Color.parseColor("#40000000"));
        push_web_time.bringToFront();
        push_web_fr.addView(push_web_time);
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        push_webview.onResume();
        push_webview.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        push_webview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        push_webview.destroy();
        closeTimeOut();
    }

    public static final String NO_RESPONSE = "NoResponse";
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            goBack();
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && push_webview.canGoBack()) {
            Log.i(TAG, "onKeyDown: web.getUrl:"+push_webview.getUrl());

            if (push_webview.getUrl().contains(LoadUrl)) {//正式地址
                    finish();
                return super.onKeyDown(keyCode, event);
            }
            if(push_webview.getUrl().contains(NO_RESPONSE) ){
                return true;
            }
            push_webview.goBack();
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_BACK&&!push_webview.canGoBack()){
            finish();
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
//    public void exit() {
//        if (System.currentTimeMillis() - exitTime > 2000L) {
//            Toast.makeText(getApplicationContext(), "再按一次退出网页", Toast.LENGTH_SHORT).show();
//            exitTime = System.currentTimeMillis();
//            return;
//        }
//        finish();
//        System.exit(0);
//    }
//    private  long exitTime = 0;
    public void goBack(){
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                push_webview.evaluateJavascript("javascript:goBack()",new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }else{
                push_webview.loadUrl("javascript:goBack()");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

   private DisposableObserver<Long> timeOutObserver;
    private void closeTimeOut() {
        if (timeOutObserver!=null){
            if (!timeOutObserver.isDisposed()){
                timeOutObserver.dispose();
            }
        }
    }
    @JavascriptInterface
    public void initTimeOut(final TextView textView) {
        Log.i(TAG, "initTimeOut: ");
//        final long showTimes = pushInfoBean.getShowTimes();
        Log.i(TAG, "initTimeOut: showTimes"+showTimes);
        closeTimeOut();
        timeOutObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                if (aLong > 0) {
                    Log.i(TAG, "initTimeOut onNext: " + aLong);
                    textView.setText(aLong.toString());
                } else {
                    finish();
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
}
