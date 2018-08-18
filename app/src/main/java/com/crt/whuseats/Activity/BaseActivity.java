package com.crt.whuseats.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baidu.mobstat.StatService;
import com.crt.whuseats.Broadcast.ReservationChangeReciver;
import com.crt.whuseats.Service.NetService;
import com.crt.whuseats.Utils.Settings;
import com.crt.whuseats.Task.TaskManager;
import com.crt.whuseats.Utils.TimeHelp;

public class BaseActivity extends AppCompatActivity
{


    public static String PACKAGENAME;
    public static int VERSIONCODE;
    public static String VERSIONNAME;
    public static Settings AppSetting;  //主程序设置
    public static TaskManager taskManager=new TaskManager();
    public static TimeHelp timeHelp;    //时间转换
    public static Context ApplicationContext;  //全局Context
    protected ReservationChangeReciver localreceiver;
    public LocalBroadcastManager localBroadcastManager;  //广播发送者
    //region 服务

    public NetService.NetBinder mbinder;
    public ServiceConnection serviceConnection=new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mbinder=(NetService.NetBinder)service;
            //触发绑定服务完成生命周期
            onServiceBinded();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {

        }
    };
    //endregion

    //region 生命周期
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent serviceintent=new Intent(BaseActivity.this,NetService.class);
        ApplicationContext=getApplicationContext();
        timeHelp=new TimeHelp();
        AppSetting=new Settings();
        try
        {
            //获取当前版本号

            PackageManager m= getPackageManager();
            PACKAGENAME=getPackageName();
            PackageInfo i=m.getPackageInfo(PACKAGENAME, 0);
            VERSIONCODE=i.versionCode;
            VERSIONNAME=i.versionName;
        }
        catch (Exception e)
        {
            Log.e("Base_GetVersion", e.getMessage());
        }

        // region 注册广播接收机
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ReservationChangeReciver.BROADCASTER_RECHANGE);
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localreceiver=new ReservationChangeReciver();
        localBroadcastManager.registerReceiver(localreceiver,intentFilter );
        //endregion

        //在基类中绑定服务
        bindService(serviceintent,serviceConnection,BIND_AUTO_CREATE );
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbindService(serviceConnection);
        //解除广播注册
        localBroadcastManager.unregisterReceiver(localreceiver);
    }

    /**
     * 生命周期-当服务绑定完成的时候
     */
    protected void onServiceBinded()
    {

    }
    //endregion
}
