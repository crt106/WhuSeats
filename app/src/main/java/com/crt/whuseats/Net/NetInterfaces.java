package com.crt.whuseats.Net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import static com.crt.whuseats.Net.RequestUrls.*;

/**
 * 放置所有的网络请求(Retrofix)的接口
 * Created at v1.0 by crt 2018-12-6
 */
public interface NetInterfaces
{
    //region 图书馆服务器接口

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
     * 获取用户信息
     * @return
     */
    @GET(LIB_URL + URL_USER)
    Observable<ResponseBody> GetUserInfo();

    /**
     * 获取图书馆公告
     * @return
     */
    @GET(LIB_URL+URL_ANNOUNCE)
    Observable<ResponseBody> GetAnnounce();

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

    //endregion


    //region crt服务器接口

    /**
     * 获取权限设定TXT文件的内容
     * @return
     */
    @GET(CRT_HOST+"/WhuSeats_per.txt")
    Observable<ResponseBody> GetPermissionTXT();

    /**
     * 预热和获取新闻
     * @return
     */
    @GET(CRT_HOST+URL_PREHEAT)
    Observable<ResponseBody> PreheatAndNews();

    //endregion
}
