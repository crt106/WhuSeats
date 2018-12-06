package com.crt.whuseats.Net;

import android.annotation.SuppressLint;
import android.util.Log;

import com.crt.whuseats.JsonModels.JsonHelp;
import com.crt.whuseats.JsonModels.JsonModel_Login;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;


/**
 * 网络通信类-登录
 * Created at v1.0 by crt 2018-12-5
 */
public class RequestLogin extends RequestBase
{

    //登录接口地址
    final String URL_LOGIN = "/rest/auth";

    /**
     * 发起登录请求并且返回
     * @param user 用户名
     * @param pwd 密码
     * @param observable 监听者
     */
    @SuppressLint("CheckResult")
    public void SendLoginRequest(String user, String pwd, Observer<JsonModel_Login> observable) throws Exception
    {
        String loginurl = LIB_URL + URL_LOGIN + "?username=" + user + "&password=" + pwd;
        Request loginRequest= CreatDefaultGet(loginurl);
        Observable.create(new ObservableOnSubscribe<JsonModel_Login>()
        {
            @Override
            public void subscribe(ObservableEmitter<JsonModel_Login> emitter) throws Exception
            {
                String jsonstr=GetResponseSync(loginRequest).body().string();
                JsonModel_Login info=JsonHelp.GetLogin(jsonstr);

                //判断是否登陆成功咯~
                if (info.status.equals("success"))
                {
                    LIB_TOKEN = info.token;
                    Log.e("Net:Login", "移动端返回Token:" + LIB_TOKEN);
                    emitter.onComplete();

                } else
                {
                    Log.e("Net:Login", "移动端登陆失败");
                    emitter.onError(new Exception("移动端登录失败"+info.message));
                }

            }
        }).observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(observable);

    }
}
