package com.crt.whuseats.JsonModels;

import android.util.Log;

import com.crt.whuseats.Utils.TimeHelp;

import org.json.JSONArray;
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
public class JsonModel_HistoryList extends JsonModel_Base
{
    private static final String TAG = "JsonModel_HistoryList";
    //存放历史预约状况的链表
    public List<JsonModel_Reservations> reservationsList=new LinkedList<>();

    public JsonModel_HistoryList(String JsonStr) throws Exception
    {
        super(JsonStr);
            JSONArray reservationarray=((JSONObject)data).getJSONArray("reservations");
            for (int i=0;i<reservationarray.length();i++)
            {
                try
                {
                    JSONObject jobj=reservationarray.getJSONObject(i);

                    JsonModel_Reservations temp=new JsonModel_Reservations();
                    temp.id=jobj.getInt("id");
                    temp.onDate=jobj.getString("date");
                    temp.begin=TimeHelp.GetTimeStructByValue(jobj.getString("begin"));
                    temp.end=TimeHelp.GetTimeStructByValue(jobj.getString("end"));
                    temp.awayBegin=jobj.getString("awayBegin");
                    temp.awayEnd=jobj.getString("awayEnd");
                    temp.location=jobj.getString("loc");
                    temp.datastatus=jobj.getString("stat");

                    reservationsList.add(temp);
                } catch (Exception e)
                {
                    continue;
                }
            }
    }
}
