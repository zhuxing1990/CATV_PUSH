package com.vunke.catv_push.manage;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.vunke.catv_push.base.BaseConfig;
import com.vunke.catv_push.util.MacUtils;
import com.vunke.catv_push.util.SharedPreferencesUtil;
import com.vunke.catv_push.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * Created by zhuxi on 2018/1/3.
 */
public class DevicesManager {
    private static final String TAG = "DevicesManager";
    public static JSONObject setRequestParams(Context context){
        Log.i(TAG, "setRequestParams: ");
        JSONObject json = new JSONObject();
        try {
            String mac = MacUtils.getMacAddr();
            String cardId = getSystemData(BaseConfig.CA_CARD);
            if(!TextUtils.isEmpty(cardId)&&!"unknown".equals(cardId)){
                json.put("cardId",cardId);
            }
            String productType = getSystemData(BaseConfig.PRODUCT_TYPE);
            if ("unknown".equals(productType)){
                productType = "2";
            }
            String sn = getSystemData(BaseConfig.SN);
            String stbid = getSystemData(BaseConfig.STBID);
            if ("unknown".equals(stbid)&& !"unknown".equals(sn)){
                stbid = sn;
            }

            String terminalModel = getSystemData(BaseConfig.MODEL);
            if ("unknown".equals(terminalModel)){
                terminalModel = Build.MODEL;
            }
            String userName = getSystemData(BaseConfig.PARTNER_USERID);
            if(!TextUtils.isEmpty(userName)&& !"unknown".equals(userName)){
                json.put("userName",userName);
            }
            String area_id = getSystemData(BaseConfig.AREA_CODE);
            if(!TextUtils.isEmpty(area_id)&& !"unknown".equals(area_id)){
                json.put("Area_id",area_id);
            }
            String county_code= getSystemData(BaseConfig.COUNTY_CODE);
            if(!TextUtils.isEmpty(county_code)&&!"unknown".equals(county_code)){
                json.put("county_code",county_code);
            }
            String city_code = getSystemData(BaseConfig.CITY_CODE);
            if(!TextUtils.isEmpty(city_code)&& !"unknown".equals(city_code)){
                json.put("city_code",city_code);
            }
            String channel = getSystemData(BaseConfig.CHANNEL);
            if(!TextUtils.isEmpty(channel)&&!"unknown".equals(channel)){
                json.put("channel",channel);
            }
            String enablem5G = getSystemData(BaseConfig.ENABLEM5G);
            if(!TextUtils.isEmpty(enablem5G)&& !"unknown".equals(enablem5G)){
                json.put("enablem5G",enablem5G);
            }
            json .put("version_code", Utils.getVersionCode(context))
                    .put("version_name",Utils.getVersionName(context))
                    .put("stbModle", terminalModel)
                    .put("stb_id",stbid)
                    .put("mac", mac)
                    .put("productType", productType)
                    .put("timestamp", System.currentTimeMillis()+"".trim());
                     boolean hasSession = SharedPreferencesUtil.getBooleanValue(context, SharedPreferencesUtil.CleanSessionKey, true);
                     json.put("isCleanSession",hasSession);
            Log.i(TAG, "setRequestParams: json:"+json.toString());
            return json;
        }catch (JSONException e){
            Log.i(TAG, "setRequestParams: onError");
            e.printStackTrace();
        }
        return json;
    }
    /**
     * 获取机顶盒的相关数据
     *
     * @param str
     * @return
     */
    public static String getSystemData(String str) {
        Log.i(TAG, "getSystemData: "+str);
        String value2 = "unknown";
        Class<?> c = null;
        try {
            c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value2 = (String) (get.invoke(c, str, "unknown"));
            Log.i("getSystemData", "get:" +str+":"+ value2);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("getSystemData", "getSystemData failed");
            return value2;
        }
        return value2;
    }
//    public static void queryDevicesInfo(Context context, DeviceInfoBean deviceInfoBean) {
//        String baseURL = "content://stbconfig/authentication/";
//        Uri localUri = Uri.parse(baseURL);
//        Cursor cursor = context.getContentResolver().query(localUri,
//                null, null, null, null);
//        try {
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    String name = cursor.getString(cursor.getColumnIndex("name"));
//                    String value = cursor.getString(cursor.getColumnIndex("value"));
////                    LogUtil.i(TAG, "queryDevicesInfo: " + name + "=" + value);
//                    SetDeviceInfo(name,value,deviceInfoBean);
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//    }

//    private static void SetDeviceInfo(final String name , final String value, final DeviceInfoBean deviceInfoBean) {
//        Observable.just(name)
//                .filter(new Func1<String, Boolean>() {
//                    @Override
//                    public Boolean call(String s) {
//                        return !TextUtils.isEmpty(s);
//                    }
//                })
//                 .observeOn(Schedulers.io())
//                 .subscribe(new Subscriber<String>() {
//                     @Override
//                     public void onCompleted() {
//                         this.unsubscribe();
//                     }
//
//                     @Override
//                     public void onError(Throwable e) {
//                         Log.i(TAG, "onError: ");
//                        e.printStackTrace();
//                         this.unsubscribe();
//                     }
//
//                     @Override
//                     public void onNext(String s) {
////                         Log.d(TAG, "onNext: "+s+"="+value);
//                         switch (s) {
//                             case "username":
//                                 deviceInfoBean.setUsername(value);
//                                 break;
//                             case "password":
//                                 deviceInfoBean.setPassword(value);
//                                 break;
//                             case "user_token":
//                                 deviceInfoBean.setUser_token(value);
//                                 break;
//                             case "auth_server":
//                                 deviceInfoBean.setAuth_server(value);
//                                 break;
//                             case "loginstatus":
//                                 deviceInfoBean.setLoginstatus(value);
//                                 break;
//                             case "stb_id":
//                                 deviceInfoBean.setStb_id(value);
//                                 break;
//                             case "EPGDomain":
//                                 deviceInfoBean.setEPGDomain(value);
//                                 break;
//                             case "EPGGroupNMB":
//                                 deviceInfoBean.setEPGGroupNMB(value);
//                                 break;
//                             case "Area_id":
//                                 deviceInfoBean.setArea_id(value);
//                                 break;
//                             case "Group_id":
//                                 deviceInfoBean.setGroup_id(value);
//                                 break;
//                             default:
//                                 break;
//                         }
//                     }
//                 });
//
//    }
}
