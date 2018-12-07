package com.crt.whuseats.Net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import static com.crt.whuseats.Net.RequestUrls.*;

/**
 * 放置所有的网络请求(Retrofix)的接口
 */
public interface NetInterfaces
{

    /**
     * 向图书馆服务器发送登录请求
     *
     * @param user
     * @param pwd
     * @return
     */
    @Headers({"Host:"+LIB_HOST,"Connection:Keep-Alive"})
    @GET(LIB_URL + URL_LOGIN)
    Observable<ResponseBody> Login(@Query("username") String user,@Query("password") String pwd);

    /**
     * 获取权限设定TXT文件的内容
     * @return
     */
    @GET("http://120.79.7.230/WhuSeats_per.txt")
    Observable<ResponseBody> GetPermissionTXT();

    /**
     * 签到
     * @param token
     * @return
     */
    @GET(LIB_URL + URL_CHECKIN)
    Observable<ResponseBody> CheckIn(@Query("token") String token);

    /**
     * 暂离
     * @param token
     * @return
     */
    @GET(LIB_URL + URL_LEAVE)
    Observable<ResponseBody> Leave(@Query("token") String token);

    /**
     * 签到
     * @param token
     * @return
     */
    @GET(LIB_URL + URL_STOP)
    Observable<ResponseBody> Stop(@Query("token") String token);
}
