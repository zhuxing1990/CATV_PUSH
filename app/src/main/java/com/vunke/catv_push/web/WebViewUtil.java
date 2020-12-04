package com.vunke.catv_push.web;

import android.graphics.Color;
import android.webkit.WebSettings;
import android.webkit.WebView;


/**
 * Created by zhuxi on 2020/8/8.
 */

public class WebViewUtil {
    private static final String TAG = "WebViewUtil";
    public static void initView(WebView webView) {
        webView.setBackgroundColor(Color.parseColor("#00000000"));
        WebSettings settings = webView.getSettings();
        settings.setBlockNetworkImage(false);//解决图片不显示
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setSupportZoom(true);
        settings.setDisplayZoomControls(true); //隐藏原生的缩放控件
        // 支持js
        // 设置字符编码
        settings.setDefaultTextEncodingName("GBK");
//        settings.setDefaultTextEncodingName("UTF-8");// 设置字符编码
        // 启用支持javascript
        settings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        settings.setBuiltInZoomControls(true);//设置内置的缩放控件。若为false，则该WebView不可缩放
        settings.setLightTouchEnabled(true);
        settings.setSupportZoom(true);//支持缩放，默认为true。是下面那个的前提。
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 不使用缓存，只从网络获取数据.
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // settings.setLoadWithOverviewMode(true);
        // 支持JS交互
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //监听网页的加载进度
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView webView, int i) {
//                if (i < 100 ) {
////                    tvTaskProgress.setVisibility(View.VISIBLE);
//                    webView.setVisibility(View.GONE);
//                } else {
////                    if (MainTaskFragment.this.isVisible()) {
////                        tvTaskProgress.setVisibility(View.GONE);
//                    webView.setVisibility(View.VISIBLE);
////                    }
//                }
//            }
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//                if (!TextUtils.isEmpty(title)){
//                    if (title.contains("404")||title.contains("500")||title.contains("Error")||title.contains("找不到网页")||title.contains("网页无法打开")){
//                        Log.i(TAG, "onReceivedTitle: webview load url failed");
//                    }
//                }
//            }
//        });

    }
}
