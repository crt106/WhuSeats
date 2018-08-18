package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * 解析指定ID房间状况的Json
 * 例子：
 * {
 "status": "success",
 "data": [{
 "roomId": 14,
 "room": "3C创客-双屏电脑（20台）",
 "floor": 1,
 "reserved": 1,
 "inUse": 17,
 "away": 0,
 "totalSeats": 20,
 "free": 2
 }, {
 "roomId": 13,
 "room": "3C创客-电子资源阅览区（20台）",
 "floor": 1,
 "reserved": 2,
 "inUse": 15,
 "away": 0,
 "totalSeats": 20,
 "free": 3
 }.............等等],
 "message": "",
 "code": "0"
 }
 */
public class JsonInfo_HouseStats extends JsonInfo_Base
{
    /**
     * 这个room类指代特定的房间
     */
    public class room
    {
        public int roomId;
        public String roomname;
        public int whichfloor;
        public int reserved;
        public int inUse;
        public int away;
        public int totalSeats;
        public int free;

        public room(int roomId, String roomname, int whichfloor, int reserved, int inUse, int away, int totalSeats, int free)
        {
            this.roomId = roomId;
            this.roomname = roomname;
            this.whichfloor = whichfloor;
            this.reserved = reserved;
            this.inUse = inUse;
            this.away = away;
            this.totalSeats = totalSeats;
            this.free = free;
        }

    }

    public List<room> roomList=new LinkedList<>();   //房间列表
    public JsonInfo_HouseStats(String JsonStr)
    {
        super(JsonStr);
        try
        {
            JSONArray roomArray=((JSONArray)data);
            for (int i=0;i<roomArray.length();i++)
            {
                JSONObject j=roomArray.getJSONObject(i);
                int roomId=j.getInt("roomId");
                String roomname=j.getString("room");
                int whichfloor=j.getInt("floor");
                int reserved=j.getInt("reserved");
                int inUse=j.getInt("inUse");
                int away=j.getInt("away");
                int totalSeats=j.getInt("totalSeats");
                int free=j.getInt("free");
                roomList.add(new room(roomId,roomname,whichfloor,reserved,inUse,away,totalSeats,free));
            }
        }
        catch (Exception e)
        {
            Log.e("JsonInfo_HouseStatus", e.getMessage());
        }
    }

}
