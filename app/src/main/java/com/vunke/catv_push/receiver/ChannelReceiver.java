package com.vunke.catv_push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vunke.catv_push.base.BaseConfig;
import com.vunke.catv_push.db.PushTable;
import com.vunke.catv_push.manage.DevicesManager;
import com.vunke.catv_push.manage.PushManager;
import com.vunke.catv_push.modle.ChannelBean;
import com.vunke.catv_push.modle.PushInfoBean;
import com.vunke.catv_push.util.PushInfoUtil;
import com.vunke.catv_push.util.SharedPreferencesUtil;
import com.vunke.catv_push.util.TimeUtil;
import com.vunke.catv_push.view.PushDialog;

import java.util.List;

public class ChannelReceiver extends BroadcastReceiver {
    private static final String TAG = "ChannelReceiver";
    public static final String ACTION_NAME = "com.hunancatv.dvb.action.LIVE_MSG";//PackageName为Launcher的包名，比如湖南蜗牛TV为com.hncatv.dvb  com.hunancatv.dvb
    private static final String LIVE_INFO = "live_info";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null){
            String action = intent.getAction();
            Log.i(TAG, "onReceive: ");
            if (!TextUtils.isEmpty(action)){
                if (intent.hasExtra(LIVE_INFO)){
                    String jsonStr = intent.getStringExtra(LIVE_INFO);
                    Log.i(TAG, "onReceive: get jsonStr:"+jsonStr);
                    if (!TextUtils.isEmpty(jsonStr)){
                        long currentTimeMillis = System.currentTimeMillis();
                        String data = TimeUtil.getDateTime(TimeUtil.dateFormatYMDHMofChinese,currentTimeMillis);
//                        Toast.makeText(context,data+":"+jsonStr,Toast.LENGTH_LONG).show();
                        PushManager.uploadChannelInfo(jsonStr);
                        try {
                            ChannelBean channelBean = new Gson().fromJson(jsonStr, ChannelBean.class);
                            if (channelBean.getStatus()==0){ //状态等于0  播放节目
                                String channelId = channelBean.getChannelId();
                                Log.i(TAG, "onReceive: get channelId:"+channelId);
                                SharedPreferencesUtil.setStringValue(context,BaseConfig.CHANNEL_ID,channelId);
                                String channel = SharedPreferencesUtil.getStringValue(context, BaseConfig.CHANNEL_ID, null);
                                Log.i(TAG, "onReceive: set channel:"+channel);
                                PushDialog instance = PushDialog.getInstance(context);
                                instance.setChannelId(channelId);
                                if (instance.isShow()){
                                    instance.dismiss();
                                    instance.clearPush();
                                }
                                String productType = DevicesManager.getSystemData(BaseConfig.PRODUCT_TYPE);
//                                if ("unknown".equals(productType)){
//                                    productType = "2";
//                                }
                                queryPush(context, PushTable.SHOW_RULES);
                            }else {
                                SharedPreferencesUtil.setStringValue(context,BaseConfig.CHANNEL_ID,"");
                                PushDialog instance = PushDialog.getInstance(context);
                                instance.setChannelId(null);
                                if (instance.isShow()){
                                    instance.dismiss();
                                    instance.clearPush();
                                }
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    }

    private void queryPush(Context context,String selection) {
        Log.i(TAG, "queryPush: ");
        List<PushInfoBean> pushInfoBeans = PushInfoUtil.queryPushInfo(context,selection+" is not null" , null);
        if (pushInfoBeans!=null&&pushInfoBeans.size()!=0){
            for (int i = 0; i < pushInfoBeans.size(); i++) {
                PushInfoBean pushInfoBean = pushInfoBeans.get(i);
                PushInfoUtil.initShow(context,pushInfoBean);
            }
        }else{
            Log.i(TAG, "queryPush: query channel push is null");
        }
    }

}
