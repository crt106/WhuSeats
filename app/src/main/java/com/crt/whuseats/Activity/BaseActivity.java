package com.crt.whuseats.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
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
import com.crt.whuseats.Utils.Flavors;
import com.crt.whuseats.Utils.Settings;
import com.crt.whuseats.Task.TaskManager;
import com.crt.whuseats.Utils.TimeHelp;
import com.wang.avi.AVLoadingIndicatorView;

public class BaseActivity extends AppCompatActivity
{

    protected ReservationChangeReciver localreceiver;
    public LocalBroadcastManager localBroadcastManager;  //广播发送者


    //region 生命周期
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // region 注册广播接收机
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ReservationChangeReciver.BROADCASTER_RECHANGE);
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localreceiver=new ReservationChangeReciver();
        localBroadcastManager.registerReceiver(localreceiver,intentFilter );
        //endregion

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
        //解除广播注册
        localBroadcastManager.unregisterReceiver(localreceiver);
    }

    //endregion
}
