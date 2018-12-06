package com.crt.whuseats.Net;

import android.annotation.SuppressLint;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.IoScheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
     * 基础的发送http请求
     *
     * @param request
     * @return
     */

    protected static void SendRequest(Request request, Observer<Response> observer)
    {
        Observable<Response> responseObservable = Observable.create(new ObservableOnSubscribe<Response>()
        {
            @Override
            public void subscribe(ObservableEmitter<Response> emitter) throws Exception
            {
                Response response = okHttpClient.newCall(request).execute();
                emitter.onNext(response);
            }

        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread());
    }
}
