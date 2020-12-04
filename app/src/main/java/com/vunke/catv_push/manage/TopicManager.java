package com.vunke.catv_push.manage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.vunke.catv_push.base.BaseConfig;
import com.vunke.catv_push.db.TopicTable;
import com.vunke.catv_push.modle.TopicBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuxi on 2020/8/13.
 */

public class TopicManager {
    private static final String TAG = "TopicManager";
    public static void saveTopicInfo(Context context, TopicBean topicBean){
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TopicTable.TOPIC_NAME,topicBean.getTopicName());
            contentValues.put(TopicTable.QOS,topicBean.getQos());
            Uri insert = context.getContentResolver().insert(Uri.parse(BaseConfig.TOPIC_CONTENT), contentValues);
            Log.i(TAG, "saveTopicInfo: insert:"+insert);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int updateTopicInfo(Context context, TopicBean topicBean){
        int update = -1;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TopicTable.TOPIC_NAME,topicBean.getTopicName());
            contentValues.put(TopicTable.QOS,topicBean.getQos());
            String where = TopicTable.TOPIC_NAME+"=?";
             update = context.getContentResolver().update(Uri.parse(BaseConfig.TOPIC_CONTENT), contentValues, where, new String[]{topicBean.getTopicName()});
        }catch (Exception e){
            e.printStackTrace();
        }
        return update;
    }
    public static List<TopicBean> queryTopicInfoList(Context context){
        List<TopicBean> list= new ArrayList<>();
        try {
            Cursor cursor = context.getContentResolver().query(Uri.parse(BaseConfig.TOPIC_CONTENT), null, null, null, null);
            if (cursor!=null){
                while (cursor.moveToNext()){
                    TopicBean bean = new TopicBean();
                    bean.setTopicName(cursor.getString(cursor.getColumnIndex(TopicTable.TOPIC_NAME)));
                    bean.setQos(cursor.getInt(cursor.getColumnIndex(TopicTable.QOS)));
                    list.add(bean);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  list;
    }
}
