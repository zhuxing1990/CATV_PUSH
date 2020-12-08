package com.vunke.catv_push.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.vunke.catv_push.R;

public class WeatherBaseDialog {
    private static final String TAG = "WeatherBaseDialog";
    public Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public WeatherBaseDialog(Context context) {
        this.context = context;
        windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.LEFT| Gravity.TOP;
        // 设置图片格式，效果为背景透明
        layoutParams.format = PixelFormat.RGBA_8888;
    }

    private FrameLayout frameLayout;
    public ImageView imageView;
    public MarqueeTextView textView;
    public void showView(){
        Log.i(TAG, "showView: ");
        if (!isShow){
            frameLayout = new FrameLayout(context);
            FrameLayout.LayoutParams fragmeLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
            frameLayout.setLayoutParams(fragmeLayoutParams);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_weather,null);
            imageView= view.findViewById(R.id.weather_img);
            textView = view.findViewById(R.id.weather_textview);
            frameLayout.addView(view);
            windowManager.addView(frameLayout,layoutParams);
            isShow = true;
        }
    }
    private boolean isShow= false;
    public boolean isShow(){
        return isShow;
    }

    public void dismiss(){
        try {
            Log.i(TAG, "dismiss: ");
            if (isShow) {
                isShow = false;
                windowManager.removeView(frameLayout);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
