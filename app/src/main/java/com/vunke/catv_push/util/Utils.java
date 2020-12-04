package com.vunke.catv_push.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.vunke.catv_push.base.WhiteList;

public class Utils {
    private static final String TAG = "Utils";

    /**
     * 判断当前网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetConnected(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {

                NetworkInfo info = connectivity.getActiveNetworkInfo();

                if (info != null) {
                    boolean istrue = false;
                    istrue = istrue ? info.isConnected() : info.isAvailable();
                    return istrue;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * @param context
     * @return versionCode 版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            String pkName = context.getPackageName();
            versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return versionCode;
    }
    /**
     * @param context
     * @return versionName 版本名字
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            String pkName = context.getPackageName();
            versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
        return versionName;
    }
    /**
     * 获取版本信息
     * @param context
     * @return packageName+versionName+versionCode
     */
    public static String getVersionInfo(Context context){
        try {

            String pkName = context.getPackageName();

            String versionName = context.getPackageManager().getPackageInfo(

                    pkName, 0).versionName;

            int versionCode = context.getPackageManager()

                    .getPackageInfo(pkName, 0).versionCode;

            return pkName + "   " + versionName + "  " + versionCode;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isMangguoPlayer(Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        String  currentClassName =manager.getRunningTasks(1).get(0).topActivity.getClassName();
        Log.i(TAG, "isMangguoPlayer: get top activity:"+currentClassName);
        if (currentClassName.contains(WhiteList.TOP_ACTIVITY_0)
                ||currentClassName.contains(WhiteList.TOP_ACTIVITY_1)
                ||currentClassName.contains(WhiteList.TOP_ACTIVITY_2)
                || currentClassName.contains(WhiteList.TOP_ACTIVITY_3)
        ) {
            Log.i(TAG, "isMangguoPlayer: mang guo apk is playing video ");
            return true;
        }
        else if(currentClassName.contains("com.vunke.catv_push")||currentClassName.contains("com.vunke.cath_auth")){
            Log.i(TAG, "isMangguoPlayer:  is push Activity or auth activity,not show");
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean isAuth(Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        String  currentClassName =manager.getRunningTasks(1).get(0).topActivity.getClassName();
        Log.i(TAG, "isAuth: get top activity:"+currentClassName);
        if(currentClassName.contains("com.vunke.catv_push")||currentClassName.contains("com.vunke.cath_auth")){
            Log.i(TAG, "isAuth:  is push Activity or auth activity,not show");
            return true;
        }
        else {
            return false;
        }
    }
}