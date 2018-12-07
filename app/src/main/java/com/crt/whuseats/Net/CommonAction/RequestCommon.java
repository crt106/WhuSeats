package com.crt.whuseats.Net.CommonAction;

import android.annotation.SuppressLint;

import com.crt.whuseats.Net.RequestBase;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import static com.crt.whuseats.Net.RequestUrls.LIB_TOKEN;


/**
 * 网络通信类 - 通用命令（签到、暂离、结束使用）
 * Created at v1.0 by crt 2018-12-6
 */
public class RequestCommon extends RequestBase
{
    public enum CommonType
    {
        Check_IN,
        Leave,
        Stop,
    }

    /**
     * 发送上述三类公共请求
     * @param type
     * @param resultmsg
     */
    @SuppressLint("CheckResult")
    public static void SendRequestCommon(CommonType type, Observable<String> resultmsg)
    {
        Observable<ResponseBody> obre=null;
        switch (type)
        {
            case Stop: obre=LibNetRequest.Stop(LIB_TOKEN);break;
            case Leave:obre=LibNetRequest.Leave(LIB_TOKEN);break;
            case Check_IN:obre=LibNetRequest.CheckIn(LIB_TOKEN);break;
        }

       obre.map(new Function<ResponseBody, String>()
       {
           @Override
           public String apply(ResponseBody body) throws Exception
           {
               return body.string();
           }
       }).subscribeOn(Schedulers.newThread())

    }

}
