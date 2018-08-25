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
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.crt.whuseats.Dialog.CustomProgressDialog;
import com.crt.whuseats.Dialog.UpdateDialog;
import com.crt.whuseats.Interface.onProgressReturn;
import com.crt.whuseats.JsonHelps.JsonHelp;
import com.crt.whuseats.JsonHelps.JsonInfo_User;
import com.crt.whuseats.Service.NetService;
import com.crt.whuseats.R;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.Utils.TimeHelp;

import java.io.File;
import java.util.ArrayList;

public class LoginActivity extends BaseActivity
{

    public static final int ANDROID_M_PERMISSION=1;
    //region 控件
    public EditText Username;
    public EditText Password;
    public CheckBox IsSavePassword;
//  public CheckBox IsLoginpc;
    public CustomProgressDialog downloadDialog;
    public UpdateDialog ConfimDialog;
    public TextView JoinQQGroup;
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
        Username=(EditText)findViewById(R.id.et_userName);
        Password=(EditText)findViewById(R.id.et_password);
        IsSavePassword=(CheckBox)findViewById(R.id.if_remember_password);
//        IsLoginpc=(CheckBox)findViewById(R.id.if_loginpc);
        JoinQQGroup=(TextView)findViewById(R.id.tv_JoinQQGroup);

        //读取已经保存的用户名和密码
        try
        {
            String user=AppSetting.UserAndPwd.getString("Username","" );
            String pwd=AppSetting.UserAndPwd.getString("Password","" );

            Username.setText(user);
            Password.setText(pwd);
        }
        catch (Exception e)
        {
            Log.e("LoginActivity", e.getMessage());
        }
        //绑定加入QQ群文字点击事件
        JoinQQGroup.setOnClickListener((v)->{joinQQGroup();});
        getPersimmions();

        //向百度统计发送数据
        try
        {
            StatService.setOn(this, 0);
            StatService.start(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //实现通知点击的自动登录
        try
        {
            boolean isautologin=getIntent().getBooleanExtra("IsAutoLogin", false);
            if(isautologin)
            {
                //直接进入主界面
                Intent StartMain = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(StartMain);
            }
        }

        catch (Exception e)
        {
           Log.e("LoginActivity", "AutoStart:"+e.toString());
        }

    }

    //在服务绑定之后检查更新
    @Override
    protected void onServiceBinded()
    {
        super.onServiceBinded();
        CheckUpdate();
    }

    //简单的登陆效果
    public void ButtonLogin_Click(View v)
    {
        //检查是不是处于维护状态
        if(TimeHelp.IsOverTime("23:50")||!TimeHelp.IsOverTime("0:30"))
        {
            AlertDialog temp=new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("此时处于图书馆服务器维护阶段")
                    .setPositiveButton("溜了溜了",null )
                    .create();
            temp.show();
            return;
        }

        //如果是合法用户才予以登陆
        if(CheckPermission(Username.getText().toString()))
        {
            //获取用户信息回调接口 如果处于冻结状态的话...
            onTaskResultReturn UserInfoResult=new onTaskResultReturn()
            {
                @Override
                public void OnTaskSucceed(Object... data)
                {
                    try
                    {
                        JsonInfo_User userdata= JsonHelp.GetUserInfo((String)data[0]);
                        //保存用户姓名
                        AppSetting.UserAndPwdEditor.putString("name",userdata.name).apply();
                    } catch (Exception e)
                    {
                        Log.e("LoginActivity", e.toString());
                    }
                }

                @Override
                public void OnTaskFailed(Object... data)
                {

                }
            };
            //创建[移动端登录]回调处理接口
            onTaskResultReturn Result = new onTaskResultReturn()
            {
                @Override
                public void OnTaskSucceed(Object... data)
                {
                    try
                    {
                        mbinder.GetUserInfo(UserInfoResult);
                       Toast.makeText(getApplicationContext(), "登录成功~", Toast.LENGTH_SHORT).show();
                       Intent StartMain = new Intent(LoginActivity.this, MainActivity.class);
                       startActivity(StartMain);

                    }
                    catch (Exception e)
                    {
                        Log.e("LoginActivity", e.getMessage());
                    }

                }

                @Override
                public void OnTaskFailed(Object... data)
                {
                    Toast.makeText(getApplicationContext(), "登录失败,看看是不是输错密码啥的..", Toast.LENGTH_SHORT).show();
                }
            };

            //移动端登录
            USERNAME=Username.getText().toString();
            PASSWORD=Password.getText().toString();
            mbinder.login(USERNAME,PASSWORD,Result);


            //检查是否保存密码 是则保存到那啥里面
            if (IsSavePassword.isChecked())
            {
                AppSetting.UserAndPwdEditor.putString("Username", Username.getText().toString());
                AppSetting.UserAndPwdEditor.putString("Password", Password.getText().toString());
            }
            else
                AppSetting.UserAndPwdEditor.clear();

            AppSetting.UserAndPwdEditor.apply();
        }
        else
            Toast.makeText(LoginActivity.this, "抱歉,您没有登录权限", Toast.LENGTH_LONG).show();


//        //调试时的跳过登陆
//        Intent StartMain=new Intent(LoginActivity.this,MainActivity.class);
//        startActivity(StartMain);

    }
    //endregion


    boolean Ispermit;
    //检查登陆权限
    public boolean CheckPermission(String Username)
    {

        Thread checkThread=new Thread(
                ()->{
            Ispermit=mbinder.CheckPermissionTxT(Username);
        });
        try
        {
            checkThread.start();
            checkThread.join();
            return Ispermit;
        }
        catch (Exception e)
        {
            Log.e("LoginActivity_check", e.getMessage());
            return false;
        }
    }

    //region 检查更新
    public void CheckUpdate()
    {

        try
        {
            Thread t=new Thread(()->{IsneedUpdate=mbinder.CheckUpdate_IsShowDialog(false);});
            t.start();
            t.join();
        }
        catch (Exception e)
        {
            Log.e("Login_Checkupdate", e.toString());
        }
        if(!IsneedUpdate)
            return;

        //显示确定对话框
        ConfimDialog=new UpdateDialog(LoginActivity.this,NetService.ISFORCEUPDATE);
        ConfimDialog.show();
        ConfimDialog.setButtonOKListenner(ConfimDialog_OK);
    }

    //下载更新进度回调
    onProgressReturn downloadProgress=(data)->{
        downloadDialog.setProgress(((Number)data[0]).intValue());
    };
    //下载完成结果回调
    onTaskResultReturn downloadTaskReturn=new onTaskResultReturn()
    {
        @Override
        public void OnTaskSucceed(Object... data)
        {
            downloadDialog.dismiss();
            File downloadApk=(File)data[0];

            if(downloadApk!=null)
            InstallApk(downloadApk);
            //下载更新错误 弹出一个错误提示框
            else
            {
                AlertDialog tempDialog=new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("淦！")
                        .setMessage("好像更新过程中粗了什么问题哒!")
                        .setPositiveButton("查看解决方案不?", (dialog,v)->{
                            //跳转说明书
                            Intent intent=new Intent(LoginActivity.this, WebViewActivity.class);
                            intent.putExtra("Uri","http://120.79.7.230/WhuSeats_info.html#update");
                            startActivity(intent);
                            dialog.dismiss();
                        })
                        .create();
                tempDialog.show();
            }
        }

        @Override
        public void OnTaskFailed(Object... data)
        {
            downloadDialog.dismiss();
            Toast.makeText(LoginActivity.this, "下载更新失败哒！淦！",Toast.LENGTH_LONG);
        }
    };

    //弹出的确认下载对话框确定回调
    View.OnClickListener ConfimDialog_OK=(view)->{

        if(ConfimDialog.isShowing())
            ConfimDialog.dismiss();
        downloadDialog=new CustomProgressDialog(LoginActivity.this);
        downloadDialog.show();
        downloadDialog.setTitle("正在更新");
        downloadDialog.setCanceledOnTouchOutside(false); //设置该对话框不能通过点击取消
        downloadDialog.setMessage("不要催了不要催了..服务器小水管");

        //开始执行下载
        mbinder.DownLoadUpdateByHttp(downloadTaskReturn,downloadProgress);
    };

    //endregion


    //调用系统程序安装apk
    public void InstallApk(File f)
    {
        Uri fileUri;
        Intent installapk=new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)//安卓7.0之前的操作
        {
            fileUri = Uri.fromFile(f);
            installapk.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        else
        {
            //兼容android7.0 使用共享文件的形式
            fileUri= FileProvider.getUriForFile(getApplicationContext(),getPackageName(),f);
            installapk.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installapk.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        installapk.setDataAndType(fileUri,"application/vnd.android.package-archive" );
        startActivity(installapk);
        System.exit(0);
    }

    /****************
     *
     * 发起添加群流程。群号：WhuSeats(789729207) 的 key 为： mo5gO56OIB_tQ1idGlZDAItUqbPPHHDL
     * 调用 joinQQGroup(mo5gO56OIB_tQ1idGlZDAItUqbPPHHDL) 即可发起手Q客户端申请加群 WhuSeats(789729207)
     *
     * key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public boolean joinQQGroup() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + "mo5gO56OIB_tQ1idGlZDAItUqbPPHHDL"));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    //region 安卓6.0动态检查权限
    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ArrayList<String> permissions = new ArrayList<String>();

            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }


            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), ANDROID_M_PERMISSION);
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
            Log.e("LoginActivity" ,"权限获取成功");
        }
        else
        {
            Log.e("LoginActivity" ,"权限获取失败");
        }
    }
    //endregion



}
