package com.crt.whuseats.Utils;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.Activity.WebViewActivity;
import com.crt.whuseats.Dialog.CustomProgressDialog;
import com.crt.whuseats.Dialog.UpdateDialog;
import com.crt.whuseats.Interface.onProgressReturn;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.Service.NetService;

import java.io.File;

//下载更新的帮助类 和
public class UpdateHelp
{
    BaseActivity ActivityConnect;                //与主活动的连接
    public UpdateDialog ConfimDialog;            //crt渠道更新对话框
    public CustomProgressDialog downloadDialog;  //crt渠道下载对话框
    private boolean IsneedUpdate;                //是否需要更新
    boolean IgnoreUnforce;                       //是否忽略非强制更新 区分LoginActivity和SettingActivity发起的更新请求

    //构造函数
    public UpdateHelp(BaseActivity context,boolean isignore)
    {
        ActivityConnect=context;
        IgnoreUnforce=isignore;
    }

    //region 回调方法

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
                AlertDialog tempDialog=new AlertDialog.Builder(ActivityConnect)
                        .setTitle("淦！")
                        .setMessage("好像更新过程中粗了什么问题哒!")
                        .setPositiveButton("查看解决方案不?", (dialog,v)->{
                            //跳转说明书
                            Intent intent=new Intent(ActivityConnect, WebViewActivity.class);
                            intent.putExtra("Uri","http://120.79.7.230/WhuSeats_info.html#update");
                            ActivityConnect.startActivity(intent);
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
            Toast.makeText(ActivityConnect, "下载更新失败哒！淦！",Toast.LENGTH_LONG);
        }
    };

    //弹出的确认下载对话框确定回调
    View.OnClickListener ConfimDialog_OK=(view)->{

        if(ConfimDialog.isShowing())
            ConfimDialog.dismiss();
        downloadDialog=new CustomProgressDialog(ActivityConnect);
        downloadDialog.show();
        downloadDialog.setTitle("正在更新");
        downloadDialog.setCanceledOnTouchOutside(false); //设置该对话框不能通过点击取消
        downloadDialog.setMessage("不要催了不要催了..服务器小水管");

        //开始执行下载
        ActivityConnect.mbinder.DownLoadUpdateByHttp(downloadTaskReturn,downloadProgress);
    };

    //百度UI更新接口
    UICheckUpdateCallback uiCheckUpdateCallback=new UICheckUpdateCallback()
    {
        @Override
        public void onNoUpdateFound()
        {
            //判断是登陆界面还是设置界面发起的
            if(!IgnoreUnforce)
            {
                Toast.makeText(ActivityConnect, "当前没有可用版本",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCheckComplete()
        {

        }
    };
    //endregion

    /**
     *
     * 是否忽略非强制更新 即非强制更新没有必要弹出更新对话框
     */
    public boolean CheckUpdate()
    {
        try
        {
            switch (BaseActivity.appFlavors)
            {
                case crt:
                    //本地渠道更新
                    Thread t=new Thread(()->{IsneedUpdate=ActivityConnect.mbinder.CheckUpdate_IsShowDialog(false);});
                    t.start();
                    t.join();

                    //如果忽略掉非强制更新
                    if(IgnoreUnforce)
                    {
                        if(IsneedUpdate&&NetService.ISFORCEUPDATE)
                        {
                            //显示确定对话框
                            ConfimDialog=new UpdateDialog(ActivityConnect, NetService.ISFORCEUPDATE);
                            ConfimDialog.show();
                            ConfimDialog.setButtonOKListenner(ConfimDialog_OK);
                            return true;
                        }
                        return false;
                    }
                    else if(IsneedUpdate)
                    {
                        //显示确定对话框
                        ConfimDialog=new UpdateDialog(ActivityConnect, NetService.ISFORCEUPDATE);
                        ConfimDialog.show();
                        ConfimDialog.setButtonOKListenner(ConfimDialog_OK);
                        return true;
                    }
                    return false;


                case BaiduMarket:
                    //百度渠道更新
                    BDAutoUpdateSDK.uiUpdateAction(ActivityConnect, uiCheckUpdateCallback, false);
                    break;
            }
        }
        catch (Exception e)
        {
            Log.e("Checkupdate", e.toString());
            return false;
        }
        return true;
    }

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
            fileUri= FileProvider.getUriForFile(BaseActivity.ApplicationContext,BaseActivity.PACKAGENAME,f);
            installapk.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installapk.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        installapk.setDataAndType(fileUri,"application/vnd.android.package-archive" );
        ActivityConnect.startActivity(installapk);
        System.exit(0);
    }

}
