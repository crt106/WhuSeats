package com.crt.whuseats;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.crt.whuseats.JsonModels.JsonModel_User;
import com.crt.whuseats.Model.ListenItem;
import com.crt.whuseats.Task.TaskManager;
import com.crt.whuseats.Utils.Flavors;
import com.crt.whuseats.Utils.Settings;
import com.crt.whuseats.Utils.TimeHelp;

import java.util.LinkedList;
import java.util.List;

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

    public static boolean IsApplicationVInit = false;        //当前数据信息是否已经初始化
    public static Settings AppSetting;                     //主程序设置

    //endregion

    //region 工具类
    public static TaskManager taskManager = new TaskManager();   //任务管理类

    //endregion

    //region 用户信息

    public static JsonModel_User UserInfo;                 //用户信息
    public static boolean IsLoginIN = true;                 //判断用户是否已登录进入

    //endregion

    //region 当前预约状态

    //endregion

    //region 历史预约

    //endregion

    //region 预约第二日配置

    //endregion

    //region 监听配置

    public static List<ListenItem> ListenItemList = new LinkedList<>();     //当前监听房间配置列表

    public static int MAXLOOPCOUNT = 100;                             //筛选模式下最大的筛选轮次
    public static int MAXLOOPCOUT_DEFAULT = 100;                      //筛选模式默认最大轮次

    public static int ChooseHouseID_Listen = 1;                      //选择的建筑ID
    public static int ChooseRoomID_Listen;                         //选择的房间ID
    public static String ChooseDate_Listen;                        //选择的日期


    public static int DELAY_SENDBOOK = 300;                        // 发送预约请求前后的延迟
    public static int DELAY_SENDBOOK_DEFAULT = 300;


    public static int DELAY_BEFOREFILTRATE = 1500;                 //发起筛选请求之前的延迟
    public static int DELAY_BEFOREFILTRATE_DEFAULT = 1500;

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
        ApplicationContext = getApplicationContext();

        AppSetting = new Settings();
        try
        {
            //获取当前版本号
            PackageManager m = getPackageManager();
            PACKAGENAME = getPackageName();
            PackageInfo i = m.getPackageInfo(PACKAGENAME, 0);
            VERSIONCODE = i.versionCode;
            VERSIONNAME = i.versionName;
            //获取当前渠道名称
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String Flavorname = ai.metaData.getString("BaiduMobAd_CHANNEL");
            if (Flavorname.equals("crt"))
                appFlavors = Flavors.crt;
            else if (Flavorname.equals("BaiduMarket"))
                appFlavors = Flavors.BaiduMarket;
        } catch (Exception e)
        {
            Log.e("Base_GetVersion", e.getMessage());
        }
        IsApplicationVInit = true;
    }
}
