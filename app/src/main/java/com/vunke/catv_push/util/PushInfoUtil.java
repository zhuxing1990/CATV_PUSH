package com.vunke.catv_push.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.vunke.catv_push.base.BaseConfig;
import com.vunke.catv_push.db.PushTable;
import com.vunke.catv_push.manage.PushManager;
import com.vunke.catv_push.modle.PushInfoBean;
import com.vunke.catv_push.modle.PushLog;

import java.util.ArrayList;
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
                    list.add(pushInfoBean);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public static void  deletePushInfo(Context context, String push_id){
        Log.i(TAG, "deletePushInfo: "+push_id);
        try {
            int delete = context.getContentResolver().delete(Uri.parse(BaseConfig.PUSH_INFO_CONTENT), "push_id=?", new String[]{push_id});
            Log.i(TAG, "deleteData: delete:"+delete);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static int updatePushInfo(Context context, PushInfoBean pushInfoBean){
        Log.i(TAG, "updatePushInfo: "+pushInfoBean.toString());
        int update = -1;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PushTable.PUSH_ID,pushInfoBean.getPushId());
            contentValues.put(PushTable.PUSH_STATUS,pushInfoBean.getPushStatus());
            contentValues.put(PushTable.ACC_TIME,pushInfoBean.getAcc_time());
            update  = context.getContentResolver().update(Uri.parse(BaseConfig.PUSH_INFO_CONTENT), contentValues, "push_id=?", new String[]{pushInfoBean.getPushId()});
        }catch (Exception e){
            e.printStackTrace();
        }
      return update;
    }

    public static void uploadPushLog(PushInfoBean pushInfoBean,Context context) {
        PushLog pushLog = new PushLog();
        pushLog.setTimestamp(System.currentTimeMillis());
        pushLog.setShow_time(System.currentTimeMillis());
        PushManager.UpLoadPushLog(context,pushInfoBean,pushLog);
    }
}
