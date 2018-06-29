package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * 解析预约历史返回的JSon
 * {
 "status": "success",
 "data": {
 "reservations": [{
   "id": 2799985,
   "date": "2018-5-14",
   "begin": "18:00",
   "end": "22:30",
   "awayBegin": null,
   "awayEnd": null,
   "loc": "信息科学分馆1层东区创新学习-云桌面（42台）040号",
   "stat": "CHECK_IN"
 }, {
 "id": 2779326,
 "date": "2018-5-13",
 "begin": "14:00",
 "end": "22:00",
 "awayBegin": null,
 "awayEnd": null,
 "loc": "信息科学分馆1层西区3C创客-双屏电脑（20台）003号",
 "stat": "STOP"
 }, {
 "id": 2771558,
 "date": "2018-5-12",
 "begin": "14:30",
 "end": "22:30",
 "awayBegin": null,
 "awayEnd": null,
 "loc": "信息科学分馆3层东区三楼东社会科学图书借阅区029号",
 "stat": "CANCEL"
 }, ..................
 "message": "",
 "code": "0"
 }
 */
public class JsonInfo_HistoryList extends JsonInfo_Base
{
    //存放历史预约状况的链表
    public List<JsonInfo_Reservations> reservationsList=new LinkedList<>();

    public JsonInfo_HistoryList(String JsonStr)
    {
        super(JsonStr);
        try
        {
            JSONArray reservationarray=((JSONObject)data).getJSONArray("reservations");
            for (int i=0;i<reservationarray.length();i++)
            {
                JSONObject jobj=reservationarray.getJSONObject(i);
                JsonInfo_Reservations temp=new JsonInfo_Reservations();
                temp.id=jobj.getInt("id");
                temp.onDate=jobj.getString("date");
                temp.begin=jobj.getString("begin");
                temp.end=jobj.getString("end");
                temp.awayBegin=jobj.getString("awayBegin");
                temp.awayEnd=jobj.getString("awayEnd");
                temp.location=jobj.getString("loc");
                temp.datastatus=jobj.getString("stat");

                reservationsList.add(temp);
            }
        }
        catch (Exception e)
        {
            Log.e("JsonInfo_HistoryList",e.getMessage());
        }
    }
}
