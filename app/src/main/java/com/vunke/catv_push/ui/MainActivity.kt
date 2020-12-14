package com.vunke.catv_push.ui

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.media.audiofx.BassBoost
import android.os.Bundle
import android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.vunke.catv_push.R
import com.vunke.catv_push.util.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //        var bean = PushInfoBean()
//        var b = PushInfoBean()
//        b.createTimes=1606143127554
//        b.duration=2
//        b.expireTime=1606459975000
//        b.groupId="3741932"
//        b.height=0
//        b.intervalTime=20
//        b.marginLeft=0
//        b.marginTop=0
//        b.pushId="3"
//        b.pushLink=""
//        b.pushMessage="长沙市气象台发布暴雪橙色预警[Ⅱ级/严重]测试：长沙市气象台11月17日16时15分发布暴雪橙色预警信号：预计长沙市区未来2小时将发生雷电活动，出现暴雪灾害事故的可能性比较大，请注意防范。"
//        b.pushStatus="0"
//        b.pushTitle="暴雪"
//        b.pushType = "4"
////        b.pushUrl="http://42.192.82.189/customFace/1606136135372.jpg"
//        b.pushUrl="http://www.keyou.club/weather/gale_blue.png"
//        b.showEndTime=1606459975000
//        b.showStartTime=1606288038000
//        b.showTimes=60
//        b.taskId="1"
//        b.topicType="4"
//        b.width=0
//        b.showRules = 0
//        b.systemId = 1
//        PushInfoUtil.savePushInfo(applicationContext,b)


//        bean.pushId = "3";
//        bean.createTime = System.currentTimeMillis()
//        bean.expireTime = System.currentTimeMillis()+500000L
//        bean.showStartTime = System.currentTimeMillis()+1000L
//        bean.showEndTime = System.currentTimeMillis()+400000L
//        bean.pushName = "测试推送"
//        bean.pushTitle = "本地测试推送"
//        bean.pushType = "2"
//        bean.pushStatus = "2"
////        bean.pushLink = "http://www.souhu.com"
////        bean.pushUrl = "https://m.sohu.com/?pvid=000115_3w_index&jump=front"
//        bean.pushUrl = "http://119.39.118.162:8082/video/20191113/video_180127.mp4"
//        bean.showTimes = 15
//        bean.pushMessage = "测试"
//        bean.width = -1
//        bean.height = 1
//        bean.marginLeft=0
//        bean.marginTop = 0
//        PushInfoUtil.savePushInfo(applicationContext,bean)
//        finish()
    }


}