package com.crt.whuseats.Fragment;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.Activity.HistoryActivity;
import com.crt.whuseats.Activity.MainActivity;
import com.crt.whuseats.Activity.TimeChangeActivity;
import com.crt.whuseats.Broadcast.ReservationChangeReciver;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.JsonHelps.JsonHelp;
import com.crt.whuseats.JsonHelps.JsonInfo_Base;
import com.crt.whuseats.JsonHelps.JsonInfo_Reservations;
import com.crt.whuseats.R;
import com.crt.whuseats.Service.NetService;

//主界面Fragment
public class HomeFragment extends Fragment
{



    //控件们
    public Button ButtonCheckIn;
    public Button ButtonLeave;
    public Button ButtonExtend;
    public Button ButtonStop;
    public Button ButtonHistroy;
    public TextView tv_Reshow;
    //与主活动的连接
    public MainActivity ActivityConnect;
    public HomeFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ActivityConnect=(MainActivity) getActivity();
        return inflater.inflate(R.layout.frag_home,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //region 获取可爱的控件们以及注册他们的事件
        ButtonCheckIn=(Button)getView().findViewById(R.id.btn_checkin);
        ButtonLeave=(Button)getView().findViewById(R.id.btn_leave_back);
        ButtonExtend=(Button)getView().findViewById(R.id.btn_extend);
        ButtonStop=(Button)getView().findViewById(R.id.btn_stop);
        ButtonHistroy=(Button)getView().findViewById(R.id.btn_Histroy);
        tv_Reshow=(TextView) getView().findViewById(R.id.tv_Reshow);

        //region 绑定有些东东的事件
        ButtonCheckIn.setOnClickListener((v)->{

            ActivityConnect.mbinder.CheckIn(commonReturn);
        });
        ButtonLeave.setOnClickListener((v)->{
            ActivityConnect.mbinder.Leave(commonReturn);
        });


        ButtonExtend.setOnClickListener((v)->{
            Intent timechange=new Intent(ActivityConnect, TimeChangeActivity.class);
            startActivity(timechange);
        });

        ButtonStop.setOnClickListener((v)->{
            //弹出确认对话框
            AlertDialog conformDialog=new AlertDialog.Builder(ActivityConnect)
                    .setTitle("确认")
                    .setMessage("是否结束使用")
                    .setPositiveButton("是滴是滴", (dialog,i)->{ActivityConnect.mbinder.Stop(commonReturn);})
                    .setNegativeButton("手滑了", null)
                    .create();
            conformDialog.show();
        });

        //跳转到新的Activity进行查询历史的操作咯~
        ButtonHistroy.setOnClickListener((v)->{
            Intent intent=new Intent(ActivityConnect, HistoryActivity.class);
            startActivity(intent);
                });

        //endregion
        //检查是不是新版本 然后来清除设置
        CheckIfNewVersion();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        RefreshRE();
    }

    //region 公共回调事件们~
    onTaskResultReturn commonReturn=new onTaskResultReturn()
    {
        @Override
        public void OnTaskSucceed(Object... data)
        {
            try
            {
                String datastr=(String)data[0];
                JsonInfo_Base info=new JsonInfo_Base(datastr);
                if(info.status.equals("fail"))
                {
                    Toast.makeText(ActivityConnect, info.message, Toast.LENGTH_SHORT).show();
                }
                else if(info.status.equals("success"))
                {
                    Toast.makeText(ActivityConnect, "操作完成", Toast.LENGTH_SHORT).show();
                }
                RefreshRE();
            }
            catch (Exception e)
            {
                Log.e("HomeF_commonReturn", e.getMessage());
            }
        }

        @Override
        public void OnTaskFailed(Object... data)
        {

        }
    };

    //刷新用户相关的状态
    public void RefreshRE()
    {
        ActivityConnect.mbinder.CheckReservations(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                try
                {
                    String datastr=(String)data[0];
                    JsonInfo_Reservations reinfo= JsonHelp.GetReservation(datastr);

                    //如果当前预约为空的话 刷新状态
                    if(reinfo.data.toString().equals("null"))
                    {
                        Intent broadcast=new Intent(ReservationChangeReciver.BROADCASTER_RECHANGE);
                        broadcast.putExtra("Status", "None");
                        ActivityConnect.localBroadcastManager.sendBroadcast(broadcast);
                        tv_Reshow.setText("当前没有可用预约");
                        SwitchButton(false);
                    }
                    else
                    {
                        Intent broadcast=new Intent(ReservationChangeReciver.BROADCASTER_RECHANGE);
                        broadcast.putExtra("Status", "success");
                        ActivityConnect.localBroadcastManager.sendBroadcast(broadcast);
                        tv_Reshow.setText(String.format("当前预约:\n%s\n%s----%s\n",reinfo.location,reinfo.begin,reinfo.end));
                        SwitchButton(true);
                    }
                }
                catch (Exception e)
                {
                    Log.e("HomeF_RefreshRE", e.getMessage());
                }
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        });
    }

    //改变某些Button的状况
    public void SwitchButton(boolean isenable)
    {
        if(isenable)
        {
            ButtonCheckIn.setEnabled(true);
            ButtonLeave.setEnabled(true);
            ButtonExtend.setEnabled(true);
            ButtonStop.setEnabled(true);
        }
        else
        {
            ButtonCheckIn.setEnabled(false);
            ButtonLeave.setEnabled(false);
            ButtonExtend.setEnabled(false);
            ButtonStop.setEnabled(false);
        }
    }

    //新版本检查部分数据
    public void CheckIfNewVersion()
    {
        try
        {
            int savedVersion=BaseActivity.AppSetting.ListenSetting.getInt("AppVersion", 0);
            //清除部分数据
            if(savedVersion!=BaseActivity.VERSIONCODE)
            {
                BaseActivity.AppSetting.ListenSettingEditor.remove("DELAY_NEWSEATS");
                BaseActivity.AppSetting.ListenSettingEditor.remove("DELAY_BEFORESTARTTIME");
                BaseActivity.AppSetting.ListenSettingEditor.remove("DELAY_BEFOREENDTIME");
                BaseActivity.AppSetting.ListenSettingEditor.remove("DELAY_SENDBOOK");

                NetService.DELAY_NEWSEATS= BaseActivity.AppSetting.ListenSetting.getInt("DELAY_NEWSEATS",NetService.DELAY_NEWSEATS_DEFAULT);
                NetService.DELAY_BEFORESTARTTIME =BaseActivity.AppSetting.ListenSetting.getInt("DELAY_BEFORESTARTTIME",NetService.DELAY_BEFORESTARTTIME_DEFAULT);
                NetService.DELAY_BEFOREENDTIME =BaseActivity.AppSetting.ListenSetting.getInt("DELAY_BEFOREENDTIME",NetService.DELAY_BEFOREENDTIME_DEFAULT);
                NetService.DELAY_SENDBOOK =BaseActivity.AppSetting.ListenSetting.getInt("DELAY_SENDBOOK",NetService.DELAY_SENDBOOK_DEFAULT);
                //保存当前版本号
                BaseActivity.AppSetting.ListenSettingEditor.putInt("AppVersion",BaseActivity.VERSIONCODE);

                BaseActivity.AppSetting.ListenSettingEditor.apply();
            }
        } catch (Exception e)
        {
            Log.e("HomeFragment",e.getMessage() );
        }
    }
}
