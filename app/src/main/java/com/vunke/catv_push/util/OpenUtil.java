package com.vunke.catv_push.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class OpenUtil {
    private static final String TAG = "OpenUtil";
    /**
     * 判断应用是否安装
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isPkgInstalled(Context context, String packageName) {
        Log.i(TAG, "isPkgInstalled: getPackageName:"+packageName);
        if (TextUtils.isEmpty(packageName)) {
            Log.i(TAG, "isPkgInstalled: get packageName is null");
            return false;
        }
        ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }
    /**
     * 启动应用
     */
    public static void StartAPP2(String packageName,String ClassName,String  key,String value,Context context){
        Log.i(TAG, "StartAPP: packageName:"+packageName+"\t ClassName:"+ClassName+"\t key:"+key+"\t value:"+value);
        if (TextUtils.isEmpty(packageName)&&TextUtils.isEmpty(ClassName)){
            Log.e(TAG,"StartAPP: start app failed ,get appinfo error");
            Toast.makeText(context, "启动应用失败：获取应用失败", Toast.LENGTH_SHORT).show();
        }else  if (!TextUtils.isEmpty(packageName)&&TextUtils.isEmpty(ClassName)){
            Log.e(TAG, "StartAPP: get class is null,startApk");
            if (isPkgInstalled(context,packageName)){
                Log.i(TAG, "StartAPP: app is installed,startApk");
                StartAPK2(packageName,key,value,context);
            }else{
                Log.e(TAG, "StartAPP: app not is installed");
                Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
            }
        }else if (TextUtils.isEmpty(packageName)&&!TextUtils.isEmpty(ClassName)){
            Log.e(TAG, "StartAPP: get packageName is null,start locat Activity");
            StartLocatActivity2(ClassName,key,value,context);
        }else  if (!TextUtils.isEmpty(packageName)&&!TextUtils.isEmpty(ClassName)){
            if (isPkgInstalled(context,packageName)){
                Log.e(TAG, "StartAPP: app is installed,startApk");
                StartActivity2(packageName,ClassName,key,value,context);
            }else{
                Log.e(TAG, "StartAPP: app not is installed2");
                Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 启动应用
     */
    public static void StartAPP2(String packageName,String ClassName,String[]  key,String[] value,Context context){
        Log.i(TAG, "StartAPP: packageName:"+packageName+"\t ClassName:"+ClassName+"\t key:"+key+"\t value:"+value);
        if (TextUtils.isEmpty(packageName)&&TextUtils.isEmpty(ClassName)){
            Log.e(TAG,"StartAPP: start app failed ,get appinfo error");
            Toast.makeText(context, "启动应用失败：获取应用失败", Toast.LENGTH_SHORT).show();
        }else  if (!TextUtils.isEmpty(packageName)&&TextUtils.isEmpty(ClassName)){
            Log.e(TAG, "StartAPP: get class is null,startApk");
            if (isPkgInstalled(context,packageName)){
                Log.i(TAG, "StartAPP: app is installed,startApk");
                StartAPK2(packageName,key,value,context);
            }else{
                Log.e(TAG, "StartAPP: app not is installed");
                Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
            }
        }else if (TextUtils.isEmpty(packageName)&&!TextUtils.isEmpty(ClassName)){
            Log.e(TAG, "StartAPP: get packageName is null,start locat Activity");
            StartLocatActivity2(ClassName,key,value,context);
        }else  if (!TextUtils.isEmpty(packageName)&&!TextUtils.isEmpty(ClassName)){
            if (isPkgInstalled(context,packageName)){
                Log.e(TAG, "StartAPP: app is installed,startApk");
                StartActivity2(packageName,ClassName,key,value,context);
            }else{
                Log.e(TAG, "StartAPP: app not is installed2");
                Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 启动本地Activity
     * @param ClassName
     * @param context
     */
    public static void StartLocatActivity2(String ClassName,String[] key,String[] value,Context context){
        if (TextUtils.isEmpty(ClassName)){
            Log.e(TAG, "className is null");
            Toast.makeText(context, "启动本地页面失败", Toast.LENGTH_SHORT).show();
            return ;
        }
        if (ClassName.contains(context.getPackageName())) {
            Intent intent = new Intent();
            intent.setClassName(context, ClassName);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PutAllExtra(intent,key,value);
                context.startActivity(intent);
            }
        }else{
            Toast.makeText(context, "启动失败，无法启动该应用", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 启动本地Activity
     * @param ClassName
     * @param context
     */
    public static void StartLocatActivity2(String ClassName,String key,String value,Context context){
        if (TextUtils.isEmpty(ClassName)){
            Log.e(TAG, "className is null");
            Toast.makeText(context, "启动本地页面失败", Toast.LENGTH_SHORT).show();
            return ;
        }
        if (ClassName.contains(context.getPackageName())) {
            Intent intent = new Intent();
            intent.setClassName(context, ClassName);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PutAllExtra(intent,key,value);
                context.startActivity(intent);
            }
        }else{
            Toast.makeText(context, "启动失败，无法启动该应用", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 根据包名和类名启动APK
     *
     * @param packageName
     * @param ClassName
     * @param context
     */
    public static void StartActivity2(String packageName,String ClassName,String[] key,String[] value, Context context) {
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "packageName is null");
            Toast.makeText(context, "启动失败", Toast.LENGTH_SHORT).show();
            return ;
        }
        if (TextUtils.isEmpty(ClassName)){
            Log.e(TAG, "className is null");
            StartAPK2(packageName,key,value,context);
            return ;
        }
        Log.i(TAG, "StartActivity: get packageName;"+packageName);
        Log.i(TAG, "StartActivity: get className;"+ClassName);
        Intent intent = new Intent();
        intent.setClassName(packageName, ClassName);
//        方法一：
//        if (context.getPackageManager().resolveActivity(intent, 0) == null) {
//            // 说明系统中不存在这个activity
//        }
//        方法二：
        if(intent.resolveActivity(context.getPackageManager()) != null) {
            // 说明系统中不存在这个activity
            Log.i(TAG, "StartActivity: get Activity success");
            intent= new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(packageName, ClassName);
            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PutAllExtra(intent,key,value);
            context.startActivity(intent);
        }else{
            Toast.makeText(context, "启动失败,获取本地页面失败", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "StartActivity: startActivity error ,get Activity failed");
        }
//        方法三：
//        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
//        if (list.size() == 0) {
//            // 说明系统中不存在这个activity
//        }
    }
    /**
     * 根据包名启动APK
     *
     * @param packageName
     * @param context
     */
    public static void StartAPK2(String packageName, String[] key,String[] value,Context context) {
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "packageName is null");
            return;
        }
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List apps = pManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo ri = (ResolveInfo) apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                Log.i(TAG, "start package:"+packageName+",start package launcher:"+ className);
                Intent intent= new Intent(Intent.ACTION_MAIN);
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PutAllExtra(intent,key,value);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 根据包名启动APK
     *
     * @param packageName
     * @param context
     */
    public static void StartAPK2(String packageName, String key,String value,Context context) {
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "packageName is null");
            return;
        }
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List apps = pManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo ri = (ResolveInfo) apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                Log.i(TAG, "start package:"+packageName+",start package launcher:"+ className);
                Intent intent= new Intent(Intent.ACTION_MAIN);
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PutAllExtra(intent,key,value);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 根据包名和类名启动APK
     *
     * @param packageName
     * @param ClassName
     * @param context
     */
    public static void StartActivity2(String packageName,String ClassName,String key,String value, Context context) {
        if (TextUtils.isEmpty(packageName)) {
            Log.e(TAG, "packageName is null");
            Toast.makeText(context, "启动失败", Toast.LENGTH_SHORT).show();
            return ;
        }
        if (TextUtils.isEmpty(ClassName)){
            Log.e(TAG, "className is null");
            StartAPK2(packageName,key,value,context);
            return ;
        }
        Log.i(TAG, "StartActivity: get packageName;"+packageName);
        Log.i(TAG, "StartActivity: get className;"+ClassName);
        Intent intent = new Intent();
        intent.setClassName(packageName, ClassName);
//        方法一：
//        if (context.getPackageManager().resolveActivity(intent, 0) == null) {
//            // 说明系统中不存在这个activity
//        }
//        方法二：
        if(intent.resolveActivity(context.getPackageManager()) != null) {
            // 说明系统中不存在这个activity
            Log.i(TAG, "StartActivity: get Activity success");
            intent= new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(packageName, ClassName);
            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PutAllExtra(intent,key,value);
            context.startActivity(intent);
        }else{
            Toast.makeText(context, "启动失败,获取本地页面失败", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "StartActivity: startActivity error ,get Activity failed");
        }
//        方法三：
//        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
//        if (list.size() == 0) {
//            // 说明系统中不存在这个activity
//        }
    }

    public static void PutAllExtra(Intent intent, String[] key, String[] value){
        try {
            if (key==null||value==null){
                Log.i(TAG, "PutAllExtra: key 或者 value 为空");
                return;
            }
            if (key.length!=0&&value.length!=0) {
                for (int i = 0; i < key.length; i++) {
                    intent.putExtra(key[i], value[i]);
//                    }
                }
            }else{
                Log.i(TAG, "PutAllExtra: get array key or array value is null");
            }
        }catch (Exception e){
            Log.e(TAG, "PutAllExtra key value 长度不一致 ",e);
        }
    }
    public static void PutAllExtra(Intent intent,String key,String value){
        try {
            if (TextUtils.isEmpty(key)||TextUtils.isEmpty(value)){
                Log.i(TAG, "PutAllExtra:  get key or value is null");
                return;
            }
            intent.putExtra(key, value);
        }catch (Exception e){
            Log.e(TAG, "PutAllExtra key value 长度不一致 ",e);
        }
    }
    /**
     * 根据包名启动APK
     *
     * @param packageName
     * @param className
     * @param context
     */
    public static void StartAPP(String packageName,String className,String jsonData, Context context) {
        if (TextUtils.isEmpty(packageName)) {
            Log.i(TAG, "包名为空");
//            packageName = DefaultPackage;
        }
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List apps = pManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo ri = (ResolveInfo) apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                if (TextUtils.isEmpty(className)){
                    className = ri.activityInfo.name;
                }
                Intent intent = new Intent(Intent.ACTION_MAIN);
                ComponentName cn = new ComponentName(packageName, className);
                if (!TextUtils.isEmpty(jsonData)){
                    intent.putExtra("jsonData",jsonData);
                }
                intent.setComponent(cn);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
