package com.vunke.catv_push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.vunke.catv_push.service.PushServer;


/**
 * Created by zhuxi on 2020/7/29.
 */

public class BootBroadReceiver extends BroadcastReceiver {
    private static final String TAG = "BootBroadReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context,"收到开机广播",Toast.LENGTH_LONG).show();
        Intent pushIntent = new Intent(context, PushServer.class);
        context.startService(pushIntent);
        if (intent!=null){
            String action =intent.getAction();
            if (!TextUtils.isEmpty(action)){
                Log.i(TAG, "onReceive getAction: "+action);
            }
        }

    }
}
