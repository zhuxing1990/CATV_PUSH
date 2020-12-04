package com.vunke.catv_push.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vunke.catv_push.R;
import com.vunke.catv_push.util.TimeOutUtil;

/**
 * Created by zhuxi on 2020/8/6.
 */

public class TextInfoActivity extends AppCompatActivity {
    private static final String TAG = "TextInfoActivity";
    private TextView push_text_title,push_text_info;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textonfo);
        initView();
        initData(getIntent());
    }

    private void initView() {
        push_text_title = findViewById(R.id.push_text_title);
        push_text_info = findViewById(R.id.push_text_info);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData(intent);
    }

    private void initData(Intent intent) {
        if (intent.hasExtra("push_title")){
            String push_title = intent.getStringExtra("push_title");
            if (!TextUtils.isEmpty(push_title)){
                push_text_title.setText(push_title);
            }
        }
        if (intent.hasExtra("push_info")){
            String push_info = intent.getStringExtra("push_info");
            if (!TextUtils.isEmpty(push_info)){
                push_text_info.setText(push_info);
            }
        }
        if (intent.hasExtra("show_times")){
            String show_times = intent.getStringExtra("show_times");
            if (!TextUtils.isEmpty(show_times)){
                TimeOutUtil.startTimeOut(this, Long.valueOf(show_times));
            }
        }
    }
}
