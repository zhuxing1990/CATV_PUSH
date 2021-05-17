package com.vunke.catv_push.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.vunke.catv_push.base.BaseConfig;
import com.vunke.catv_push.db.PushTable;
import com.vunke.catv_push.manage.PushManager;
import com.vunke.catv_push.modle.PushInfoBean;
import com.vunke.catv_push.modle.PushLog;
import com.vunke.catv_push.view.PushDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuxi on 2020/8/5.
 */

public class PushInfoUtil {
    private static final String TAG = "PushInfoUtil";
    public static void savePushInfo(Context context, PushInfoBean pushInfoBean){
        Log.i(TAG, "savePushInfo: ");
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PushTable.CREATE_TIMES,pushInfoBean.getCreateTimes());
            contentValues.put(PushTable.PUSH_ID,pushInfoBean.getPushId());
            contentValues.put(PushTable.PUSH_TITLE,pushInfoBean.getPushTitle());
            contentValues.put(PushTable.PUSH_NAME,pushInfoBean.getPushName());
            contentValues.put(PushTable.PUSH_TYPE,pushInfoBean.getPushType());
            contentValues.put(PushTable.PUSH_STATUS,pushInfoBean.getPushStatus());
            contentValues.put(PushTable.PUSH_LINK,pushInfoBean.getPushLink());
            contentValues.put(PushTable.PUSH_UTL,pushInfoBean.getPushUrl());
            contentValues.put(PushTable.EXPIRE_TIME,pushInfoBean.getExpireTime());
            contentValues.put(PushTable.SHOW_START_TIME,pushInfoBean.getShowStartTime());
            contentValues.put(PushTable.SHOW_END_TIME,pushInfoBean.getShowEndTime());
            contentValues.put(PushTable.SHOW_TIMES,pushInfoBean.getShowTimes());
            contentValues.put(PushTable.PUSH_MESSAGE,pushInfoBean.getPushMessage());
            contentValues.put(PushTable.WIDTH,pushInfoBean.getWidth());
            contentValues.put(PushTable.HEIGHT,pushInfoBean.getHeight());
            contentValues.put(PushTable.MARGIN_LEFT,pushInfoBean.getMarginLeft());
            contentValues.put(PushTable.MARGIN_TOP,pushInfoBean.getMarginTop());
            contentValues.put(PushTable.ACC_TIME,pushInfoBean.getAcc_time());
            contentValues.put(PushTable.TASK_ID,pushInfoBean.getTaskId());
            contentValues.put(PushTable.TOPIC_TYPE,pushInfoBean.getTopicType());
            contentValues.put(PushTable.DURATION,pushInfoBean.getDuration());
            contentValues.put(PushTable.GROUP_ID,pushInfoBean.getGroupId());
            contentValues.put(PushTable.INTERVAL_TIME,pushInfoBean.getIntervalTime());
            contentValues.put(PushTable.SHOW_RULES,pushInfoBean.getShowRules());
            contentValues.put(PushTable.SYSTEM_ID,pushInfoBean.getSystemId());
            contentValues.put(PushTable.START_TIME,pushInfoBean.getStartTime());
            contentValues.put(PushTable.END_TIME,pushInfoBean.getEndTime());
            contentValues.put(PushTable.DVB_CHANNEL_IDS,pushInfoBean.getDvbChannelIds());
            contentValues.put(PushTable.IP_CHANNEL_IDS,pushInfoBean.getIpChannelIds());
            contentValues.put(PushTable.SHOW_SCENE,pushInfoBean.getShowScene());
            Uri insert = context.getContentResolver().insert(Uri.parse(BaseConfig.PUSH_INFO_CONTENT), contentValues);
            Log.i(TAG, "savePushInfo: insert:"+insert);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static List<PushInfoBean> queryPushInfo(Context context){
        Log.i(TAG, "queryPushInfo: ");
        List<PushInfoBean> list = new ArrayList<>();
        try {
            Cursor cursor =context.getContentResolver().query(Uri.parse(BaseConfig.PUSH_INFO_CONTENT),null, null, null,null);
            if (cursor!=null){
                while (cursor.moveToNext()){
                    PushInfoBean pushInfoBean = new PushInfoBean();
                    pushInfoBean.setCreateTimes(cursor.getLong(cursor.getColumnIndex(PushTable.CREATE_TIMES)));
                    pushInfoBean.setPushId(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_ID)));
                    pushInfoBean.setPushTitle(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_TITLE)));
                    pushInfoBean.setPushName(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_NAME)));
                    pushInfoBean.setPushType(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_TYPE)));
                    pushInfoBean.setPushStatus(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_STATUS)));
                    pushInfoBean.setPushLink(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_LINK)));
                    pushInfoBean.setPushUrl(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_UTL)));
                    pushInfoBean.setExpireTime(cursor.getLong(cursor.getColumnIndex(PushTable.EXPIRE_TIME)));
                    pushInfoBean.setShowStartTime(cursor.getLong(cursor.getColumnIndex(PushTable.SHOW_START_TIME)));
                    pushInfoBean.setShowEndTime(cursor.getLong(cursor.getColumnIndex(PushTable.SHOW_END_TIME)));
                    pushInfoBean.setShowTimes(cursor.getLong(cursor.getColumnIndex(PushTable.SHOW_TIMES)));
                    pushInfoBean.setPushMessage(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_MESSAGE)));
                    pushInfoBean.setWidth(cursor.getInt(cursor.getColumnIndex(PushTable.WIDTH)));
                    pushInfoBean.setHeight(cursor.getInt(cursor.getColumnIndex(PushTable.HEIGHT)));
                    pushInfoBean.setMarginLeft(cursor.getInt(cursor.getColumnIndex(PushTable.MARGIN_LEFT)));
                    pushInfoBean.setMarginTop(cursor.getInt(cursor.getColumnIndex(PushTable.MARGIN_TOP)));
                    pushInfoBean.setAcc_time(cursor.getInt(cursor.getColumnIndex(PushTable.ACC_TIME)));
                    pushInfoBean.setTaskId(cursor.getString(cursor.getColumnIndex(PushTable.TASK_ID)));
                    pushInfoBean.setTopicType(cursor.getString(cursor.getColumnIndex(PushTable.TOPIC_TYPE)));
                    pushInfoBean.setDuration(cursor.getInt(cursor.getColumnIndex(PushTable.DURATION)));
                    pushInfoBean.setGroupId(cursor.getString(cursor.getColumnIndex(PushTable.GROUP_ID)));
                    pushInfoBean.setIntervalTime(cursor.getLong(cursor.getColumnIndex(PushTable.INTERVAL_TIME)));
                    pushInfoBean.setShowRules(cursor.getInt(cursor.getColumnIndex(PushTable.SHOW_RULES)));
                    pushInfoBean.setSystemId(cursor.getInt(cursor.getColumnIndex(PushTable.SYSTEM_ID)));
                    pushInfoBean.setStartTime(cursor.getLong(cursor.getColumnIndex(PushTable.START_TIME)));
                    pushInfoBean.setEndTime(cursor.getLong(cursor.getColumnIndex(PushTable.END_TIME)));
                    pushInfoBean.setDvbChannelIds(cursor.getString(cursor.getColumnIndex(PushTable.DVB_CHANNEL_IDS)));
                    pushInfoBean.setIpChannelIds(cursor.getString(cursor.getColumnIndex(PushTable.IP_CHANNEL_IDS)));
                    pushInfoBean.setShowScene(cursor.getString(cursor.getColumnIndex(PushTable.SHOW_SCENE)));
                    list.add(pushInfoBean);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public static void  deletePushInfo(Context context, String groupId){
        Log.i(TAG, "deletePushInfo groupId: "+groupId);
        try {
            int delete = context.getContentResolver().delete(Uri.parse(BaseConfig.PUSH_INFO_CONTENT), "groupId=?", new String[]{groupId});
            Log.i(TAG, "deleteData: delete:"+delete);
            String isPush = SharedPreferencesUtil.getStringValue(context,PushTable.GROUP_ID+groupId,"");
            if (!TextUtils.isEmpty(isPush)){
                SharedPreferencesUtil.remove(context,PushTable.GROUP_ID+groupId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static List<PushInfoBean> queryPushInfo(Context context,String selection,String value){
        List<PushInfoBean> list = new ArrayList<>();
        Log.i(TAG, "getPushInfo: "+selection+"="+value);
        try {
            Cursor cursor = context.getContentResolver().query(Uri.parse(BaseConfig.PUSH_INFO_CONTENT), null, selection, null, null);
            if (cursor!=null){
                while (cursor.moveToNext()){
                    PushInfoBean pushInfoBean = new PushInfoBean();
                    pushInfoBean.setCreateTimes(cursor.getLong(cursor.getColumnIndex(PushTable.CREATE_TIMES)));
                    pushInfoBean.setPushId(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_ID)));
                    pushInfoBean.setPushTitle(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_TITLE)));
                    pushInfoBean.setPushName(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_NAME)));
                    pushInfoBean.setPushType(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_TYPE)));
                    pushInfoBean.setPushStatus(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_STATUS)));
                    pushInfoBean.setPushLink(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_LINK)));
                    pushInfoBean.setPushUrl(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_UTL)));
                    pushInfoBean.setExpireTime(cursor.getLong(cursor.getColumnIndex(PushTable.EXPIRE_TIME)));
                    pushInfoBean.setShowStartTime(cursor.getLong(cursor.getColumnIndex(PushTable.SHOW_START_TIME)));
                    pushInfoBean.setShowEndTime(cursor.getLong(cursor.getColumnIndex(PushTable.SHOW_END_TIME)));
                    pushInfoBean.setShowTimes(cursor.getLong(cursor.getColumnIndex(PushTable.SHOW_TIMES)));
                    pushInfoBean.setPushMessage(cursor.getString(cursor.getColumnIndex(PushTable.PUSH_MESSAGE)));
                    pushInfoBean.setWidth(cursor.getInt(cursor.getColumnIndex(PushTable.WIDTH)));
                    pushInfoBean.setHeight(cursor.getInt(cursor.getColumnIndex(PushTable.HEIGHT)));
                    pushInfoBean.setMarginLeft(cursor.getInt(cursor.getColumnIndex(PushTable.MARGIN_LEFT)));
                    pushInfoBean.setMarginTop(cursor.getInt(cursor.getColumnIndex(PushTable.MARGIN_TOP)));
                    pushInfoBean.setAcc_time(cursor.getInt(cursor.getColumnIndex(PushTable.ACC_TIME)));
                    pushInfoBean.setTaskId(cursor.getString(cursor.getColumnIndex(PushTable.TASK_ID)));
                    pushInfoBean.setTopicType(cursor.getString(cursor.getColumnIndex(PushTable.TOPIC_TYPE)));
                    pushInfoBean.setDuration(cursor.getInt(cursor.getColumnIndex(PushTable.DURATION)));
                    pushInfoBean.setGroupId(cursor.getString(cursor.getColumnIndex(PushTable.GROUP_ID)));
                    pushInfoBean.setIntervalTime(cursor.getLong(cursor.getColumnIndex(PushTable.INTERVAL_TIME)));
                    pushInfoBean.setShowRules(cursor.getInt(cursor.getColumnIndex(PushTable.SHOW_RULES)));
                    pushInfoBean.setSystemId(cursor.getInt(cursor.getColumnIndex(PushTable.SYSTEM_ID)));
                    pushInfoBean.setStartTime(cursor.getLong(cursor.getColumnIndex(PushTable.START_TIME)));
                    pushInfoBean.setEndTime(cursor.getLong(cursor.getColumnIndex(PushTable.END_TIME)));
                    pushInfoBean.setDvbChannelIds(cursor.getString(cursor.getColumnIndex(PushTable.DVB_CHANNEL_IDS)));
                    pushInfoBean.setIpChannelIds(cursor.getString(cursor.getColumnIndex(PushTable.IP_CHANNEL_IDS)));
                    pushInfoBean.setShowScene(cursor.getString(cursor.getColumnIndex(PushTable.SHOW_SCENE)));
                    list.add(pushInfoBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public static void updatePushInfo(Context context, PushInfoBean pushInfoBean){
        Log.i(TAG, "updatePushInfo: "+pushInfoBean.toString());
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PushTable.CREATE_TIMES,pushInfoBean.getCreateTimes());
            contentValues.put(PushTable.PUSH_ID,pushInfoBean.getPushId());
            contentValues.put(PushTable.PUSH_TITLE,pushInfoBean.getPushTitle());
            contentValues.put(PushTable.PUSH_NAME,pushInfoBean.getPushName());
            contentValues.put(PushTable.PUSH_TYPE,pushInfoBean.getPushType());
            contentValues.put(PushTable.PUSH_STATUS,pushInfoBean.getPushStatus());
            contentValues.put(PushTable.PUSH_LINK,pushInfoBean.getPushLink());
            contentValues.put(PushTable.PUSH_UTL,pushInfoBean.getPushUrl());
            contentValues.put(PushTable.EXPIRE_TIME,pushInfoBean.getExpireTime());
            contentValues.put(PushTable.SHOW_START_TIME,pushInfoBean.getShowStartTime());
            contentValues.put(PushTable.SHOW_END_TIME,pushInfoBean.getShowEndTime());
            contentValues.put(PushTable.SHOW_TIMES,pushInfoBean.getShowTimes());
            contentValues.put(PushTable.PUSH_MESSAGE,pushInfoBean.getPushMessage());
            contentValues.put(PushTable.WIDTH,pushInfoBean.getWidth());
            contentValues.put(PushTable.HEIGHT,pushInfoBean.getHeight());
            contentValues.put(PushTable.MARGIN_LEFT,pushInfoBean.getMarginLeft());
            contentValues.put(PushTable.MARGIN_TOP,pushInfoBean.getMarginTop());
            contentValues.put(PushTable.ACC_TIME,pushInfoBean.getAcc_time());
            contentValues.put(PushTable.TASK_ID,pushInfoBean.getTaskId());
            contentValues.put(PushTable.TOPIC_TYPE,pushInfoBean.getTopicType());
            contentValues.put(PushTable.DURATION,pushInfoBean.getDuration());
            contentValues.put(PushTable.GROUP_ID,pushInfoBean.getGroupId());
            contentValues.put(PushTable.INTERVAL_TIME,pushInfoBean.getIntervalTime());
            contentValues.put(PushTable.SHOW_RULES,pushInfoBean.getShowRules());
            contentValues.put(PushTable.SYSTEM_ID,pushInfoBean.getSystemId());
            contentValues.put(PushTable.START_TIME,pushInfoBean.getStartTime());
            contentValues.put(PushTable.END_TIME,pushInfoBean.getEndTime());
            contentValues.put(PushTable.DVB_CHANNEL_IDS,pushInfoBean.getDvbChannelIds());
            contentValues.put(PushTable.IP_CHANNEL_IDS,pushInfoBean.getIpChannelIds());
            contentValues.put(PushTable.SHOW_SCENE,pushInfoBean.getShowScene());
            Uri insert = context.getContentResolver().insert(Uri.parse(BaseConfig.PUSH_INFO_CONTENT), contentValues);
            Log.i(TAG, "updatePushInfo: insert:"+insert);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void uploadPushLog(PushInfoBean pushInfoBean,Context context) {
        PushLog pushLog = new PushLog();
        pushLog.setTimestamp(System.currentTimeMillis());
        pushLog.setShow_time(System.currentTimeMillis());
        PushManager.UpLoadPushLog(context,pushInfoBean,pushLog);
    }
    public static void initShow(Context context,PushInfoBean pushInfoBean) {
        Log.i(TAG, "initShow: ");
        long time = System.currentTimeMillis();
        long expire_time = pushInfoBean.getExpireTime();
        Log.i(TAG, "initShow new time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,time));
        Log.i(TAG, "initShow expire_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,expire_time));
        boolean isExpire = TimeUtil.isExpire(time,expire_time);
        if (!isExpire){
            Log.i(TAG, "onNext: this push message not expire");
            long show_start_time = pushInfoBean.getShowStartTime();
            Log.i(TAG, "initShow show_start_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,show_start_time));
            long show_end_time = pushInfoBean.getShowEndTime();
            Log.i(TAG, "initShow show_end_time: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,show_end_time));
            if (0==show_start_time&&0==show_end_time){ //判断开始时间和结束时间是否都等于0 。等于0则通过
                getShowTime(context,pushInfoBean,time);
            }else if (show_start_time== show_end_time) { //判断开始时间是否等于结束时间，等于则通过
                getShowTime(context,pushInfoBean,time);
            } else {
                boolean isShow =TimeUtil.isEffectiveDate(time,show_start_time,show_end_time);
                if (isShow){
                    Log.i(TAG, "onNext: this push message can show");
                    getShowTime(context,pushInfoBean,time);
                }else{
                    Log.i(TAG, "onNext: this push message can't show");
                }
            }
        }else{
            Log.i(TAG, "onNext: this push message is expire");
            PushInfoUtil.deletePushInfo(context,pushInfoBean.getGroupId());
            pushInfoBean.setPushStatus("4");//设置推送状态为4 失效
            PushInfoUtil.uploadPushLog(pushInfoBean,context);
            PushDialog.getInstance(context).removePushInfo(pushInfoBean);
        }
    }
    public static void getShowTime(Context context,PushInfoBean pushInfoBean, long time) {
        Log.i(TAG, "getShowTime: ");
        long startTime = pushInfoBean.getStartTime();//获取开始时间
        Log.i(TAG, "getShowTime startTime: "+ TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,startTime));
        long endTime = pushInfoBean.getEndTime();//获取结束时间
        Log.i(TAG, "getShowTime endTime: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,endTime));
        if (0==startTime&&0==endTime){
            startShow(context,pushInfoBean);
        }else if (startTime == endTime){
            startShow(context,pushInfoBean);
        }else{
            try {
                SimpleDateFormat tf = new SimpleDateFormat(TimeUtil.dateFormatHMS);//格式化当前时间 设置成 小时
                Date currentTime = tf.parse(tf.format(time));
                long timeTime = currentTime.getTime();//获取当前时间
                Log.i(TAG, "getShowTime timeTime: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,timeTime));
                boolean effectiveDate = TimeUtil.isEffectiveDate(timeTime, startTime, endTime);//判断 当前时间 是否在 开始时间和结束时间内
                Log.i(TAG, "getShowTime: effectiveDate:"+effectiveDate);
                if(effectiveDate){//如果是 ，则允许显示
                    startShow(context,pushInfoBean);
                }else{ //否则 不允许显示
                    //获取当前显示的数据，判断是否需要关闭当前显示的内容
                    Log.i(TAG, "getShowTime: not startTime and not endTime, don't show ");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startShow(Context context,PushInfoBean pushInfoBean) {
        Log.i(TAG, "startShow: ");
        boolean isEpgPlayer = Utils.isEPGPlayer(context);
        Log.i(TAG, "startShow: isEpgPlayer:"+isEpgPlayer);
//        if (isEpgPlayer){
//            Log.i(TAG, "startShow: epg is play video or push message is show,don't show");
//        }else{
        try {
            PushDialog instance = PushDialog.getInstance(context);
            if (!instance.isShow()){
                Log.i(TAG, "startShow: start show");
                instance.addPushInfo(pushInfoBean);
//                String ipChannelIds = pushInfoBean.getIpChannelIds();
//////                String dvbChannelIds = pushInfoBean.getDvbChannelIds();
//                if (!TextUtils.isEmpty(dvbChannelIds)||!TextUtils.isEmpty(ipChannelIds)){
                String showScene = pushInfoBean.getShowScene();
                if (!TextUtils.isEmpty(showScene)){
                    Log.i(TAG, "startShow: is showScene push info ,don't delete");
                }else{
                    PushInfoUtil.deletePushInfo(context,pushInfoBean.getGroupId());
                }
            }else{
                Log.i(TAG, "startShow:  dialog is show ,don't show");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        }
    }

    public static  boolean isShow(long time, PushInfoBean pushInfoBean) throws ParseException {
        long startTime = pushInfoBean.getStartTime();//获取开始时间
        Log.i(TAG, "isShow startTime: "+ TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,startTime));
        long endTime = pushInfoBean.getEndTime();//获取结束时间
        if (0==startTime&&0==endTime){
            return true;
        }else if (startTime == endTime){
            return true;
        }else{
            Log.i(TAG, "isShow endTime: "+TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,endTime));
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
                return false;
            }
        }
    }
}
