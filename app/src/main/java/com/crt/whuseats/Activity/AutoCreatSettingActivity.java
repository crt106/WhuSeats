package com.crt.whuseats.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

//用来跳转至自启动权限界面的Activity
public class AutoCreatSettingActivity extends AppCompatActivity
{


    //获取手机类型
    private static String getMobileType() {
        return Build.MANUFACTURER;
    }

    //跳转至授权页面
    public  void jumpStartInterface(Context context)
    {
        Intent intent = new Intent();
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("AutoCreat", "******************当前手机型号为：" + getMobileType());

            ComponentName componentName = null;
            if (getMobileType().equals("Xiaomi"))
            {
                // 红米Note4测试通过
                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
            }
            else if (getMobileType().equals("Letv"))
            { // 乐视2测试通过
                intent.setAction("com.letv.android.permissionautoboot");
            }
            else if (getMobileType().equals("samsung"))
            { // 三星Note5测试通过
                //componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.ram.AutoRunActivity");
                //componentName = ComponentName.unflattenFromString("com.samsung.android.sm/.ui.ram.RamActivity");// Permission Denial not exported from uid 1000，不允许被其他程序调用
                componentName = ComponentName.unflattenFromString("com.samsung.android.sm/.app.dashboard.SmartManagerDashBoardActivity");
            }
            else if (getMobileType().equals("HUAWEI"))
            { // 华为测试通过
                //componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");//锁屏清理
                componentName = ComponentName.unflattenFromString("com.huawei.systemmanager/.startupmgr.ui.StartupNormalAppListActivity");//跳自启动管理
                //SettingOverlayView.show(context);
            }
            else if (getMobileType().equals("vivo"))
            {
                // VIVO
                componentName = new ComponentName("com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
            }
            else if (getMobileType().equals("Meizu"))
            {
                //万恶的魅族
                //componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity");//跳转到手机管家
                componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.SmartBGActivity");//跳转到后台管理页面
            }
            else if (getMobileType().equals("OPPO"))
            {
                // OPPO R8205测试通过
                componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
            }
            else if (getMobileType().equals("ulong"))
            {
                // 360手机 未测试
                componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity");
            }
            else if(getMobileType().equals("samsung"))
            {
                componentName = new ComponentName("com.samsung.android.sm",
                            "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
            }
            else
            {
                // 将用户引导到系统设置页面
                if (Build.VERSION.SDK_INT >= 9) {
                    Log.e("HLQ_Struggle", "APPLICATION_DETAILS_SETTINGS");
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                    intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                }
                Toast.makeText(context, "抱歉,您手机型号我不知道怎么打开自启动界面", Toast.LENGTH_SHORT).show();
            }

            intent.setComponent(componentName);
            context.startActivity(intent);



        }
        catch (Exception e)
        {
            //抛出异常就直接打开设置页面
            Log.e("HLQ_Struggle", e.getLocalizedMessage());
            intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
            Toast.makeText(context, "抱歉,您手机型号我不知道怎么打开自启动界面", Toast.LENGTH_SHORT).show();
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        jumpStartInterface(AutoCreatSettingActivity.this);
    }
}
