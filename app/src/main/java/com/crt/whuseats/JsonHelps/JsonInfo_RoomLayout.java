package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * 解析特定房间的座位布局
 * {
 "status": "success",
 "data": {
 "id": 8,
 "name": "三楼西社会科学图书借阅区",
 "cols": 25,
 "rows": 24,
 "layout": {
 "0": {
 "type": "empty"
 },
 "1": {
 "type": "empty"
 },
 "2": {
 "type": "empty"
 },...
 "1005": {
 "id": 4767,
 "name": "001",
 "type": "seat",
 "status": "IN_USE",
 "window": true,
 "power": true,
 "computer": false,
 "local": false
 },
 "1006": {
 "id": 4768,
 "name": "005",
 "type": "seat",
 "status": "IN_USE",
 "window": true,
 "power": true,
 "computer": false,
 "local": false
 },"message": "",
 "code": "0"
 }
 */
public class JsonInfo_RoomLayout extends  JsonInfo_Base
{

    public List<seat> seatList=new LinkedList<>();
    public int roomId;
    public String roomName;
    public int cols;
    public int rows;
    private JSONObject layoutOBJ;
    public JsonInfo_RoomLayout(String JsonStr)
    {
        super(JsonStr);
        try
        {
            roomId=((JSONObject)data).getInt("id");
            roomName=((JSONObject)data).getString("name");
            cols=((JSONObject)data).getInt("cols");
            rows=((JSONObject)data).getInt("rows");
            layoutOBJ=((JSONObject)data).getJSONObject("layout");
            //开始循环解析 获取座位列表
            for(int i=0;i<rows;i++)
            {
                for(int j=0;j<cols;j++)
                {
                    String getstr= Integer.toString(i*1000+j);
                    JSONObject nowOBJ=layoutOBJ.getJSONObject(getstr);
                    String seattype=nowOBJ.getString("type");

                    //判断该字段下的子字段是否为空
                    if(!seattype.equals("seat"))
                        continue;

                    int seatid=nowOBJ.getInt("id");
                    String name=nowOBJ.getString("name");
                    String seatstatus=nowOBJ.getString("status");
                    boolean window=nowOBJ.getBoolean("window");
                    boolean power=nowOBJ.getBoolean("power");
                    boolean computer=nowOBJ.getBoolean("computer");
                    boolean local=nowOBJ.getBoolean("local");
                    seatList.add(new seat(seatid,name,seattype,seatstatus,window,power,computer,local));
                }
            }
        }
        catch (Exception e)
        {
            Log.e("JsonInfo_RoomLayout", e.getMessage() );
            return;
        }
    }
}
