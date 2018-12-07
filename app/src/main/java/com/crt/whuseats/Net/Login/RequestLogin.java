package com.crt.whuseats.Net.Login;

import android.annotation.SuppressLint;
import android.util.Log;

import com.crt.whuseats.JsonModels.JsonHelp;
import com.crt.whuseats.JsonModels.JsonModel_Login;
import com.crt.whuseats.Net.RequestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.crt.whuseats.Net.RequestUrls.LIB_TOKEN;


/**
 * 网络通信类-登录
 * Created at v1.0 by crt 2018-12-5
 */
public class RequestLogin extends RequestBase
{


    /**
     * 发起登录请求并且返回
     * 执行两步请求：检查登录权限和登录
     *
     * @param user       用户名
     * @param pwd        密码
     * @param observable 监听者
     */
    @SuppressLint("CheckResult")
    public static void SendLoginRequest(String user, String pwd, Observer<JsonModel_Login> observable) throws Exception
    {
        CrtNetRequest.GetPermissionTXT()
                //进行第一步权限检查的判定
                .flatMap(new Function<ResponseBody, Observable<ResponseBody>>()
                {

                    @Override
                    public Observable<ResponseBody> apply(ResponseBody responseBody) throws Exception
                    {
                        //权限检查判定
                        String pertext = responseBody.string();
                        String[] userArray = pertext.split("\r\n");
                        List<String> userList = new ArrayList<>(Arrays.asList(userArray));

                        boolean canlogin = false;

                        //如果第一行控制文本为false 代表关闭登录限制 故无条件返回true 不要绕晕了
                        if (userList.get(0).equals("false"))
                            canlogin = true;
                        else if (userList.contains(user))
                            canlogin = true;
                        else canlogin = false;

                        //根据canlogin进行下一步操作
                        if (canlogin)
                        {
                            return LibNetRequest.Login(user, pwd);
                        } else throw new LoginCRTPermissionException("抱歉，您好像没有权限登录");
                    }
                })
                //返回一个Login的Observable 转换为JsonModel_Login
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .map(new Function<ResponseBody, JsonModel_Login>()
                {
                    @Override
                    public JsonModel_Login apply(ResponseBody body) throws Exception
                    {
                        String jsonstr = body.string();
                        JsonModel_Login info = JsonHelp.GetLogin(jsonstr);
                        //判断是否登陆成功咯~
                        if (info.status.equals("success"))
                        {
                            LIB_TOKEN = info.token;
                            Log.e("Net:Login", "移动端返回Token:" + LIB_TOKEN);
                            return info;

                        } else
                        {
                            Log.e("Net:Login", "移动端登陆失败");
                            throw new LoginException("移动端登录错误" + info.message);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observable);

    }
}
