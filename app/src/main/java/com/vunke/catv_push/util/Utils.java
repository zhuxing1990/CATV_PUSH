package com.vunke.catv_push.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.vunke.catv_push.base.WhiteList;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.content.Context.USAGE_STATS_SERVICE;

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
     *
     * @param context
     * @return packageName+versionName+versionCode
     */
    public static String getVersionInfo(Context context) {
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

    public static boolean isEPGPlayer(Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
            if (runningTasks != null && runningTasks.size() != 0) {
                String currentClassName = runningTasks.get(0).topActivity.getClassName();
                Log.i(TAG, "isEPGPlayer: get top activity:" + currentClassName);
                if (currentClassName.contains(WhiteList.TOP_ACTIVITY_0)
                        || currentClassName.contains(WhiteList.TOP_ACTIVITY_1)
                        || currentClassName.contains(WhiteList.TOP_ACTIVITY_2)
                        || currentClassName.contains(WhiteList.TOP_ACTIVITY_3)
                ) {
                    Log.i(TAG, "isEPGPlayer: mang guo apk is playing video ");
                    return true;
                } else if (currentClassName.contains("com.vunke.catv_push") || currentClassName.contains("com.vunke.cath_auth")) {
                    Log.i(TAG, "isEPGPlayer:  is push Activity or auth activity,not show");
                    return true;
                } else {
                    return false;
                }
            } else {
                Log.i(TAG, "isEPGPlayer:runningTasks is null or  size is 0");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isAuth(Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
            if (runningTasks != null && runningTasks.size() != 0) {
                String currentClassName = runningTasks.get(0).topActivity.getClassName();
                Log.i(TAG, "isAuth: get top activity:" + currentClassName);
                if (currentClassName.contains("com.vunke.catv_push") || currentClassName.contains("com.vunke.cath_auth")) {
                    Log.i(TAG, "isAuth:  is push Activity or auth activity,not show");
                    return true;
                } else {
                    return false;
                }
            } else {
                Log.i(TAG, "isAuth:runningTasks is null or  size is 0");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static String getTopActivity(Context context){
        String topActivity = "";
        ActivityManager activityManager =(ActivityManager)context.getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList!=null){
            for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList){
                String processName = processInfo.processName;
                Log.i(TAG, "getTopActivity: processName"+processName);
                if (processInfo.importance ==ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    Log.i(TAG, "getTopActivity:"+processName+" is top activity");
                    topActivity = processName;
                }
            }
        }
        return topActivity;
    }
    @SuppressLint("NewApi")
    public static String getForegroundApp(Context context) {
        Log.i(TAG, "getForegroundApp: ");
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        long endt = calendar.getTimeInMillis();//结束时间
        calendar.add(Calendar.DAY_OF_MONTH, -1);//时间间隔为一个月
        long statt = calendar.getTimeInMillis();//开始时间
        UsageStatsManager usageStatsManager=(UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);
        //获取一个月内的信息
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY,statt,endt);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }
        UsageStats recentStats = null;
        for (UsageStats usageStats : queryUsageStats) {
            if(recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()){
                recentStats = usageStats;
            }
        }
        return recentStats.getPackageName();
    }

    @SuppressLint("NewApi")
    public static String getForegroundApp2(Context context) {
        Log.i(TAG, "getForegroundApp2: ");
        String topPackageName = "";
        long time = System.currentTimeMillis();
        UsageStatsManager usageStatsManager=(UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);
        //获取一个月内的信息
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY,time - 60*1000 * 3, time);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }
       try {
           if (queryUsageStats != null) {
               SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
               for (UsageStats usageStats : queryUsageStats) {
                   mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
               }
               if (mySortedMap != null && !mySortedMap.isEmpty()) {
                   topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
               }
           }
       }catch (Exception e){
           e.printStackTrace();
       }
        Log.i(TAG, "getForegroundApp2: "+topPackageName);
        return topPackageName;
    }
    public static List<Object> removeDuplicate(List<Object> list) {
        Set set = new LinkedHashSet<Object>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }
}