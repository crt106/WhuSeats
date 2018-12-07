package com.crt.whuseats.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.crt.whuseats.ApplicationV;
import com.crt.whuseats.Dialog.AlipayDialog;
import com.crt.whuseats.Dialog.LoadingDialog;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.JsonModels.JsonHelp;
import com.crt.whuseats.JsonModels.JsonModel_Login;
import com.crt.whuseats.JsonModels.JsonModel_User;
import com.crt.whuseats.Net.Login.LoginCRTPermissionException;
import com.crt.whuseats.Net.Login.LoginException;
import com.crt.whuseats.Net.Login.RequestLogin;
import com.crt.whuseats.R;
import com.crt.whuseats.Utils.TimeHelp;
import com.crt.whuseats.Utils.ToastUtils;
import com.crt.whuseats.Utils.UpdateHelp;
import java.util.ArrayList;
import static com.crt.whuseats.ApplicationV.*;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class LoginActivity extends BaseActivity
{
    private static final String TAG = "LoginActivity";

    public static final int ANDROID_M_PERMISSION = 1356564;
    //region 控件
    public EditText Username;
    public EditText Password;
    public CheckBox IsSavePassword;

    public TextView JoinQQGroup;
    private TextView tvLoginPass;
    //endregion

    public static String USERNAME;     //用户名
    public static String PASSWORD;     //密码
    private boolean IsneedUpdate;


    //region 生命周期和按钮相应
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //region 绑定控件和设定控件事件

        Username = (EditText) findViewById(R.id.et_userName);
        Password = (EditText) findViewById(R.id.et_password);
        IsSavePassword = (CheckBox) findViewById(R.id.if_remember_password);
        JoinQQGroup = (TextView) findViewById(R.id.tv_JoinQQGroup);
        tvLoginPass = (TextView) findViewById(R.id.tv_login_pass);

        //跳过登陆按钮点击之后 跳过登陆
        tvLoginPass.setOnClickListener(v ->
        {
            //直接进入主界面
            IsLoginIN = false;
            Toast.makeText(getApplicationContext(), "跳过登录 功能受限~", Toast.LENGTH_SHORT).show();
            Intent StartMain = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(StartMain);
        });

        //长按跳过登录按钮进入管理
        tvLoginPass.setOnLongClickListener(v ->
        {
            IsLoginIN = false;
            Intent StartMain = new Intent(LoginActivity.this, AdminActivity.class);
            startActivity(StartMain);
            return true;
        });

        //绑定加入QQ群文字点击事件
        JoinQQGroup.setOnClickListener((v) ->
        {
            joinQQGroup();
        });

        //endregion

        //region 读取已经保存的用户名和密码
        try
        {
            USERNAME = AppSetting.GetUserName();
            PASSWORD = AppSetting.GetPassWord();
            Username.setText(USERNAME);
            Password.setText(PASSWORD);
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        //endregion

        //获取权限
        getPersimmions();


        //region 向百度统计发送数据
        try
        {
            StatService.setOn(this, 0);
            StatService.start(this);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        //endregion


        // region 实现通知点击的自动登录(解析Intent)
        try
        {
            boolean isautologin = getIntent().getBooleanExtra("IsAutoLogin", false);
            if (isautologin)
            {
                //直接进入主界面
                Intent StartMain = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(StartMain);
                IsLoginIN = true;
            }
        } catch (Exception e)
        {
            Log.e(TAG, "AutoStart:" + e.toString());
        }

        //endregion

        CheckUpdate();
        WebPreheat();
    }


    //简单的登陆效果
    public void ButtonLogin_Click(View v)
    {
        //显示小菊花
        LoadingDialog.LoadingShow(LoginActivity.this, true);

        //检查是不是处于维护状态
        if (TimeHelp.IsOverTime("23:50") || !TimeHelp.IsOverTime("0:30"))
        {
            AlertDialog temp = new AlertDialog.Builder(
                    this)
                    .setTitle("提示")
                    .setMessage("此时处于图书馆服务器维护阶段")
                    .setPositiveButton("溜了溜了", null)
                    .create();
            temp.show();
            LoadingDialog.LoadingHide();
            return;
        }
        //移动端登录
        USERNAME = Username.getText().toString();
        PASSWORD = Password.getText().toString();
        try
        {
            RequestLogin.SendLoginRequest(USERNAME, PASSWORD, new Observer<JsonModel_Login>()
            {
                @Override
                public void onSubscribe(Disposable d)
                {

                }

                @Override
                public void onNext(JsonModel_Login jsonModel_login)
                {
                    ToastUtils.ShowLongToast("登陆成功");

                    //检查是否保存密码 是则保存到那啥里面
                    if (IsSavePassword.isChecked())
                    {
                        AppSetting.SetUserName(USERNAME);
                        AppSetting.SetPassWord(PASSWORD);
                        Log.e(TAG, "onNext: 用户名密码保存成功");
                    } else
                        AppSetting.ClearUserInfo();

                    LoadingDialog.LoadingHide();
                }

                @Override
                public void onError(Throwable e)
                {
                    if(e instanceof LoginCRTPermissionException)
                    {
                        ToastUtils.ShowShortToast("权限错误");
                        return;
                    }
                    else if(e instanceof LoginException)
                    {
                        ToastUtils.ShowShortToast(e.getMessage());
                    }
                    else
                    {
                        ToastUtils.ShowShortToast("未处理的异常："+e.getMessage());
                    }
                    LoadingDialog.LoadingHide();
                }

                @Override
                public void onComplete()
                {

                }
            });
        }
        catch (Exception e3)
        {
            Log.e(TAG, "ButtonLogin_Click: "+e3.toString());
        }

    }
    //endregion


    boolean Ispermit;


    //region 检查更新
    public void CheckUpdate()
    {
        UpdateHelp updateHelp = new UpdateHelp(this, true);
        boolean isneedUptade = updateHelp.CheckUpdate();
    }


    //endregion


    /****************
     *
     * 发起添加群流程。群号：WhuSeats(789729207) 的 key 为： mo5gO56OIB_tQ1idGlZDAItUqbPPHHDL
     * 调用 joinQQGroup(mo5gO56OIB_tQ1idGlZDAItUqbPPHHDL) 即可发起手Q客户端申请加群 WhuSeats(789729207)
     *
     * key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public boolean joinQQGroup()
    {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + "mo5gO56OIB_tQ1idGlZDAItUqbPPHHDL"));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try
        {
            startActivity(intent);
            return true;
        } catch (Exception e)
        {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    //和服务器的预热以及news获取,以及获取吱口令
    public void WebPreheat()
    {
//        mbinder.ASPNETpreheat(new onTaskResultReturn()
//        {
//            @Override
//            public void OnTaskSucceed(Object... data)
//            {
//                try
//                {
//                    String news = data[0].toString();
//                    news = news.replace("\"", "");
//                    String isnews = news.split("-")[0];
//                    String msg = news.split("-")[1];
//                    if (isnews.equals("true"))
//                    {
//                        AlertDialog tmp = new AlertDialog.Builder(LoginActivity.this)
//                                .setTitle("News")
//                                .setMessage(msg)
//                                .create();
//                        tmp.show();
//                    }
//                } catch (Exception e)
//                {
//                    Log.e(TAG, "ASPNETpreheat:", e.getCause());
//                }
//            }
//
//            @Override
//            public void OnTaskFailed(Object... data)
//            {
//
//            }
//        });

    }

    //region 安卓6.0动态检查权限 顺便检查一下通知有没有打开
    @TargetApi(23)
    private void getPersimmions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {

            ArrayList<String> permissions = new ArrayList<String>();

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }


            if (permissions.size() > 0)
            {
                requestPermissions(permissions.toArray(new String[permissions.size()]), ANDROID_M_PERMISSION);
            }
        }
        //检查通知是否开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                NotificationManagerCompat no = NotificationManagerCompat.from(LoginActivity.this);
                //如果未开启 跳转对话框开启
                if (!no.areNotificationsEnabled())
                {
                    AlertDialog tmp = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("提示！")
                            .setMessage("发现你没有打开本应用的通知诶,这样的话在预约第二天座位的时候会大概率导致失败 并且就算成功也没有反馈\n而且不打开的话这个对话框每次都会来烦你~")
                            .setPositiveButton("前往开启", jump2NoticeOpen)
                            .create();
                    tmp.show();
                }
            } catch (Exception e)
            {
                Log.e(TAG, "getPersimmions: ", e.getCause());
            }
        }
    }

    //获取权限回调函数
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean IsGetPermission = true;
        switch (requestCode)
        {
            case (ANDROID_M_PERMISSION):
            {
                for (int results : grantResults)
                {
                    if (results == PackageManager.PERMISSION_GRANTED)
                        continue;
                    IsGetPermission = false;
                }
                break;
            }
        }
        if (IsGetPermission)
        {
            Log.e("LoginActivity", "权限获取成功");
        } else
        {
            Log.e("LoginActivity", "权限获取失败");
        }
    }

    /**
     * 跳转到开启通知的界面
     */
    AlertDialog.OnClickListener jump2NoticeOpen = (dialog, p) ->
    {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(localIntent);
    };
    //endregion


}
