package com.crt.whuseats.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.crt.whuseats.Adapter.TimeManageAdapter;
import com.crt.whuseats.Dialog.SuccessDialog;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.JsonHelps.JsonHelp;
import com.crt.whuseats.JsonHelps.JsonInfo_Base;
import com.crt.whuseats.JsonHelps.JsonInfo_Reservations;
import com.crt.whuseats.JsonHelps.JsonInfo_SeatTime_Start;
import com.crt.whuseats.R;
import com.crt.whuseats.Service.NetService;

import java.util.LinkedList;
import java.util.List;

public class TimeChangeActivity extends BaseActivity
{

    //region 字段
    public String choosedStartStr; //选择的起始和终止时间
    public String choosedEndStr;
    public int NowSeatsId;   //当前座位ID
    public int ReservationId; //当前预约的编号
    public String UserStart;

    //利用TimeHelp转换时间字符串到ID
    public int getUserStartID()
    {
        return timeHelp.Value2ID.get(UserStart);
    }

    public String UserEnd;

    //利用TimeHelp转换时间字符串到ID
    public int getUserEndID()
    {
        return timeHelp.Value2ID.get(UserEnd);
    }
    private RecyclerView rvTimeList;
    private TextView tvTimechangeSeatsinfo;
    private TextView tvTimemaStart;
    private TextView tvTimemaEnd;
    private Button btnTimemaSwitch;



    //endregion

    //时间框按钮点击事件回调
    public TimeManageAdapter.OnItemClick TimeChangeClick=(timeStr,v) -> {

        //显示PopupMenu
        PopupMenu popupMenu=new PopupMenu(TimeChangeActivity.this,v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_timemanage,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener((item -> {

            //根据按下的不同菜单做出不同动作
            switch (item.getItemId())
            {
                //按下设置开始时间
                case R.id.menu_tm_setStart:
                    choosedStartStr=timeStr;
                    tvTimemaStart.setText(choosedStartStr);
                    break;
                case R.id.menu_tm_setEnd:
                    choosedEndStr=timeStr;
                    tvTimemaEnd.setText(choosedEndStr);
                    break;
            }
            return true;
        }));

    };

    //切换时间按钮事件
    public View.OnClickListener BtnSwitchClick=v->{
        //第一步 取消当前预约
        mbinder.Cancel(Integer.toString(ReservationId), new onTaskResultReturn()
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
                        Toast.makeText(TimeChangeActivity.this, info.message, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //成功取消预约
                    else if(info.status.equals("success"))
                    {
                        //第二步 重新预约(同步)

                        //这个操作保证app状态存活 所以直接改NetService状态
                        NetService.LIB_SEATSSTATUS="None";

                        //设置子线程
                        Thread t=new Thread(()->{
                            String st=Integer.toString(timeHelp.Value2ID.get(choosedStartStr));
                            String et=Integer.toString(timeHelp.Value2ID.get(choosedEndStr));
                            mbinder.FreeBookSync(NowSeatsId, st, et, timeHelp.GetTodayStr());
                        });
                        t.start();
                        t.join();
                        if(NetService.LIB_SEATSSTATUS.equals("success"))
                        {
                            //显示对话框
                            SuccessDialog successDialog=new SuccessDialog(TimeChangeActivity.this, NetService.LIB_BOOKRETURNINFO);
                            successDialog.show();
                            successDialog.SetTitle("切换时间完成");
                        }
                        else
                        {
                            Toast.makeText(TimeChangeActivity.this, "切换失败！请检查", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                catch (Exception e)
                {
                    Log.e("HistoryActivity", e.getMessage());
                }
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        });
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timemanage);
        //获取控件们
        rvTimeList = (RecyclerView) findViewById(R.id.rv_TimeList);
        tvTimechangeSeatsinfo = (TextView) findViewById(R.id.tv_timechange_seatsinfo);
        tvTimemaStart = (TextView) findViewById(R.id.tv_timema_start);
        tvTimemaEnd = (TextView) findViewById(R.id.tv_timema_end);
        btnTimemaSwitch = (Button) findViewById(R.id.btn_timema_switch);
        //绑定切换时间按钮事件
        btnTimemaSwitch.setOnClickListener(BtnSwitchClick);
    }

    @Override
    protected void onServiceBinded()
    {
        super.onServiceBinded();
        RefreshRE();
    }

    //刷新用户当前预约状况
    public void RefreshRE()
    {
        mbinder.CheckReservations(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                try
                {
                    String datastr = (String) data[0];
                    JsonInfo_Reservations reinfo = JsonHelp.GetReservation(datastr);
                    //如果当前预约为空的话 刷新状态
                    if (reinfo.data.toString().equals("null"))
                    {
                        finish();
                        Toast.makeText(BaseActivity.ApplicationContext, "发生错误,当前预约为空", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //返回消息成功 刷新字段值
                        tvTimechangeSeatsinfo.setText(reinfo.location);
                        NowSeatsId=reinfo.SeatId;
                        ReservationId=reinfo.id;
                        //获取开始和结束事件并且赋值给TextView
                        UserStart=reinfo.begin;
                        choosedStartStr=reinfo.begin;
                        tvTimemaStart.setText(UserStart);

                        UserEnd=reinfo.end;
                        choosedEndStr=reinfo.end;
                        tvTimemaEnd.setText(UserEnd);

                        //刷新时间表
                        RefreshTimeList();
                    }
                }
                catch (Exception e)
                {
                    Log.e("TimeChangeActivity", e.getMessage());
                }
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        });
    }

    //刷新时间列表,将RecyclerView实例化
    public void RefreshTimeList()
    {
        try
        {
            mbinder.GetSeatStartTime(NowSeatsId, timeHelp.GetTodayStr(), new onTaskResultReturn()
            {
                @Override
                public void OnTaskSucceed(Object... data)
                {
                    JsonInfo_SeatTime_Start seatTime_start=JsonHelp.GetSeatStartTime((String)data[0]);
                    List<String> timelist=new LinkedList<>();
                    /**建立合乎要求的时间链表
                     8:00_false
                     9:00_free
                     11:30_user
                     */
                    for (int i=480;i<=1350;i+=30)
                    {
                        String istr=Integer.toString(i);
                        String i2Value=timeHelp.ID2Value.get(i);
                        if(i>=getUserStartID()&&i<=getUserEndID())
                        {
                            timelist.add(i2Value+"_user");
                            continue;
                        }
                        else if(seatTime_start.StartTimeList.contains(istr))
                        {
                            timelist.add(i2Value+"_free");
                            continue;
                        }
                        else
                        {
                            timelist.add(i2Value+"_false");
                        }
                    }
                    //构建RecyclerView和adapter

                    LinearLayoutManager layoutManager=new LinearLayoutManager(TimeChangeActivity.this);
                    rvTimeList.setLayoutManager(layoutManager);
                    TimeManageAdapter timeManageAdapter=new TimeManageAdapter(timelist,TimeChangeClick);
                    Log.e("TimeChangeAc", "Adapter创建完毕");
                    rvTimeList.setAdapter(timeManageAdapter);
                    //刷新一下
                    rvTimeList.refreshDrawableState();
                }

                @Override
                public void OnTaskFailed(Object... data)
                {

                }
            });
        }
        catch (Exception e)
        {
            Log.e("TimeChangeActivity", e.getMessage());
            Toast.makeText(TimeChangeActivity.this, "刷新可用时间列表时发生错误", Toast.LENGTH_SHORT).show();
        }
    }


}
