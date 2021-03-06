package com.crt.whuseats.Fragment;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.crt.whuseats.Activity.AutoCreatSettingActivity;
import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.Activity.LoginActivity;
import com.crt.whuseats.Activity.MainActivity;
import com.crt.whuseats.Activity.SeatsActivity;
import com.crt.whuseats.Activity.WebViewActivity;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.JsonHelps.JsonHelp;
import com.crt.whuseats.JsonHelps.JsonInfo_Base;
import com.crt.whuseats.JsonHelps.JsonInfo_Tomorrow;
import com.crt.whuseats.R;
import com.crt.whuseats.Service.BookService;
import com.crt.whuseats.Utils.TimeHelp;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookFragment extends Fragment
{


    //region 控件们
    public MainActivity ActivityConnect;
    private TextView tvBookinfoHouse;
    private TextView tvBookinfoRoom;
    private TextView tvBookinfoTime;
    private TextView tvIsseatcomp;
    private Button btnEditbook;
    private Button btnaddBook;
    private Button btnautostart;
    private TextView tvClockinfo;
    private TextView tvClockIntroduce;






    //endregion

    //控制UI刷新的变量和任务
    public boolean istextrunning;
    //region 与BookService的绑定

    public BookService.BookBinder mbinder_b;
    public ServiceConnection serviceConnection=new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mbinder_b =(BookService.BookBinder)service;
            //尝试刷新定时器信息
            try
            {
                RefreshClockInfo();
            }
            catch (Exception e)
            {

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {

        }
    };

    //endregion

    public boolean IsClockon; //判断用户是不是已经提交过闹钟请求
    Textrefresh textrefreshTask;



    public BookFragment()
    {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ActivityConnect=(MainActivity)getActivity();
        Intent bookService=new Intent(ActivityConnect, BookService.class);
        ActivityConnect.bindService(bookService, serviceConnection, Context.BIND_AUTO_CREATE);
        return inflater.inflate(R.layout.frag_book, container,false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //region 绑定控件和事件
        tvBookinfoHouse = (TextView) getView().findViewById(R.id.tv_bookinfo_house);
        tvBookinfoRoom = (TextView) getView().findViewById(R.id.tv_bookinfo_room);
        tvBookinfoTime = (TextView) getView().findViewById(R.id.tv_bookinfo_time);
        tvIsseatcomp=(TextView)getView().findViewById(R.id.tv_isseatcomp);
        btnEditbook = (Button) getView().findViewById(R.id.btn_editbook);
        btnaddBook =(Button)getView().findViewById(R.id.btn_AddBook);
        btnautostart=(Button)getView().findViewById(R.id.btn_autostart);
        tvClockinfo = (TextView) getView().findViewById(R.id.tv_clockinfo);
        tvClockIntroduce = (TextView) getView().findViewById(R.id.tv_clock_introduce);
        //绑定事件们

        tvIsseatcomp.setVisibility(View.INVISIBLE);

        //跳转说明书
        tvClockIntroduce.setOnClickListener(v->{
            Intent intent=new Intent(ActivityConnect, WebViewActivity.class);
            intent.putExtra("Uri","http://120.79.7.230/WhuSeats_info.html#book");
            startActivity(intent);
        });
        //编辑绑定的座位按钮
        btnEditbook.setOnClickListener((v)->{
            //如果用户未登录
            if(!LoginActivity.IsLoginIN)
            {
                Toast.makeText(ActivityConnect, "用户未登陆", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent=new Intent(ActivityConnect, SeatsActivity.class);
            startActivity(intent);
        });
        //启动自启动页面
        btnautostart.setOnClickListener(v->{
            Intent intent=new Intent(ActivityConnect, AutoCreatSettingActivity.class);
            startActivity(intent);
        });

//        //云预约开关的事件
//        swUsecloud.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked)->{
//            Toast.makeText(ActivityConnect,"敬请期待~" , Toast.LENGTH_SHORT).show();
//            buttonView.setChecked(false);
//        });

        //endregion

        //创建的时候尝试各类初始化
        try
        {
            RefreshClockInfo();
            InitBookInfo();
            CheckIfCompetition();
        }
        catch (Exception e)
        {

        }

    }

    @Override
    public void onStart()
    {
        super.onStart();
        RefreshClockInfo();
        InitBookInfo();
        CheckIfCompetition();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        EndClockTextrefresh();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EndClockTextrefresh();
    }

    //初始化用户已经设置好的预约信息 显示到TextView中
    public void InitBookInfo()
    {
        tvBookinfoHouse.setText(BaseActivity.AppSetting.BookSetting.getString("ChooseHouseName", ""));
        //拼接座位信息字符串
        StringBuilder sb1=new StringBuilder();
        sb1.append("("+BaseActivity.AppSetting.BookSetting.getInt("ChooseSeatID", 0)+")");
        sb1.append(BaseActivity.AppSetting.BookSetting.getString("ChooseRoomName", ""));
        sb1.append(BaseActivity.AppSetting.BookSetting.getString("ChooseSeatName", ""));


        tvBookinfoRoom.setText(sb1.toString());

        //拼接时间信息字符串
        StringBuilder sb2=new StringBuilder();
        String v1=BaseActivity.AppSetting.ListenSetting.getString("startTimeValue", "");
        sb2.append(v1);
        sb2.append("----");
        String v2=BaseActivity.AppSetting.ListenSetting.getString("endTimeValue", "");
        sb2.append(v2);
        tvBookinfoTime.setText(sb2.toString());
    }


    //刷新当前的定时器状态
    public void RefreshClockInfo()
    {
        //检查是否已经有保存的Clock
        IsClockon=BaseActivity.AppSetting.BookSetting.getBoolean("IsClockon", false);

        //根据当前的不同状况对UI做出不同指令
        if(!IsClockon)
        {
            tvClockinfo.setText("当前没有可用定时器");
            btnaddBook.setText("添加定时器");
            btnaddBook.setBackgroundResource(R.drawable.green_btn_selector);

            if(istextrunning)
            EndClockTextrefresh();

            //设置添加预约按钮
            btnaddBook.setOnClickListener(v->{
                if(!TimeHelp.IsOverTime("22:45")&&LoginActivity.IsLoginIN)
                {
                    //注意这里是直接startService而不是Bind
                    Intent bookService=new Intent(ActivityConnect, BookService.class);
                    ActivityConnect.startService(bookService);
                    BaseActivity.AppSetting.BookSettingEditor.putBoolean("IsClockon", true).apply();

                    //region 向服务器添加预约信息
                    int seatID=BaseActivity.AppSetting.BookSetting.getInt("ChooseSeatID",-1 );
                    if(seatID!=-1)
                    ActivityConnect.mbinder.AddTomorrowInfo(seatID, LoginActivity.USERNAME, new onTaskResultReturn()
                    {
                        @Override
                        public void OnTaskSucceed(Object... data)
                        {
                            JsonInfo_Base info=new JsonInfo_Base(data[0].toString());
                            String message= info.message;
                            Log.e("book_addtomoinfo", message);
                        }

                        @Override
                        public void OnTaskFailed(Object... data)
                        {

                        }
                    });
                    //endregion

                    RefreshClockInfo();
                }
                else
                {
                    Toast.makeText(ActivityConnect, "当前添加预约信息不可用",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            //重新刷新这个定时器
            if(mbinder_b !=null)
            mbinder_b.ReAddClock();

            btnaddBook.setText("取消定时器");
            btnaddBook.setBackgroundResource(R.drawable.red_btn_selector);
            //开始刷新文字
            StartClockTextrefresh();

            //设置取消预约按钮
            btnaddBook.setOnClickListener(v->{
                mbinder_b.DeleteClock();

                //region 向服务器删除预约信息
                if(LoginActivity.IsLoginIN)
                {
                    int seatID=BaseActivity.AppSetting.BookSetting.getInt("ChooseSeatID",-1 );
                    if(seatID!=-1)
                        ActivityConnect.mbinder.DeleteTomorrowInfo(seatID, LoginActivity.USERNAME, new onTaskResultReturn()
                        {
                            @Override
                            public void OnTaskSucceed(Object... data)
                            {
                                JsonInfo_Base info=new JsonInfo_Base(data[0].toString());
                                String message= info.message;
                                Log.e("book_deletetomoinfo", message);
                            }

                            @Override
                            public void OnTaskFailed(Object... data)
                            {

                            }
                        });
                }
                //endregion

                RefreshClockInfo();
            });
        }
    }

    //预约倒计时文字刷新任务
    class Textrefresh extends AsyncTask<Void,Long,Void>
    {

        @Override
        protected Void doInBackground(Void... voids)
        {
            while (istextrunning&!isCancelled())
            {
                try
                {
                    publishProgress(TimeHelp.GetBookLeftTime());
                    Thread.sleep(300);
                }
                catch (InterruptedException e)
                {
                    Log.e("BookFrag", e.toString());
                    return null;
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values)
        {
            super.onProgressUpdate(values);

            String hms = formatLongToTimeStr(values[0]);
            tvClockinfo.setText("当前已添加定时器\n"+ hms+"后执行");
        }

        @Override
        protected void onCancelled(Void aVoid)
        {
            super.onCancelled(aVoid);
            return;
        }
    }

    //region 时间字符串获取

    /**
    *毫秒转换为时分秒
    */
    private static String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;

        second = l.intValue() / 1000;

        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        return (getTwoLength(hour) + ":" + getTwoLength(minute)  + ":"  + getTwoLength(second));
    }

    private static String getTwoLength(final int data) {
        if(data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }
    //endregion


    //开启预约时间间隔文字刷新
    public void StartClockTextrefresh()
    {
       istextrunning=true;
       textrefreshTask=new Textrefresh();
       //给这个任务单独创建一个线程池
        BlockingQueue<Runnable> queue = new LinkedBlockingDeque<Runnable>();
        ThreadPoolExecutor exec = new ThreadPoolExecutor(10, 10, 1, TimeUnit.DAYS, queue);
        textrefreshTask.executeOnExecutor(exec);
    }

    //结束预约时间间隔文字刷新
    public void EndClockTextrefresh()
    {
        istextrunning=false;
        if(textrefreshTask!=null)
        {
           textrefreshTask.cancel(true);
            Log.e("BookFragment", "成功暂停计时文字刷新");
           textrefreshTask=null;
        }
    }


    /**
     获取第二天座位预约信息并且判断当前座位是不是存在竞争
     并且根据结果来判断是否显示提示文字
     */
    public void CheckIfCompetition()
    {
        int seatID=BaseActivity.AppSetting.BookSetting.getInt("ChooseSeatID", 0);
        if(seatID==0)
        {
            tvIsseatcomp.setVisibility(View.INVISIBLE);
            return;
        }

        //否则 开始远端获取数据
        ActivityConnect.mbinder.GetTomorrowInfo(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                try
                {
                    String str=(String)data[0];
                    JsonInfo_Tomorrow JsonClass= JsonHelp.GetTomorrowInfo(str);
                    //构建信息链表
                    SeatsActivity.tomoinfolist.clear();
                    for ( Map.Entry<Integer, Integer> ob : JsonClass.tomoInfo.entrySet() )
                    {
                        //如果大于等于2才出现竞争状况
                        if(ob.getValue()>=2)
                        SeatsActivity.tomoinfolist.add(ob.getKey());
                    }
                    //构建完成
                    if(SeatsActivity.tomoinfolist.contains(seatID))
                    {
                        tvIsseatcomp.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        tvIsseatcomp.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e)
                {
                    Log.e("SeatsActivity","获取明日预约状况信息错误"+e.getLocalizedMessage());
                }
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        });
    }
}
