package com.crt.whuseats.Activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.crt.whuseats.Adapter.BuildingAdapter;
import com.crt.whuseats.Adapter.RoomAdapter;
import com.crt.whuseats.Dialog.ChooseTimeDialog;
import com.crt.whuseats.Dialog.LoadingDialog;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.JsonModels.JsonHelp;
import com.crt.whuseats.JsonModels.JsonModel_Fliters;
import com.crt.whuseats.JsonModels.JsonModel_HouseStats;
import com.crt.whuseats.Model.ListenDateType;
import com.crt.whuseats.Model.ListenItem;
import com.crt.whuseats.R;
import com.crt.whuseats.Utils.TimeHelp;

import java.util.LinkedList;
import java.util.List;

/**
 * 监听界面碎片
 */
public class ListenActivity extends BaseActivity
{
//
//    //region 控件
//    Spinner buildingSpinner;
//    Spinner DateSpinner;
//    GridView RoomGrid;
//
//    public BuildingAdapter bdSpinnerAdapter;    //建筑Adapter
//    public ArrayAdapter DateSpinnerAdapter;
//    public RoomAdapter roomAdapter;             //房间Adapter
//
//    public Button buttonSubmit;
//    //endregion
//
//    //region 相应字段
//
//    public List<JsonModel_Fliters.buildings> buildingsList=new LinkedList<>();//当前建筑列表
//    public List<JsonModel_HouseStats.room> roomList=new LinkedList<>();       //当前房间情况列表
//    public List<String> DateList=new LinkedList<>();                         //当前日期列表
//
//
//
//    //endregion
//    public ListenActivity()
//    {
//        // Required empty public constructor
//    }
//
//
//    //onActivityCreated里面执行相应的界面初始化以及发送查询建筑列表请求
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.frag_rooms);
//
//        //region 控件初始化
//        RoomGrid=(GridView)findViewById(R.id.gv_endtimes);
//        buildingSpinner=(Spinner)findViewById(R.id.sp_building);
//        DateSpinner=(Spinner)findViewById(R.id.sp_date);
//        buttonSubmit=(Button)findViewById(R.id.btn_submit);
//
//
//
//        //endregion
//        //region相应点击事件的注册
//
//        //"搜索按钮点下"
//        buttonSubmit.setOnClickListener((View v)->
//        {
//            if(buildingSpinner.getAdapter()==null||buildingSpinner.getAdapter().getCount()==0)
//                GetFilters();
//            if(DateSpinner.getAdapter()==null||DateSpinner.getAdapter().getCount()==0)
//                GetDateString();
//            GetRoomGrid();
//        });
//
//        //建筑Spinner选择
//        buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
//        {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//            {
//                JsonModel_Fliters.buildings choosebuilding=buildingsList.get(position);
//                ChooseHouseID=choosebuilding.id;
//                //记录用户习惯
//                AppSetting.ListenSettingEditor.putInt("DefaultBuilding", ChooseHouseID);
//                AppSetting.ListenSettingEditor.apply();
//                //Toast.makeText(ActivityConnect, "切换建筑选择到"+ChooseHouseID, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent)
//            {
//
//            }
//        });
//
//        //日期Sppinner选择
//        DateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
//        {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//            {
//
//                ChooseDate=DateList.get(position);
//                //Toast.makeText(ActivityConnect, "切换日期选择到"+ChooseDate, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent)
//            {
//
//            }
//        });
//
//        //在RoomGrid上面点击房间按钮的响应
//        RoomGrid.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id)->
//        {
//            //刷新选择房间的ID
//            JsonModel_HouseStats.room chooseroom=roomList.get(position);
//            ChooseRoomID=chooseroom.roomId;
//
//            //展开设置对话框
//            ChooseTimeDialog timeDialog=new ChooseTimeDialog(this,chooseroom.roomname);
//            timeDialog.show();
//
//            //给ChooseTime对话框上的两个按钮添加监听事件
//            //这里由于设计失误 要先show再添加监听事件
//
//            timeDialog.setButtonOKOnClickListener((View v)->
//            {
//                //添加待监听Item到ItemList
//
//                //region 构建相关变量
//                String housename=bdSpinnerAdapter.GetBuildingname(ChooseHouseID);
//                String roomname=roomAdapter.GetRoomname(ChooseRoomID);
//                ListenDateType dateType=ListenDateType.Today;
//
//                if(ChooseDate.equals(TimeHelp.GetTodayStr()))
//                    dateType=ListenDateType.Today;
//                else if (ChooseDate.equals(TimeHelp.GetTomorrowStr()))
//                    dateType=ListenDateType.Tomorrow;
//                else Log.e("ListenActivity", "Choosedate错误,为"+ChooseDate);
//
//                int st=Integer.parseInt(ChooseTimeDialog.CHOOSESTARTTIME);
//                int et=Integer.parseInt(ChooseTimeDialog.CHOOSEENDTIME);
//                //endregion
//
//                ListenItem tmp=new ListenItem(housename,ChooseHouseID,roomname,ChooseRoomID,dateType,st,et);
//                ListenItem.ItemList.add(tmp);
//                Toast.makeText(this, "已添加到列表",Toast.LENGTH_SHORT).show();
//                timeDialog.dismiss();
//                ListenActivity.this.finish();
//            });
//
//            timeDialog.setButtonCancelOnClickListener((View v)->
//            {
//                timeDialog.dismiss();
//            });
//
//        });
//
//        //endregion
//    }
//
//    @Override
//    public void onServiceBinded()
//    {
//        super.onResume();
//        //界面初始化 自动执行一次查询
//        if(buildingSpinner.getAdapter()==null||buildingSpinner.getAdapter().getCount()==0)
//            GetFilters();
//        if(DateSpinner.getAdapter()==null||DateSpinner.getAdapter().getCount()==0)
//            GetDateString();
//        GetRoomGrid();
//    }
//
//    //获取合乎格式的今天日期字符串 并且赋值给相应的adapter
//    public void GetDateString()
//    {
//        //用于返回的链表
//        DateList= TimeHelp.GetLegalTimeList();
//        DateSpinnerAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,DateList);
//        DateSpinner.setAdapter(DateSpinnerAdapter);
//
//        if(ChooseDate==null||ChooseDate.isEmpty())
//            ChooseDate=DateList.get(0);
//    }
//
//    //通过活动发起请求总建筑布局请求
//    public void GetFilters()
//    {
//
//        //真实返回
//        mbinder.GetFilters(new onTaskResultReturn()
//        {
//            @Override
//            public void OnTaskSucceed(Object... data)
//            {
//                try
//                {
//                    String datastr=(String)data[0];
//                    if(datastr==null||datastr.equals(""))
//                        throw new Exception("返回Json为空");
//                    JsonModel_Fliters info= JsonHelp.GetFliters(datastr);
//                    //把接收到的数据复制给本类变量
//                    buildingsList=info.buildingsList;
//                    bdSpinnerAdapter=new BuildingAdapter(info.buildingsList);
//                    buildingSpinner.setAdapter(bdSpinnerAdapter);
//
//                    //读取用户习惯
//                    int defaultbuilding=AppSetting.ListenSetting.getInt("DefaultBuilding", 1)-1;
//                    buildingSpinner.setSelection(defaultbuilding, true);
//                }
//                catch (Exception e)
//                {
//                    Log.e("ListenFragment_old", "建筑布局获取错误"+e.getMessage());
//                    Toast.makeText(ListenActivity.this,"建筑布局获取错误",Toast.LENGTH_SHORT).show();
//                    Toast.makeText(ListenActivity.this,"可能性：图书馆服务器维护、账号短时间冻结",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void OnTaskFailed(Object... data)
//            {
//                //Toast.makeText(ActivityConnect.getApplicationContext(),"建筑布局获取错误",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//    //通过活动发起建筑内房间布局
//    public void GetRoomGrid()
//    {
//        LoadingDialog.LoadingShow(this,true);
//        mbinder.CheckHouseStats(new onTaskResultReturn()
//        {
//            @Override
//            public void OnTaskSucceed(Object... data)
//            {
//                try
//                {
//                    String datastr=(String)data[0];
//                    if(datastr==null||datastr.equals(""))
//                        throw new Exception("返回Json为空");
//                    JsonModel_HouseStats info= JsonHelp.GetHouseStats(datastr);
//                    //把接收到的数据复制给本类变量
//                    roomList=info.roomList;
//                    roomAdapter=new RoomAdapter(ListenActivity.this,roomList);
//                    RoomGrid.setAdapter(roomAdapter);
//                }
//                catch (Exception e)
//                {
//                    Log.e("ListenFragment_old", "房间布局获取错误"+e.getMessage());
//                    Toast.makeText(ListenActivity.this,"房间布局获取错误",Toast.LENGTH_SHORT).show();
//                    Toast.makeText(ListenActivity.this,"可能性：图书馆服务器维护、账号短时间冻结",Toast.LENGTH_SHORT).show();
//                }
//                finally
//                {
//                    LoadingDialog.LoadingHide();
//                }
//            }
//
//            @Override
//            public void OnTaskFailed(Object... data)
//            {
//
//            }
//        }, ChooseHouseID);
//
//
//
//    }
//

}
