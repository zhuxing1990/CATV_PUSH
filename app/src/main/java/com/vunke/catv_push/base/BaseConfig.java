package com.vunke.catv_push.base;

public class BaseConfig {
    //测试
//    public static final String BASE_URL = "http://100.125.19.171:8082";

    // 内网测试
    public static final String BASE_URL = "http://192.168.198.33:8082/realTimePush";

    public static final String GET_SUBSCRIBE_LIST="/api/mqtt/getSubscribeList";
    public static final  String SET_PUSH_LOGS = "/api/mqtt/setPushLogs";

    public static final String PUSH_INFO_CONTENT = "content://com.vunke.catv_push/push_info";
    public static final String TOPIC_CONTENT = "content://com.vunke.catv_push.topic/topic_info";


    /**
     *登录时保存账户所在的区域CODE
     */
    public static final String CITY_CODE ="persist.sys.citycode";
    /**
     *登录是保存账号所在的区域CODE
     */
    public static final String COUNTY_CODE ="persist.sys.countycode";
    /**
     *登录成功是保存的accessToken返回的userId
     */
    public static final String PARTNER_USERID = "persist.sys.partnerUserId";
    /**
     * CA 卡号
     */
    public static final String CA_CARD = "persist.sys.ca.card_id";
//    public static final String CA_CARD2 = "sys.ca.cardid";
    /**
     * 区域码
     */
    public static final String AREA_CODE = "persist.sys.osupdate.areacode";

    /**
     * 区分DVB和IP
     * 1 DVB   2 IP
     */
    public static final String PRODUCT_TYPE = "ro.product.type";
    /**
     *渠道标识
     */
    public static final String CHANNEL = "persist.channel.code";
    /**
     * 区分5G标识
     */
    public static final String  ENABLEM5G = "ro.gk.network.enablem5g";

    /**
     * 机顶盒型号
     */
    public static final String MODEL = "ro.product.model";

    /**
     * 终端序列号 STBID
     */
    public static final String SN ="ro.serialno";
    public static final String STBID = "ro.stbid";

    /**
     * igmp
     * 是否组播 ON否 OFF是    old  0 否  1 是
     */
    public static final String IGMP = "persist.sys.multicast";
}
