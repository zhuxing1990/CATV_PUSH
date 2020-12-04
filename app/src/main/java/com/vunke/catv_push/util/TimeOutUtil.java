package com.vunke.catv_push.util;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by zhuxi on 2020/8/6.
 */

public class TimeOutUtil {
    private static final String TAG = "TimeOutUtil";
    public static CountDownTimer countDownTimer;
    public final static int STOP_TIME_OUT = 0x3345;
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case STOP_TIME_OUT:
                    try {
                        if (null!=countDownTimer){
                        countDownTimer.cancel();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    public static void startTimeOut(final Activity activity, long  outTime){
        handler.sendEmptyMessage(STOP_TIME_OUT);
        try {
            countDownTimer = new CountDownTimer(outTime,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i(TAG, "onTick: ");
                }

                @Override
                public void onFinish() {
                    activity.finish();
                }
            };
            countDownTimer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void stopTimeout(StopCallBack stopCallBack){
        handler.sendEmptyMessage(STOP_TIME_OUT);
        if (stopCallBack!=null){
            stopCallBack.onStop();
        }
    }
    public interface StopCallBack{
        void onStop();
    }
}
