package com.crt.whuseats.Net;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


import static com.crt.whuseats.Net.RequestUrls.*;

/**
 * 网络通信基类
 * Created at v1.0 by crt 2018-12-4
 */
public class RequestBase
{
    /**
     * OKHttp 客户端 直接控制时主要用于下载更新
     */
    protected static OkHttpClient okHttpClient;
    /**
     * Retrofit服务(baseUrl为图书馆服务器)
     */
    protected static Retrofit LibretrofitService;
    /**
     * Retrofit服务(baseUrl为crt服务器)
     */
    protected static Retrofit CrtretrofitService;
    /**
     * Retrofit请求接口
     */
    protected static NetInterfaces LibNetRequest;
    protected static NetInterfaces CrtNetRequest;

    static
    {
        //初始化实例
        okHttpClient = new OkHttpClient();

        LibretrofitService = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(LIB_URL)
                .build();

        CrtretrofitService = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(CRT_HOST)
                .build();

        LibNetRequest = LibretrofitService.create(NetInterfaces.class);
        CrtNetRequest = CrtretrofitService.create(NetInterfaces.class);
    }


}
