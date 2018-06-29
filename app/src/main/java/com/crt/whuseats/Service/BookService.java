package com.crt.whuseats.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.TimeHelp;

import java.util.Calendar;
import java.util.List;

//添加定时预约座位的服务
public class BookService extends Service
{
    public static final int BOOKREQUEST=123;

    public BookService()
    {

    }

    //组件们
    private AlarmManager alarmManager;
    public static PendingIntent bookPendingIntent;


    public class BookBinder extends Binder
    {
        public void DeleteClock()
        {
            BookService.this.DeleteClock();
        }
        public void ReAddClock()
        {
            BookService.this.AddClock();
        }
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return new BookBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //本服务启动的时候直接添加定时器
        AddClock();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.e("BookService", "预约服务onDestroy" );

        try
        {
            //Toast.makeText(BaseActivity.ApplicationContext, "预约服务被停止", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //添加任务到AlarmManager
    public void AddClock()
    {
        alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE); //获取系统服务 alarm_service

        //设置PendingIntent 使其到时候发送广播
        Intent bookIntent=new Intent();
        bookIntent.setAction("com.crt.whuseats.ReadytoBook");
        //接下来的两步都是提高广播接收率
        bookIntent.setComponent(new ComponentName("com.crt.whuseats","com.crt.whuseats.Broadcast.BookReceiver"));
        bookIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        bookPendingIntent=PendingIntent.getBroadcast(this, BOOKREQUEST ,bookIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        try
        {
            //根据安卓版本的不同 把PendingIntent设置到alarmManager不同
            if(Build.VERSION.SDK_INT> Build.VERSION_CODES.M) //安卓6.0之后
            {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, TimeHelp.GetBookTime(), bookPendingIntent);
                //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, bookPendingIntent);
            }
            else if(Build.VERSION.SDK_INT> Build.VERSION_CODES.KITKAT)//安卓4.4之后
            {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, TimeHelp.GetBookTime(), bookPendingIntent);
                //alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, bookPendingIntent);
            }
            else
            {
                alarmManager.set(AlarmManager.RTC_WAKEUP, TimeHelp.GetBookTime(), bookPendingIntent);
            }
        }
        catch (Exception e)
        {
            Log.e("AddClock", e.toString());
            return;
        }

        //任务添加成功
        Log.e("BookService", "把定时任务添加到AlarmManager成功,"+TimeHelp.GetBookLeftTime()+"ms后执行");

        //同样注意这里有些时候不存在Activity的
        if(BaseActivity.AppSetting!=null)
        BaseActivity.AppSetting.BookSettingEditor.putBoolean("IsClockon", true).apply();
        //否则手动获取SharedPreferences
        else
        {
            SharedPreferences.Editor temp=this.getSharedPreferences("bookdata", Context.MODE_PRIVATE).edit();
            temp.putBoolean("IsClockon", true).apply();
        }
    }

    //删除定时器
    public void DeleteClock()
    {
        //设置PendingIntent
        Intent bookIntent=new Intent();
        bookIntent.setAction("com.crt.whuseats.ReadytoBook");
        bookPendingIntent=PendingIntent.getBroadcast(this, BOOKREQUEST ,bookIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        try
        {
            alarmManager.cancel(bookPendingIntent);
        }
        catch (Exception e)
        {
            Log.e("BookService", "取消定时器发生错误");
            Toast.makeText(this,"定时器可能已经被系统KO了呢~不用担心~" ,Toast.LENGTH_SHORT ).show();
            BaseActivity.AppSetting.BookSettingEditor.putBoolean("IsClockon", false).apply();
            return;
        }
        Log.e("BookService", "取消定时器成功");
        Toast.makeText(this,"取消定时器成功" ,Toast.LENGTH_SHORT ).show();
        BaseActivity.AppSetting.BookSettingEditor.putBoolean("IsClockon", false).apply();
    }
}
