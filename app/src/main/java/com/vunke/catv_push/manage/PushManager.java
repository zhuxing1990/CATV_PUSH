package com.vunke.catv_push.manage;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.vunke.catv_push.base.BaseConfig;
import com.vunke.catv_push.db.PushTable;
import com.vunke.catv_push.modle.ChannelBean;
import com.vunke.catv_push.modle.PushDataBean;
import com.vunke.catv_push.modle.PushInfoBean;
import com.vunke.catv_push.modle.PushLog;

import org.json.JSONObject;

/**
 * Created by zhuxi on 2020/8/4.
 */

public class PushManager {
    private static final String TAG = "PushManager";
    public static void initPush(Context context, final PushCallBack callBack){
        try {
            JSONObject json = DevicesManager.setRequestParams(context);

            OkGo.<String>post(BaseConfig.BASE_URL+BaseConfig.GET_SUBSCRIBE_LIST)
                    .tag(TAG)
                    .upJson(json)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.i(TAG, "onSuccess: "+response.toString());
                            if (!TextUtils.isEmpty(response.body())){
                                try {
                                    PushDataBean pushDataBean = new Gson().fromJson(response.body(), PushDataBean.class);
                                    if (callBack!=null){
                                        callBack.onSuccess(pushDataBean);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    if (callBack!=null){
                                        callBack.onFailed(response);
                                    }
                                }

                            }else{
                                if (callBack!=null){
                                    callBack.onFailed(response);
                                }
                            }

                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.i(TAG, "onError: ");
                            if (callBack!=null){
                                callBack.onError(response,response.getException());
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
   public interface PushCallBack{
        void onSuccess(PushDataBean response);
        void onError(Response<String> response, Throwable exception);
        void onFailed(Response<String> response);
   }
   public static void UpLoadPushLog(Context context, PushInfoBean pushInfoBean, PushLog pushLog){
        try {
            JSONObject json = DevicesManager.setRequestParams(context);
                json.put(PushTable.PUSH_ID,pushInfoBean.getPushId())
                .put("timestamp",pushLog.getTimestamp())
                .put("acc_time",pushLog.getAcc_time())
                .put("show_time",pushLog.getShow_time())
                .put("direct_time",pushLog.getDirect_time())
                .put(PushTable.PUSH_STATUS,pushInfoBean.getPushStatus())
                .put(PushTable.PUSH_TYPE,pushInfoBean.getPushType())
                .put(PushTable.TASK_ID,pushInfoBean.getTaskId())
                .put(PushTable.GROUP_ID,pushInfoBean.getGroupId())
                .put(PushTable.TOPIC_TYPE,pushInfoBean.getTopicType())
                .put(PushTable.SYSTEM_ID,pushInfoBean.getSystemId());
//            Log.i(TAG, "UpLoadPushLog: request:"+json.toString());
            OkGo.<String>post(BaseConfig.BASE_URL+BaseConfig.SET_PUSH_LOGS)
                    .tag(TAG)
                    .upJson(json)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.i(TAG, "UpLoadPushLog onSuccess:  "+response.body());
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.i(TAG, "UpLoadPushLog onError: "+ response.message(),response.getException());
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
   }

   public static void uploadChannelInfo(String info){
       try {
           ChannelBean channelBean =  new Gson().fromJson(info, ChannelBean.class);
           String ca_card = DevicesManager.getSystemData(BaseConfig.CA_CARD);
           channelBean.setCardId(ca_card);
           String json = new Gson().toJson(channelBean);
           OkGo.<String>post(BaseConfig.BASE_URL+BaseConfig.QUERY_CHANNEL)
           .tag(TAG).upJson(json).execute(new StringCallback() {
               @Override
               public void onSuccess(Response<String> response) {
                   if (!TextUtils.isEmpty(response.body())){
                       Log.i(TAG, "onSuccess: response:"+response.body());
                   }
               }

               @Override
               public void onError(Response<String> response) {
                   super.onError(response);
                   Log.i(TAG, "onError: ");
               }
           });
       } catch (JsonSyntaxException e) {
           e.printStackTrace();
       }catch (Exception e){
           e.printStackTrace();
       }
   }

}
