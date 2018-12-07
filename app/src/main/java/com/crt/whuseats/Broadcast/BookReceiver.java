package com.crt.whuseats.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crt.whuseats.Service.NetService;
import com.crt.whuseats.Utils.TimeHelp;

public class BookReceiver extends BroadcastReceiver
{

    private boolean isovertime;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals("com.crt.whuseats.ReadytoBook"))
        {

            Log.e("BookReceiver", "收到要求预约广播 开始判断");

            //判断当前时间是否是正确的 解决定时器的重复开启
            isovertime=TimeHelp.IsOverTime("22:55");
            if(!isovertime)
            {
                //向NetService发送要求
                Intent StartNetService=new Intent(context, NetService.class);
                StartNetService.putExtra("IsDoBook", true);
                context.startService(StartNetService);
            }

        }
    }
}
