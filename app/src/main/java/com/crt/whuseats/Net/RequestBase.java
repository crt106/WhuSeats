package com.crt.whuseats.Net;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络通信基类
 * Created at v1.0 by crt 2018-12-4
 */
class RequestBase
{
    protected static OkHttpClient okHttpClient;

    static
    {
        okHttpClient = new OkHttpClient();
    }

    //region TCP连接地址
    final String ServerIP1 = "120.79.7.230";//阿里云服务器ip
    final String ServerIP2 = "10.133.1.126";//寝室crt的ip
    final String ServerIP3 = "10.135.97.4";//图书馆crt的ip
    final String ServerIP4 = "192.168.43.94";//热点的crt的ip
    final String ServerIP5 = "192.168.137.1";//热点代理
    final String LocalServerIP = "127.0.0.1";
    //endregion

    public String LIB_TOKEN = "";  //返回的登录字段


    //region 通用地址
    final String LIB_WHU_URL = "http://seat.lib.whu.edu.cn";
    final String LIB_GXU_URL = "http://seat.gxu.edu.cn";

    final String LIB_URL = "http://seat.lib.whu.edu.cn";
    final String LIB_URLWITHPORT = "https://seat.lib.whu.edu.cn:8443";

    final String LIB_HOST_WHU = "seat.lib.whu.edu.cn";
    final String LIB_HOST_GXU = "seat.gxu.edu.cn";

    final String LIB_HOST = "seat.lib.whu.edu.cn";
    //static final String LIB_FREEBOOK="http://seat.lib.whu.edu.cn/rest/v2/freeBook";
    final String LIB_FREEBOOK = LIB_URL + "/rest/v2/freeBook";
    final String CRT_HOST = "http://120.79.7.230";
    final String CRT_OSS = "https://bucket-crt106.oss-cn-shenzhen.aliyuncs.com";

    //endregion


    /**
     * 直接同步获取Response
     * @param request
     * @return
     */
    protected Response GetResponseSync(Request request) throws IOException
    {
        return okHttpClient.newCall(request).execute();
    }


    /**
     * 创建默认Get请求
     * @param url
     * @return
     */
    protected Request CreatDefaultGet(String url)
    {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .addHeader("Host", LIB_HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();
        return request;
    }

    /**
     * 创建默认Post请求
     * @param url
     * @param body
     * @return
     */
    protected Request CreatDefaultPost(String url, RequestBody body)
    {
        Request request=new Request.Builder()
                .post(body)
                .url(url)
                .addHeader("Host", LIB_HOST)
                .addHeader("Connection", "Keep-Alive")
                .build();
        return  request;
    }


}
