package com.crt.whuseats.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.crt.whuseats.Activity.BaseActivity;

//控制本程序的各项设置的类
public class Settings
{
    public static SharedPreferences.Editor UserAndPwdEditor;      //控制用户名与密码的设置编辑器
    public static SharedPreferences UserAndPwd;
    public static SharedPreferences.Editor ListenSettingEditor;   //控制监听的设置编辑器
    public static SharedPreferences ListenSetting;
    public static SharedPreferences.Editor BookSettingEditor;     //控制预约信息的编辑器
    public static SharedPreferences BookSetting;

    public Settings()
    {
        //利用BaseActivity来获取相应的Preference文件
        UserAndPwdEditor= BaseActivity.ApplicationContext.getSharedPreferences("pwddata", Context.MODE_PRIVATE).edit();
        UserAndPwd= BaseActivity.ApplicationContext.getSharedPreferences("pwddata", Context.MODE_PRIVATE);
        ListenSettingEditor=BaseActivity.ApplicationContext.getSharedPreferences("listendata", Context.MODE_PRIVATE).edit();
        ListenSetting=BaseActivity.ApplicationContext.getSharedPreferences("listendata", Context.MODE_PRIVATE);
        BookSettingEditor=BaseActivity.ApplicationContext.getSharedPreferences("bookdata", Context.MODE_PRIVATE).edit();
        BookSetting=BaseActivity.ApplicationContext.getSharedPreferences("bookdata", Context.MODE_PRIVATE);
    }

}
