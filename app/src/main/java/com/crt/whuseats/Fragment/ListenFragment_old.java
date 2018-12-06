package com.crt.whuseats.Fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.Activity.MainActivity;
import com.crt.whuseats.Adapter.BuildingAdapter;
import com.crt.whuseats.Adapter.RoomAdapter;
import com.crt.whuseats.Dialog.ChooseTimeDialog;
import com.crt.whuseats.Dialog.CustomProgressDialog;
import com.crt.whuseats.Dialog.SuccessDialog;
import com.crt.whuseats.Interface.onProgressReturn;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.JsonModels.JsonHelp;
import com.crt.whuseats.JsonModels.JsonModel_Fliters;
import com.crt.whuseats.JsonModels.JsonModel_HouseStats;
import com.crt.whuseats.JsonModels.JsonModel_MobileFiltrate;
import com.crt.whuseats.R;
import com.crt.whuseats.Service.NetService;
import com.crt.whuseats.Utils.TimeHelp;

import java.util.LinkedList;
import java.util.List;

/**
 * 监听界面碎片
 */
public class ListenFragment_old extends Fragment
{


    //与Activity的连接

    public MainActivity ActivityConnect;
    //region 控件
    Spinner buildingSpinner;
    Spinner DateSpinner;
    GridView RoomGrid;

    CustomProgressDialog progressDialog;

    public BuildingAdapter bdSpinnerAdapter;
    public ArrayAdapter DateSpinnerAdapter;
    public RoomAdapter roomAdapter;

    public Button buttonSubmit;
    //endregion

    //region 相应字段

    public List<JsonModel_Fliters.buildings> buildingsList=new LinkedList<>();//当前建筑列表
    public List<JsonModel_HouseStats.room> roomList=new LinkedList<>();       //当前房间情况列表
    public List<String> DateList=new LinkedList<>();                         //当前日期列表

    public static int MAXLOOPCOUNT=100;   //筛选模式下最大的筛选轮次
    public static int MAXLOOPCOUT_DEFAULT=100;

    public static int ChooseHouseID=1;   //选择的建筑ID
    public static int ChooseRoomID;    //选择的房间ID
    public static String ChooseDate;   //选择的日期

    //endregion
    public ListenFragment_old()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ActivityConnect=(MainActivity)getActivity();
        return inflater.inflate(R.layout.frag_rooms, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    //onActivityCreated里面执行相应的界面初始化以及发送查询建筑列表请求
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        super.onStart();
        //region 控件初始化
        RoomGrid=(GridView)getView().findViewById(R.id.gv_endtimes);
        buildingSpinner=(Spinner)getView().findViewById(R.id.sp_building);
        DateSpinner=(Spinner)getView().findViewById(R.id.sp_date);
        buttonSubmit=(Button)getView().findViewById(R.id.btn_submit);

        progressDialog=new CustomProgressDialog(ActivityConnect);
        //endregion
        //region相应点击事件的注册

        //"搜索按钮点下"
        buttonSubmit.setOnClickListener((View v)->
        {
            if(buildingSpinner.getAdapter()==null||buildingSpinner.getAdapter().getCount()==0)
                GetFilters();
            if(DateSpinner.getAdapter()==null||DateSpinner.getAdapter().getCount()==0)
                GetDateString();
            GetRoomGrid();
        });

        //建筑Spinner选择
        buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                JsonModel_Fliters.buildings choosebuilding=buildingsList.get(position);
                ChooseHouseID=choosebuilding.id;
                //记录用户习惯
                ActivityConnect.AppSetting.ListenSettingEditor.putInt("DefaultBuilding", ChooseHouseID);
                ActivityConnect.AppSetting.ListenSettingEditor.apply();
                //Toast.makeText(ActivityConnect, "切换建筑选择到"+ChooseHouseID, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        //日期Sppinner选择
        DateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                ChooseDate=DateList.get(position);
                //Toast.makeText(ActivityConnect, "切换日期选择到"+ChooseDate, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        //在RoomGrid上面点击房间按钮的响应
        RoomGrid.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id)->
        {
            //刷新选择房间的ID
            JsonModel_HouseStats.room chooseroom=roomList.get(position);
            ChooseRoomID=chooseroom.roomId;

            //展开设置对话框
            ChooseTimeDialog timeDialog=new ChooseTimeDialog(ActivityConnect,chooseroom.roomname);
            timeDialog.show();

            //给ChooseTime对话框上的两个按钮添加监听事件
            //这里由于设计失误 要先show再添加监听事件
            timeDialog.setButtonOKOnClickListener((View v)->
            {
                StartListen(ChooseTimeDialog.CHOOSESTARTTIME,ChooseTimeDialog.CHOOSEENDTIME);
                timeDialog.dismiss();
            });
            timeDialog.setButtonCancelOnClickListener((View v)->
            {
                timeDialog.dismiss();
            });

        });

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
        //endregion
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //界面初始化 自动执行一次查询
        if(buildingSpinner.getAdapter()==null||buildingSpinner.getAdapter().getCount()==0)
            GetFilters();
        if(DateSpinner.getAdapter()==null||DateSpinner.getAdapter().getCount()==0)
            GetDateString();
        GetRoomGrid();
    }

    //获取合乎格式的今天日期字符串 并且赋值给相应的adapter
    public void GetDateString()
    {
        //用于返回的链表
        DateList= TimeHelp.GetLegalTimeList();
        DateSpinnerAdapter=new ArrayAdapter(ActivityConnect,android.R.layout.simple_list_item_1,DateList);
        DateSpinner.setAdapter(DateSpinnerAdapter);
    }

    //通过活动发起请求总建筑布局请求
    public void GetFilters()
    {
        //真实返回
        ActivityConnect.mbinder.GetFilters(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                try
                {
                    String datastr=(String)data[0];
                    if(datastr==null||datastr.equals(""))
                        throw new Exception("返回Json为空");
                    JsonModel_Fliters info= JsonHelp.GetFliters(datastr);
                    //把接收到的数据复制给本类变量
                    buildingsList=info.buildingsList;
                    bdSpinnerAdapter=new BuildingAdapter(info.buildingsList);
                    buildingSpinner.setAdapter(bdSpinnerAdapter);

                    //读取用户习惯
                    int defaultbuilding=ActivityConnect.AppSetting.ListenSetting.getInt("DefaultBuilding", 1)-1;
                    buildingSpinner.setSelection(defaultbuilding, true);
                }
                catch (Exception e)
                {
                    Log.e("ListenFragment_old", "建筑布局获取错误"+e.getMessage());
                    Toast.makeText(ActivityConnect.getApplicationContext(),"建筑布局获取错误",Toast.LENGTH_SHORT).show();
                    Toast.makeText(ActivityConnect.getApplicationContext(),"可能性：图书馆服务器维护、账号短时间冻结",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void OnTaskFailed(Object... data)
            {
                //Toast.makeText(ActivityConnect.getApplicationContext(),"建筑布局获取错误",Toast.LENGTH_SHORT).show();
            }
        });
        //region 模拟返回


//        JsonModel_Fliters jf=new JsonModel_Fliters();
//        JsonModel_Fliters.buildings t1= jf.new buildings(1,"信息科学分馆");
//        JsonModel_Fliters.buildings t2= jf.new buildings(2,"总馆");
//        JsonModel_Fliters.buildings t3= jf.new buildings(3,"医学分馆");
//        JsonModel_Fliters.buildings t4= jf.new buildings(4,"工学分馆");
//        testlist.add(t1);
//        testlist.add(t2);
//        testlist.add(t3);
//        testlist.add(t4);
//        bdSpinnerAdapter=new BuildingAdapter(testlist);
//        buildingSpinner.setAdapter(bdSpinnerAdapter);
        //endregion
    }

    //通过活动发起建筑内房间布局
    public void GetRoomGrid()
    {
        ActivityConnect.mbinder.CheckHouseStats(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                try
                {
                    String datastr=(String)data[0];
                    if(datastr==null||datastr.equals(""))
                        throw new Exception("返回Json为空");
                    JsonModel_HouseStats info= JsonHelp.GetHouseStats(datastr);
                    //把接收到的数据复制给本类变量
                    roomList=info.roomList;
                    roomAdapter=new RoomAdapter(ActivityConnect,roomList);
                    RoomGrid.setAdapter(roomAdapter);
                }
                catch (Exception e)
                {
                    Log.e("ListenFragment_old", "房间布局获取错误"+e.getMessage());
                    Toast.makeText(ActivityConnect.getApplicationContext(),"房间布局获取错误",Toast.LENGTH_SHORT).show();
                    Toast.makeText(ActivityConnect.getApplicationContext(),"可能性：图书馆服务器维护、账号短时间冻结",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        }, ChooseHouseID);
          //region 模拟返回
//        roomList=new LinkedList<>();
//        JsonModel_HouseStats jh=new JsonModel_HouseStats();
//        JsonModel_HouseStats.room r1=jh.new room(3,"测试房间1",1,2,3,4,5,2);
//        JsonModel_HouseStats.room r2=jh.new room(5,"测试房间2",1,2,3,4,5,4);
//        JsonModel_HouseStats.room r4=jh.new room(7,"测试房间3",4,2,3,4,5,6);
//        JsonModel_HouseStats.room r5=jh.new room(9,"测试房间4",1,2,3,4,5,21);
//        JsonModel_HouseStats.room r6=jh.new room(11,"测试房间5",1,4,3,4,5,233);
//        JsonModel_HouseStats.room r7=jh.new room(17,"测试房间6",2,2,4,4,5,6);
//        JsonModel_HouseStats.room r8=jh.new room(12,"测试房间7",1,2,3,4,5,2);
//        JsonModel_HouseStats.room r9=jh.new room(14,"测试房间8",5,4,3,4,5,5);
//        JsonModel_HouseStats.room r10=jh.new room(16,"测试房间9",1,2,3,4,5,53);
//        JsonModel_HouseStats.room r11=jh.new room(16,"测试房间10",1,2,3,4,5,0);
//        JsonModel_HouseStats.room r12=jh.new room(16,"测试房间11",1,2,3,4,5,53);
//        JsonModel_HouseStats.room r13=jh.new room(16,"测试房间12",1,3,4,4,5,43);
//        JsonModel_HouseStats.room r14=jh.new room(16,"测试房间13",1,2,3,4,5,53);
//        JsonModel_HouseStats.room r15=jh.new room(16,"测试房间14",3,2,3,4,5,1);
//        roomList.add(r1);
//        roomList.add(r2);
//        roomList.add(r4);
//        roomList.add(r5);
//        roomList.add(r6);
//        roomList.add(r7);
//        roomList.add(r8);
//        roomList.add(r9);
//        roomList.add(r10);
//        roomList.add(r11);
//        roomList.add(r12);
//        roomList.add(r13);
//        roomList.add(r14);
//        roomList.add(r15);
//        roomAdapter=new RoomAdapter(ActivityConnect,roomList);
//        RoomGrid.setAdapter(roomAdapter);
        //endregion

    }

    /**************************
     * UI核心方法 开始监听
     * ************************
     */
    private int LoopCount=1; //轮次计数器
    public void StartListen(String sT,String eT)
    {
        if (!NetService.LIB_SEATSSTATUS.equals("success"))
        {
            //显示等待对话框吗?
            if(!progressDialog.isShowing())
            {
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
            }

            //region 一些回调接口的实现

            //监听座位进度回调
            onProgressReturn progressReturn=(Object... data)->
            {
                try
                {
                    int total=(Integer)data[0];
                    int now=(Integer)data[1];
                    progressDialog.setMax(total);
                    progressDialog.setProgress(now);
                }
                catch (Exception e)
                {
                    Log.e("StartListen_GetProgress", "获取当前进度时发生错误" );
                }

            };
            //检查了一轮了之后的结果的回调
            onTaskResultReturn CheckOnceReturn=new onTaskResultReturn()
            {
                @Override
                public void OnTaskSucceed(Object... data)
                {
                    Toast.makeText(ActivityConnect, "本轮监听成功", Toast.LENGTH_SHORT).show();
                    SuccessDialog successDialog=new SuccessDialog(ActivityConnect, NetService.LIB_BOOKRETURNINFO);
                    successDialog.show();
                    progressDialog.dismiss();
                }

                @Override
                public void OnTaskFailed(Object... data)
                {
                    //如果用户选择不循环监听
                    if(!ChooseTimeDialog.ISLOOP)
                    {
                        //Toast.makeText(ActivityConnect, "监听结束 未找到合适座位", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        AlertDialog listenFailed =new AlertDialog.Builder(ActivityConnect)
                                .setMessage("监听结束 未找到合适座位")
                                .create();
                        listenFailed.show();
                        LoopCount=1;
                    }
                    else
                    {

                        LoopCount++;
                        //注意这里的迭代调用哦~~~
                        if(LoopCount<MAXLOOPCOUNT)
                        StartListen(sT, eT);
                        else
                        {
                            progressDialog.dismiss();
                            AlertDialog listenFailed =new AlertDialog.Builder(ActivityConnect)
                                    .setMessage(String.format("监听次数达到上限%d 未找到合适座位",MAXLOOPCOUNT))
                                    .create();
                            listenFailed.show();
                            LoopCount=1;
                        }
                    }

                }
            };

            //endregion

            // region 弃用的遍历模式

//            if (!ChooseTimeDialog.ISFTYPE)
//            {
//                progressDialog.setTitle("开始监听啦~(遍历模式)");
//                progressDialog.setMessage("正在监听符合要求的座位(轮次"+LoopCount+")");
//                //先检查房间布局 然后根据布局文件进行监听
//                ActivityConnect.mbinder_b.CheckRoomStats(new onTaskResultReturn()
//                {
//                    @Override
//                    public void OnTaskSucceed(Object... data)
//                    {
//                        try
//                        {
//                            JsonModel_RoomLayout roomLayout = JsonHelp.GetRoomLayout((String) data[0]);
//                            //注意这里又要嵌套一层回调 没有办法 谁叫这是异步操作呢 这里是使用mbinder的开始监听房间
//                            ActivityConnect.mbinder_b.StartListenRoomOnce(roomLayout, sT, eT, ChooseDate, CheckOnceReturn, progressReturn);
//                        } catch (Exception e)
//                        {
//                            Log.e("ListenFragment_old", "接收房间布局错误" + e.getMessage());
//                            Toast.makeText(ActivityConnect, "接收房间布局错误 请重试", Toast.LENGTH_SHORT);
//                        }
//                    }
//
//                    @Override
//                    public void OnTaskFailed(Object... data)
//                    {
//
//                    }
//                }, ChooseRoomID, ChooseDate);
//            }
            //endregion
            progressDialog.setTitle("开始监听啦");
            progressDialog.setMessage("正在筛选和查找符合要求的座位(轮次"+LoopCount+")");
            //调用筛选 回调中开始监听
            ActivityConnect.mbinder.SeatsFiltrate_Mobile(ChooseDate, ChooseHouseID, ChooseRoomID, sT, eT, new onTaskResultReturn()
            {
                @Override
                public void OnTaskSucceed(Object... data)
                {
                    try
                    {
                        JsonModel_MobileFiltrate filtrateresult=JsonHelp.GetMFiltrateSeats((String)data[0]);
                        ActivityConnect.mbinder.StartListenRoomOnce(filtrateresult, sT, eT, ChooseDate, CheckOnceReturn, progressReturn);
                    }
                    catch (Exception e)
                    {
                       Log.e("ListenFragment_old", "筛选座位发生错误" + e.getMessage());
                       Toast.makeText(ActivityConnect, "筛选座位发生错误", Toast.LENGTH_SHORT);
                    }
                }

                    @Override
                    public void OnTaskFailed(Object... data)
                    {

                    }
            });
        }
        else
        {
            Toast.makeText(ActivityConnect,"当前是不是已经有成功的预约啦？有就不要点了啦" , Toast.LENGTH_LONG).show();
        }

    }
}
