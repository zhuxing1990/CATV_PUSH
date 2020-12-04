package com.vunke.catv_push.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vunke.catv_push.manage.DevicesManager;
import com.vunke.catv_push.modle.AppInfoBean;
import com.vunke.catv_push.modle.StartAppsBean;
import com.vunke.catv_push.util.OpenUtil;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


public class JavaScriptObject {
    private static final String TAG = "JavaScriptObject";
    private Activity activity;
//    private Context context;
    private WebView webview;
    public JavaScriptObject(Activity activity, WebView webview) {
//    public JavaScriptObject(Context context, WebView webview) {
        this.activity = activity;
//        this.context = context;
        this.webview = webview;
    }
        @JavascriptInterface
        public void sendMessageToJAVA(String json) {
            Log.i("tag", "sendMessageToJAVA: json:"+json);
            if(TextUtils.isEmpty(json)||json.equals("undefined")){
                Log.i(TAG, "setAppData: get jsonData is null ro  is undefined");
                return ;
            }
            try {
                if (!TextUtils.isEmpty(json)){
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.has("startApp")){
                        StartAppsBean.StartAppBean startApp = GetStartAppBean(json);
                        String s = "";
                        if (startApp.getJsonData()!=null){
                            s = new Gson().toJson(startApp.getJsonData());
                        }
                        Log.i(TAG, "getAppIntent: jsonData:"+s);
                        OpenUtil.StartAPP(startApp.getPackageName(),startApp.getClassName(),s,activity);
//                        Utils.StartAPP(startApp.getPackageName(),startApp.getClassName(),s,context);
                    }else if(jsonObject.has("record_param")){
                        RecordParam = jsonObject.getString("record_param");
                        Log.i(TAG, "sendMessageToJAVA: 记录当前浏览器传参:"+ RecordParam);
                    }else if(jsonObject.has("toast")){
                        String toastData = jsonObject.getString("toast");
                        if (!TextUtils.isEmpty(toastData)){
                            Toast.makeText(activity, toastData, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(context, toastData, Toast.LENGTH_SHORT).show();
                        }else{
                            Log.i(TAG, "sendMessageToJAVA: 网页获取的参数为空");
                        }
                    }
                }
            }catch (Exception e){
                Log.i(TAG, "sendMessageToJAVA: 浏览器交互出现异常");
                e.printStackTrace();
            }

        }
    @JavascriptInterface
    public void sendMessageToJAVA(String json, String key, String value){
        if(TextUtils.isEmpty(json)||json.equals("undefined")){
            Log.i(TAG, "sendMessageToJAVA json key value: get jsonData is null ro  is undefined");
            return ;
        }
        Log.i(TAG, "sendMessageToJAVA2: json:"+json);
        try {
            if (!TextUtils.isEmpty(json)){
                JSONObject jsonObject = new JSONObject(json);
                if(jsonObject.has("startApp")){
                    StartAppsBean.StartAppBean startApp = GetStartAppBean(json);
                    OpenUtil.StartAPP2(startApp.getPackageName(),startApp.getClassName(),key,value,activity);
//                    Utils.StartAPP2(startApp.getPackageName(),startApp.getClassName(),key,value,context);
                }
            }
        }catch (Exception e){
            Log.i(TAG, "sendMessageToJAVA: 浏览器交互出现异常");
            e.printStackTrace();
        }
    }
        @JavascriptInterface
        public void sendMessageToJAVA2(String json, String[] key, String[] value){
            if(TextUtils.isEmpty(json)||json.equals("undefined")){
                Log.i(TAG, "sendMessageToJAVA json key value: get jsonData is null ro  is undefined");
                return ;
            }
            Log.i(TAG, "sendMessageToJAVA2: json:"+json);
            try {
                if (!TextUtils.isEmpty(json)){
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.has("startApp")){
                        StartAppsBean.StartAppBean startApp = GetStartAppBean(json);
                        OpenUtil.StartAPP2(startApp.getPackageName(),startApp.getClassName(),key,value,activity);
//                        Utils.StartAPP2(startApp.getPackageName(),startApp.getClassName(),key,value,context);
                    }
                }
            }catch (Exception e){
                Log.i(TAG, "sendMessageToJAVA: 浏览器交互出现异常");
                e.printStackTrace();
            }
        }

        private StartAppsBean.StartAppBean GetStartAppBean(String json) {
            StartAppsBean startAppBean = new Gson().fromJson(json, StartAppsBean.class);
            return startAppBean.getStartApp();
        }

        private String RecordParam = "";

        @JavascriptInterface
        public void getRecordParam(){
            Log.i(TAG, "getRecordParam: 浏览器获取记录的传参 RecordParam"+RecordParam);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webview.evaluateJavascript("javascript:getRecordParam(" + RecordParam + ")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }else{
                webview.loadUrl("javascript:getRecordParam(" + RecordParam + ")");
            }
        }
        @JavascriptInterface
        public String getAppData(String packageName){
            Log.i(TAG, "getAppData: packageName:"+packageName);
            String appData = "";
            if(TextUtils.isEmpty(packageName)||packageName.equals("undefined")){
                Log.i(TAG, "getAppData: get jsonData is null ro  is undefined");
                return appData;
            }
            try {
                PackageManager packageManager = activity.getPackageManager();
//                PackageManager packageManager = context.getPackageManager();
                final PackageInfo packageInfo = packageManager.getPackageInfo(packageName,0);
                if (packageInfo!=null){
                    String info = packageInfo.toString();
                    Log.i(TAG, "getAppData: info:"+info);

                            AppInfoBean appInfoBean = new AppInfoBean();
                            AppInfoBean.JsonDataBean  jsonDataBean = new AppInfoBean.JsonDataBean();

                            String versionName = packageInfo.versionName;
                            int versionCode = packageInfo.versionCode;
                            if (!TextUtils.isEmpty(versionName)){
                                jsonDataBean .setVersionName(packageInfo.versionName);
                            }
                           if (versionCode>0){
                               jsonDataBean.setVersionCode(packageInfo.versionCode);
                           }
                            jsonDataBean.setPackageName(packageInfo.packageName);
                            appInfoBean.setJsonData(jsonDataBean);
                            appData = new Gson().toJson(appInfoBean);
              }
            }catch (Exception e){
                e.printStackTrace();
            }
            return appData;
        }

        @JavascriptInterface
        public void LoadUrl(String url){
            Log.i(TAG, "LoadUrl: url:"+url);
            if (TextUtils.isEmpty(url)||url.equals("undefined")){
                Log.i(TAG, "LoadUrl: get url is null");
            }else{
                Observable.just(url)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                webview.loadUrl(s);
                            }
                        });
            }
        }
        @JavascriptInterface
      public void finishActivity(){
//            context.finish();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.finish();
                }
            });
      }
    @JavascriptInterface
    public String getDeviceInfo(Context mcontext){
        Log.i(TAG, "getDeviceInfo: ");
        String devicesInfo = "";
        try{
            JSONObject jsonObject = DevicesManager.setRequestParams(mcontext);
            devicesInfo = jsonObject.toString();
//            devicesInfo = new Gson().toJson(jsonObject);
        }catch (Exception e){
            e.printStackTrace();
        }
        return devicesInfo;
    }

    @JavascriptInterface
    public void sendBroadCast(String packageName, String action, String jsonData){
        try {
            Log.i(TAG, "sendBroadCast: action:"+action);
            Intent intent = new Intent();
            intent.setPackage(packageName);
            intent.setAction(action);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            if (!TextUtils.isEmpty(jsonData)){
                intent.putExtra("jsonData",jsonData);
            }
            activity.sendBroadcast(intent);
//            context.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void sendService(String packageName, String action, String jsonData){
        try {
            Log.i(TAG, "sendBroadCast: action:"+action);
            Intent intent = new Intent();
            intent.setPackage(packageName);
            intent.setAction(action);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            if (!TextUtils.isEmpty(jsonData)){
                intent.putExtra("jsonData",jsonData);
            }
            activity.startService(intent);
//            context.startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
//    public void playAudio(String audio_url){
//        try {
//            MediaPlayer mediaPlayer = new MediaPlayer();
//            mediaPlayer.reset();
//            // 设置声音效果
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            if (!TextUtils.isEmpty(audio_url)) {
//                mediaPlayer.setDataSource(activity, Uri.parse(audio_url));
//            } else {
//                Log.i(TAG, "setVideo: get audio_url is null");
//            }
//            Log.i(TAG, "initVideo: loading video");
//            mediaPlayer.setLooping(false);
//            mediaPlayer.prepareAsync();
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    if (mp != null) {
//                        mp.stop();
//                        mp.release();
//                        mp = null;
//                    }
//                }
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}