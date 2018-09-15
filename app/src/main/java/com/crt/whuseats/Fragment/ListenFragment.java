package com.crt.whuseats.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Switch;
import android.widget.Toast;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.Activity.ListenActivity;
import com.crt.whuseats.Activity.LoginActivity;
import com.crt.whuseats.Activity.MainActivity;
import com.crt.whuseats.Adapter.ListenItemAdapter;
import com.crt.whuseats.Dialog.ChooseTimeDialog;
import com.crt.whuseats.Dialog.CustomProgressDialog;
import com.crt.whuseats.Dialog.SuccessDialog;
import com.crt.whuseats.Interface.ListenSwitchChangeListener;
import com.crt.whuseats.Interface.onProgressReturn;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.JsonHelps.JsonHelp;
import com.crt.whuseats.JsonHelps.JsonInfo_MobileFiltrate;
import com.crt.whuseats.Model.ListenDateType;
import com.crt.whuseats.Model.ListenItem;
import com.crt.whuseats.R;
import com.crt.whuseats.Service.NetService;
import com.crt.whuseats.Task.ResultsTask;
import com.crt.whuseats.Utils.TimeHelp;

import java.util.LinkedList;
import java.util.List;

public class ListenFragment extends Fragment
{
    //控件们
    private RecyclerView rcvListenrooms;
    private Button btnAddroom;
    private Button btnStartlistenlist;
    private Switch swIsloopListen;

    //字段们
    public static int MAXLOOPCOUNT=100;   //筛选模式下最大的筛选轮次
    public static int MAXLOOPCOUT_DEFAULT=100;

    /**
     * 监听界面进度对话框
     */
    CustomProgressDialog progressDialog;

    //与主活动的连接
    public MainActivity ActivityConnect;

    //适配器
    public ListenItemAdapter listenItemAdapter;




    public ListenFragment()
    {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        ActivityConnect=(MainActivity)getActivity();
        ListenItem.ReadFromFile(ActivityConnect);

        return inflater.inflate(R.layout.frag_listen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //region 获取控件以及绑定他们的事件们
        rcvListenrooms = (RecyclerView) getView().findViewById(R.id.rcv_listenrooms);
        btnAddroom = (Button) getView().findViewById(R.id.btn_addroom);
        btnStartlistenlist = (Button) getView().findViewById(R.id.btn_startlistenlist);
        swIsloopListen=(Switch)getView().findViewById(R.id.sw_isloopListen);
        //初始化对话框
        progressDialog =new CustomProgressDialog(ActivityConnect);

        //为这个等待对话框添加取消响应 即取消掉监听任务
        progressDialog.setOnCancelListener((dialog) ->
        {
            try
            {
                BaseActivity.taskManager.CancelTask("ListenRoomTask");
                dialog.dismiss();
                LoopCount=1;
            }
            catch (Exception e)
            {
                Log.e("PDialog_cancelTask", e.getMessage());
            }
        });

        //添加房间按钮响应事件
        btnAddroom.setOnClickListener(v->{
            //如果用户未登录
            if(!LoginActivity.IsLoginIN)
            {
                Toast.makeText(ActivityConnect, "用户未登陆", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent start=new Intent(ActivityConnect, ListenActivity.class);
            ActivityConnect.startActivity(start);
        });

        //开始监听按钮响应事件
        btnStartlistenlist.setOnClickListener(v->{
            //如果用户未登录
            if(!LoginActivity.IsLoginIN)
            {
                Toast.makeText(ActivityConnect, "用户未登陆", Toast.LENGTH_SHORT).show();
                return;
            }
            StartListen();
        });

        swIsloopListen.setChecked(ChooseTimeDialog.ISLOOP);
        swIsloopListen.setOnCheckedChangeListener((compbutton,ischecked)->{ChooseTimeDialog.ISLOOP=ischecked;});
        //endregion

        //初始化完成之后的操作
        RefreshListenList();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ListenItem.Save2File(ActivityConnect);
    }


    @Override
    public void onStart()
    {
        super.onStart();
        RefreshListenList();
    }

    //删除按钮按下操作
    ListenItemAdapter.OnDeleteButtonClick deleteButtonClick=(id->{
        ListenItem.ItemList.remove(id);
        RefreshListenList();
    });

    //开关切换响应事件接口
    ListenSwitchChangeListener switchClck=(buttonView,isChecked,item)->{

        //首先无条件改变状态
        buttonView.setChecked(isChecked);

        switch (item.dateType)
        {
            case Today:
                //如果超过了今天的时间
                if(TimeHelp.IsOverTime("22:45"))
                {
                    Toast.makeText(ActivityConnect,"该项日期为今日,当前不可用" ,Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                }
                //否则如果合法 则改变列表项的启用状态
                else {
                    item.SetEnable(isChecked);
                }
                break;

            case Tomorrow:
                //如果没有超过今天的时间
                if(!TimeHelp.IsOverTime("22:43"))
                {
                    Toast.makeText(ActivityConnect,"该项日期为明日,当前不可用" ,Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                }
                //否则如果合法 则改变列表项的启用状态
                else {
                    item.SetEnable(isChecked);
                }
                break;
        }

        //刷新列表
        RefreshListenList();
    };

    /**
     * 刷新监听列表 显示在RecyclerView上
     */
    public void RefreshListenList()
    {
        //刷新List中每个Item的启用情况 关闭非法选项
        boolean IsOvertoday=TimeHelp.IsOverTime("22:45");

        for (ListenItem i:ListenItem.ItemList)
        {
            if(i.dateType== ListenDateType.Today && IsOvertoday)
            {
                i.SetEnable(false);
                continue;
            }

            if(i.dateType==ListenDateType.Tomorrow && !IsOvertoday)
            {
                i.SetEnable(false);
                continue;
            }
        }

        listenItemAdapter=new ListenItemAdapter(ListenItem.ItemList,deleteButtonClick,switchClck);
        LinearLayoutManager layoutManager=new LinearLayoutManager(ActivityConnect);
        rcvListenrooms.setLayoutManager(layoutManager);
        rcvListenrooms.setAdapter(listenItemAdapter);
        rcvListenrooms.refreshDrawableState();
    }



    //监听座位任务回调接口
    onTaskResultReturn ListenResult=new onTaskResultReturn()
    {
        @Override
        public void OnTaskSucceed(Object... data)
        {
            Toast.makeText(ActivityConnect, "本轮监听成功", Toast.LENGTH_SHORT).show();
            SuccessDialog successDialog=new SuccessDialog(ActivityConnect, NetService.LIB_BOOKRETURNINFO);
            successDialog.show();
            progressDialog.dismiss();
            RefreshListenList();
        }

        @Override
        public void OnTaskFailed(Object... data)
        {
            progressDialog.dismiss();
            AlertDialog listenFailed =new AlertDialog.Builder(ActivityConnect)
                    .setMessage("监听结束 未找到合适座位或出现异常")
                    .create();
            listenFailed.show();
            RefreshListenList();
        }
    };


    /**
     * 核心方法 开始监听
     */
    private int LoopCount=1; //全局变量 循环次数
    private JsonInfo_MobileFiltrate mobileFiltrate; //本次移动端过滤结果

    public void StartListen()
    {

        LoopCount=1;
        //构建临时中转列表 排除掉没有启用的项目
        List<ListenItem> tmpItemList=new LinkedList<>();
        for(ListenItem i:ListenItem.ItemList)
        {
            if(i.IsEnable)
                tmpItemList.add(i);
        }

        //创建内部类
        class ListenRoomTask extends ResultsTask<Void, String, Boolean>
        {
            public ListenRoomTask(onTaskResultReturn i, String name)
            {
                super(i, name);
            }

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();

                //显示等待对话框吗?
                if(!progressDialog.isShowing())
                {
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.setTitle("开始监听啦");
                progressDialog.setMessage("正在筛选和查找符合要求的座位(轮次"+LoopCount+")");
            }

            @Override
            protected void onCancelled()
            {
                super.onCancelled();
                Log.e("ListenRoomTask","任务被取消");
            }

            @Override
            protected Boolean doInBackground(Void... voids)
            {
                //列表为空直接溜了
                if(tmpItemList.size()==0)
                    return false;

                //如果当前没有预约成功的状态
                if (!NetService.LIB_SEATSSTATUS.equals("success"))
                {

                    //开始遍历链表来查找
                    try
                    {
                        OUTLOOP:
                        //这里一定要判断一个任务有没有被取消
                        while (LoopCount<MAXLOOPCOUNT&&!isCancelled())
                        {
                            for(ListenItem i:tmpItemList)
                            {


                                if(NetService.LIB_SEATSSTATUS.equals("success"))
                                    break OUTLOOP;

                                //如果未启用该项则跳过
                                if(i.IsEnable=false)
                                    continue;

                                onProgressUpdate("0","开始监听:"+i.Location);
                                onProgressUpdate("1","正在筛选和查找符合要求的座位(轮次"+LoopCount+")");

                                //构建计算参数
                                String st=Integer.toString(i.starttime);
                                String et=Integer.toString(i.endtime);

                                //同步方式获取过滤结果
                                String Filterstr=ActivityConnect.mbinder.SeatsFiltrate_Mobile_Sync(i.getDate(),i.HouseID ,i.RoomID ,st ,et );
                                mobileFiltrate=JsonHelp.GetMFiltrateSeats(Filterstr);

                                //开始根据过滤结果监听
                                if(ActivityConnect.mbinder.StartListenRoomOnce_Sync(mobileFiltrate, st, et, i.getDate()))
                                return true;

                                LoopCount++;
                            }

                            //如果用户选择不循环
                            if(!ChooseTimeDialog.ISLOOP)
                            {
                                return false;
                            }
                        }

                        //能够运行到此处说明超过上限了
                        progressDialog.dismiss();
                        AlertDialog listenFailed =new AlertDialog.Builder(ActivityConnect)
                                .setMessage(String.format("监听次数达到上限%d 未找到合适座位",MAXLOOPCOUNT))
                                .create();
                        listenFailed.show();
                        LoopCount=1;
                        return false;
                    }
                    catch (Exception e)
                    {
                        Log.e("ListenFragment", e.getMessage());
                    }
                }

                //如果当前预约状态为成功
                else
                {
                    //这里要在主线程toast
                    btnAddroom.post(() ->
                    {
                        Toast.makeText(ActivityConnect, "当前是不是已经有成功的预约啦？有就不要点了啦", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    });
                    return null;
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values)
            {
                super.onProgressUpdate(values);
                /**
                 * 这里刷新进度对话框中的文字
                 *  规则："0"-标题 "1"-消息
                 */
                if(values[0].equals("0"))
                {
                    progressDialog.setTitle(values[1]);
                }
                else if(values[0].equals("1"))
                {
                    progressDialog.setMessage(values[1]);
                }
            }

            @Override
            protected void onPostExecute(Boolean isscuess)
            {
                super.onPostExecute(isscuess);
                if(isscuess==null)
                    return;

                if(isscuess)
                {
                    ResultReturn.OnTaskSucceed();
                }
                else
                {
                    ResultReturn.OnTaskFailed();
                }
            }
        };

        ListenRoomTask tmp=new ListenRoomTask(ListenResult,"ListenRoomTask");
        tmp.execute();
    }
}
