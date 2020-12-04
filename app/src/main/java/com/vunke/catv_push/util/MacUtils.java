package com.vunke.catv_push.util;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by zhuxi on 2020/8/4.
 */

public class MacUtils {
    private static final String TAG = "MacUtils";
    public static String default_MAC = "00:00:00:00:00:00";
    public static String getMacAddr(){
        String LocalMac= getLocalMacAddressFromBusybox();
        return  isMacAddr(LocalMac)?LocalMac:default_MAC;
    }
    /**
     * 判断MAC地址是否正确
     * @param mac
     * @return
     */
    public static boolean isMacAddr(@Nullable String mac){
        if (TextUtils.isEmpty(mac)){
            return false;
        }
        return mac.matches("([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}");
    }
    /**
     * 获取的MAC地址和机顶盒MAC地址不一致，不建议使用 根据文件获取MAC地址
     *
     * @return
     */
    public static String getLocalMacAddressFromBusybox() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");

        // result == null
        if (result == null) {
            return "没有mac";
        }

        //
        // 磺eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
        if (result.length() > 0 && result.contains("HWaddr") == true) {
            Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
            Log.i("test", "Mac:" + Mac + " Mac.length: " + Mac.length());

            result = Mac;
            Log.i("test", result + " result.length: " + result.length());
        }
        return result.trim();
    }

    private static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            //
            while ((line = br.readLine()) != null && line.contains(filter) == false) {
                Log.i("test", "line: " + line);
            }

            result = line;
            Log.i("test", "result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
