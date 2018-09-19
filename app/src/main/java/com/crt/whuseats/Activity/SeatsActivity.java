package com.crt.whuseats.Activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crt.whuseats.Adapter.BuildingAdapter;
import com.crt.whuseats.Adapter.RoomAdapter;
import com.crt.whuseats.Adapter.SeatsAdapter;
import com.crt.whuseats.Dialog.ChooseTimeDialog;
import com.crt.whuseats.Dialog.LoadingDialog;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.JsonHelps.JsonHelp;
import com.crt.whuseats.JsonHelps.JsonInfo_Fliters;
import com.crt.whuseats.JsonHelps.JsonInfo_HouseStats;
import com.crt.whuseats.JsonHelps.JsonInfo_RoomLayout;
import com.crt.whuseats.JsonHelps.JsonInfo_Tomorrow;
import com.crt.whuseats.JsonHelps.seat;
import com.crt.whuseats.R;
import com.crt.whuseats.Utils.TimeHelp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public static List<Integer> tomoinfolist=new LinkedList<>();               //第二天预约信息列表


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


        tomoinfolist=new LinkedList<>();
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

            //构建指示文字
            String infotext=ChooseRoomName+ChooseSeatName;

            //如果被选择座位在明日信息列表中
            if(tomoinfolist.contains(ChooseSeatID))
            {
                infotext+="\n(该座位有可能存在竞争情况)";
            }

            //呼出选择时间对话框
            ChooseTimeDialog tempDialog=new ChooseTimeDialog(SeatsActivity.this,infotext);
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
        GetTomorrowInfo();
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
        LoadingDialog.LoadingShow(this,true);
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
                finally
                {
                    LoadingDialog.LoadingHide();
                }
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        }, ChooseHouseID);


    }

    /**
     * 获取座位布局
     */
    public void GetSeatsLayout()
    {
        LoadingDialog.LoadingShow(this,true);
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
                finally
                {
                    LoadingDialog.LoadingHide();
                }
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        }, ChooseRoomID, TimeHelp.GetTodayStr());
    }

    /**
      获取第二天座位预约信息，构建一个有预约座位的链表
      如果在之前的BookFragment中没有获取到 则重新获取
     */
    public void GetTomorrowInfo()
    {
        //如果已经有信息了则跳过
        if(tomoinfolist.size()!=0)
            return;

        mbinder.GetTomorrowInfo(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                try
                {
                    String str=(String)data[0];
                    JsonInfo_Tomorrow JsonClass=JsonHelp.GetTomorrowInfo(str);
                    //构建信息链表
                    tomoinfolist.clear();
                    for ( Map.Entry<Integer, Integer> ob : JsonClass.tomoInfo.entrySet() )
                    {
                        if(ob.getValue()>=2)
                        tomoinfolist.add(ob.getKey());
                    }
                    //构建完成
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
