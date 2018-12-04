package com.crt.whuseats;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.crt.whuseats.Task.TaskManager;
import com.crt.whuseats.Utils.Flavors;
import com.crt.whuseats.Utils.Settings;
import com.crt.whuseats.Utils.TimeHelp;

/**
 * 存储整个程序的相关信息字段的类
 * 将原来分散到各个类的信息统一
 * Created by crt 2018-12-4
 */
public class ApplicationV extends Application
{

    //region 应用版号等信息

    public static String PACKAGENAME;          //包名
    public static int VERSIONCODE;             //版本号
    public static String VERSIONNAME;          //版本名
    public static Flavors appFlavors;          //渠道名称

    //endregion

    //region Android组件
    public static Context ApplicationContext;  //全局Context

    //endregion

    //region 全局控制字段

    public static boolean IsApplicationVInit=false;        //当前数据信息是否已经初始化
    public static Settings AppSetting;                     //主程序设置
    //endregion

    //region 工具类
    public static TaskManager taskManager=new TaskManager();   //任务管理类
    public static TimeHelp timeHelp;                           //时间转换工具
    //endregion

    //region 用户信息

    //endregion

    //region 当前预约状态

    //endregion







    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
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
            //获取当前渠道名称
            ApplicationInfo ai= getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String Flavorname=ai.metaData.getString("BaiduMobAd_CHANNEL");
            if(Flavorname.equals("crt"))
                appFlavors=Flavors.crt;
            else if(Flavorname.equals("BaiduMarket"))
                appFlavors=Flavors.BaiduMarket;
        }
        catch (Exception e)
        {
            Log.e("Base_GetVersion", e.getMessage());
        }
        IsApplicationVInit=true;
    }
}
