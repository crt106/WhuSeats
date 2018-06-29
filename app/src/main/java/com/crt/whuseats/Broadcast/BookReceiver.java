package com.crt.whuseats.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.Service.NetService;
import com.crt.whuseats.TimeHelp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookReceiver extends BroadcastReceiver
{

    private boolean isovertime;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals("com.crt.whuseats.ReadytoBook"))
        {

            Log.e("BookReceiver", "收到要求预约广播 开始判断");
//            //检查NetService服务是否还活着
//            boolean isServicealive=false;
//            ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
//            List<RunningServiceInfo> infos=am.getRunningServices(100);
//            for(RunningServiceInfo i:infos)
//            {
//                String sname=i.service.getClassName();
//                if(sname.equals("com.crt.whuseats.Service.NetService"))
//                {
//                    //这说明服务还或者
//                    isServicealive=true;
//                    break;
//                }
//                isServicealive=false;
//            }
//            //如果服务GG了 重新开启一个
//            if(!isServicealive)
//            {
//                Intent StartNetService=new Intent(context, NetService.class);
//                context.startService(StartNetService);
//            }
//
//            //发送开始发起预约请求的广播
//            Intent DoBookBc=new Intent();
//            DoBookBc.setAction("com.crt.whuseats.DoBook");
//            context.sendBroadcast(DoBookBc);

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
