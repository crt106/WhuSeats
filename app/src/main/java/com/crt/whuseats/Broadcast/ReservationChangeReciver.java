package com.crt.whuseats.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.Service.NetService;

//这个广播接收器用于接收当前预约状况改变的消息 然后做出相应操作
public class ReservationChangeReciver extends BroadcastReceiver
{

    public static final String BROADCASTER_RECHANGE="com.crt.whuseats.RECHANGE";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String intentinfo=intent.getStringExtra("Status");

        //如果当前没有预约
        if(intentinfo.equals("None"))
        {
//            NetService.LIB_SEATSSTATUS="None";
            //Toast.makeText(BaseActivity.ApplicationContext, "当前预约为空", Toast.LENGTH_SHORT).show();
        }

        //如果当前已有成功的预约
        if(intentinfo.equals("success"))
        {
//            NetService.LIB_SEATSSTATUS="success";
        }

    }
}
