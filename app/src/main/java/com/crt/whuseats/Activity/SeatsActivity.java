package com.crt.whuseats.Activity;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crt.whuseats.Adapter.BuildingAdapter;
import com.crt.whuseats.Adapter.RoomAdapter;
import com.crt.whuseats.Adapter.SeatsAdapter;
import com.crt.whuseats.Dialog.ChooseTimeDialog;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.JsonHelps.JsonHelp;
import com.crt.whuseats.JsonHelps.JsonInfo_Fliters;
import com.crt.whuseats.JsonHelps.JsonInfo_HouseStats;
import com.crt.whuseats.JsonHelps.JsonInfo_RoomLayout;
import com.crt.whuseats.JsonHelps.seat;
import com.crt.whuseats.R;
import com.crt.whuseats.TimeHelp;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class SeatsActivity extends BaseActivity
{

    //region 控件们
    private TextView tvBooktitle;
    private ListView rvHouse;
    private ListView rvRoom;
    private GridView gvSeatPage;
    //endregion
    BuildingAdapter buildingAdapter;
    RoomAdapter roomAdapter;
    SeatsAdapter seatsAdapter;

    //一些静态字段们
    public static String ChooseHouseName;
    public static int ChooseHouseID=1;
    public static String ChooseRoomName;
    public static int ChooseRoomID;
    public static String ChooseSeatName;
    public static int ChooseSeatID;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seat_page);
        //获取控件
        tvBooktitle = (TextView) findViewById(R.id.tv_booktitle);
        rvHouse = (ListView) findViewById(R.id.rv_house);
        rvRoom = (ListView) findViewById(R.id.rv_room);
        gvSeatPage = (GridView) findViewById(R.id.gv_seat_page);


        //设置相应点击事件
        rvHouse.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id)->{
            //刷新当前选择
            ChooseHouseID=((JsonInfo_Fliters.buildings)rvHouse.getAdapter().getItem(position)).id;
            ChooseHouseName=((JsonInfo_Fliters.buildings)rvHouse.getAdapter().getItem(position)).name;
            tvBooktitle.setText(ChooseHouseName);
            GetRoomGrid();
        });
        //房间列表点击事件
        rvRoom.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id)->{
            //刷新当前选择
            ChooseRoomID=((JsonInfo_HouseStats.room)rvRoom.getAdapter().getItem(position)).roomId;
            ChooseRoomName=((JsonInfo_HouseStats.room)rvRoom.getAdapter().getItem(position)).roomname;
            tvBooktitle.setText(ChooseHouseName+" "+ChooseRoomName);
            GetSeatsLayout();
        });
        //具体座位点击事件
        gvSeatPage.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id)->{
            //获取选中的ID和座位名
            ChooseSeatID=((seat)gvSeatPage.getAdapter().getItem(position)).seatid;
            ChooseSeatName=((seat)gvSeatPage.getAdapter().getItem(position)).name;

            //呼出选择时间对话框
            ChooseTimeDialog tempDialog=new ChooseTimeDialog(SeatsActivity.this,ChooseRoomName+ChooseSeatName);
            tempDialog.show();
            tempDialog.RunInBookMode(true);
            tempDialog.setButtonCancelOnClickListener(v -> {tempDialog.dismiss();});
            tempDialog.setButtonOKOnClickListener((v)->{
                tempDialog.dismiss();
                //保存用户的选择
                BaseActivity.AppSetting.BookSettingEditor.putString("ChooseHouseName", ChooseHouseName);
                BaseActivity.AppSetting.BookSettingEditor.putInt("ChooseHouseID", ChooseHouseID);
                BaseActivity.AppSetting.BookSettingEditor.putString("ChooseRoomName", ChooseRoomName);
                BaseActivity.AppSetting.BookSettingEditor.putInt("ChooseRoomID", ChooseRoomID);
                BaseActivity.AppSetting.BookSettingEditor.putString("ChooseSeatName", ChooseSeatName);
                BaseActivity.AppSetting.BookSettingEditor.putInt("ChooseSeatID", ChooseSeatID);
                BaseActivity.AppSetting.BookSettingEditor.apply();
                Toast.makeText(BaseActivity.ApplicationContext, "选择座位成功", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    protected void onServiceBinded()
    {
        ReadSetting();
        GetFilters();
        GetRoomGrid();
    }

    //读取用户相关设置
    public void ReadSetting()
    {
        ChooseHouseName=BaseActivity.AppSetting.BookSetting.getString("ChooseHouseName", "信息科学分馆");
        ChooseHouseID=BaseActivity.AppSetting.BookSetting.getInt("ChooseHouseID", 1);
        ChooseRoomName=BaseActivity.AppSetting.BookSetting.getString("ChooseRoomName", "");
        ChooseRoomID=BaseActivity.AppSetting.BookSetting.getInt("ChooseRoomID", 8);
        ChooseSeatName=BaseActivity.AppSetting.BookSetting.getString("ChooseSeatName", "");
        ChooseSeatID=BaseActivity.AppSetting.BookSetting.getInt("ChooseSeatID", -1);
    }
    //通过活动发起请求总建筑布局请求
    public void GetFilters()
    {
        //真实返回
        mbinder.GetFilters(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                try
                {
                    String datastr = (String) data[0];
                    if (datastr == null || datastr.equals(""))
                        throw new Exception("返回Json为空");
                    JsonInfo_Fliters info = JsonHelp.GetFliters(datastr);
                    //把接收到的数据复制给本类变量
                    buildingAdapter = new BuildingAdapter(info.buildingsList);
                    rvHouse.setAdapter(buildingAdapter);
                } catch (Exception e)
                {
                    Log.e("SeatsActivity", "建筑布局获取错误" + e.getMessage());
                    Toast.makeText(SeatsActivity.this, "建筑布局获取错误", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SeatsActivity.this, "可能性：图书馆服务器维护、账号短时间冻结", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void OnTaskFailed(Object... data)
            {
                //Toast.makeText(ActivityConnect.getApplicationContext(),"建筑布局获取错误",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //通过活动发起建筑内房间布局
    public void GetRoomGrid()
    {
        mbinder.CheckHouseStats(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                try
                {
                    String datastr=(String)data[0];
                    if(datastr==null||datastr.equals(""))
                        throw new Exception("返回Json为空");
                    JsonInfo_HouseStats info= JsonHelp.GetHouseStats(datastr);
                    //把接收到的数据复制给本类变量
                    roomAdapter=new RoomAdapter(SeatsActivity.this,info.roomList);
                    rvRoom.setAdapter(roomAdapter);
                }
                catch (Exception e)
                {
                    Log.e("SeatsActivity", "房间布局获取错误"+e.getMessage());
                }
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        }, ChooseHouseID);


    }

    public void GetSeatsLayout()
    {
        mbinder.CheckRoomStats(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                try
                {
                    JsonInfo_RoomLayout roomLayout = JsonHelp.GetRoomLayout((String) data[0]);
                    //把接收到的数据传给Adapter
                    Collections.sort(roomLayout.seatList);//重新排序
                    seatsAdapter=new SeatsAdapter(SeatsActivity.this,roomLayout.seatList);
                    gvSeatPage.setAdapter(seatsAdapter);

                } catch (Exception e)
                {
                    Log.e("SeatsActivity", "接收房间布局错误" + e.getMessage());
                }
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        }, ChooseRoomID, TimeHelp.GetTodayStr());
    }




}
