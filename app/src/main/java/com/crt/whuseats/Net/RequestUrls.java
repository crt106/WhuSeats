package com.crt.whuseats.Net;

/**
 * 存放所有请求Url地址的类
 */
public class RequestUrls
{
    //region TCP连接IP地址
    static final String ServerIP1 = "120.79.7.230";//阿里云服务器ip
    static final String ServerIP2 = "10.133.1.126";//寝室crt的ip
    static final String ServerIP3 = "10.135.97.4";//图书馆crt的ip
    static final String ServerIP4 = "192.168.43.94";//热点的crt的ip
    static final String ServerIP5 = "192.168.137.1";//热点代理
    static final String LocalServerIP = "127.0.0.1";
    //endregion

    public static String LIB_TOKEN = "";  //返回的登录字段


    //region 通用地址
    static final String LIB_GXU_URL = "http://seat.gxu.edu.cn";              //广西大学服务器地址

    static final String LIB_URL = "http://seat.lib.whu.edu.cn";                  //武汉大学服务器地址
    static final String LIB_URLWITHPORT = "https://seat.lib.whu.edu.cn:8443";    //武汉大学服务器地址（https）

    static final String LIB_HOST_GXU = "seat.gxu.edu.cn";                        //武汉大学服务器HOST
    static final String LIB_HOST = "seat.lib.whu.edu.cn";                        //武汉大学服务器地址
    static final String CRT_HOST = "http://120.79.7.230";                        //CRT服务器地址
    static final String CRT_OSS = "https://bucket-crt106.oss-cn-shenzhen.aliyuncs.com";  //CRT阿里云OSS地址


    //endregion


    // region 图书馆功能地址
    static final String URL_LOGIN = "/rest/auth";                                //登录API地址
    static final String LIB_FREEBOOK = LIB_URL + "/rest/v2/freeBook";            //预约座位API地址
    static final String URL_CHECKIN = "/rest/v2/checkIn";                         //签到API地址
    static final String URL_LEAVE = "/rest/v2/leave";                             //暂离API地址
    static final String URL_STOP = "/rest/v2/stop";                              //结束使用API地址


    //endregion
}
