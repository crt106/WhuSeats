package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 解析预约信息的Json(查看当前预约)
 * 成功例子：
 * {"status":"success",
 * "data":
 * [{"id":2802877,
 *   "receipt":"0110-877-2",
 *   "onDate":"2018-05-15",
 *   "seatId":9185,
 *   "status":"RESERVE",
 *   "location":"信息科学分馆1层西区3C创客-双屏电脑（20台），座位号003",
 *   "begin":"18:00",
 *   "end":"22:00",
 *   "actualBegin":null,
 *   "awayBegin":null,
 *   "awayEnd":null,
 *   "userEnded":false,
 *   "message":"请在 05月15日17点45分 至 18点35分 之间前往场馆签到"}],
 * "message":"",
 * "code":"0"}
 * 空例子：
 * {"status":"success","data":null,"message":"","code":"0"}
 */
public class JsonInfo_Reservations extends JsonInfo_Base
{
    public int id;
    public String receipt; //回执 凭证号
    public String onDate;
    public int SeatId;
    public String datastatus; //data内包含的status 这是指示当前座位的状态
    public String location;
    public String begin; //预约的开始时间
    public String end;   //预约的结束时间
    //region 这三个键还没有看出来有什么用
    public String actualBegin;
    public String awayBegin;
    public String awayEnd;
    //endregion
    public boolean userEnded;
    public String datamessage; //data内部的message

    public JsonInfo_Reservations(String JsonStr)
    {
        super(JsonStr);
        try
        {
            JSONObject data=((JSONArray)super.data).getJSONObject(0);//转换成本类的实例 真是麻烦
            id=data.getInt("id");
            receipt=data.getString("receipt");
            onDate=data.getString("onDate");
            SeatId=data.getInt("seatId");
            datastatus=data.getString("status");
            location=data.getString("location");
            begin=data.getString("begin");
            end=data.getString("end");
            actualBegin=data.getString("actualBegin");
            awayBegin=data.getString("awayBegin");
            awayEnd=data.getString("awayEnd");
            userEnded=data.getBoolean("userEnded");
            userEnded=data.getBoolean("userEnded");
            datamessage=data.getString("message");
        }
        catch (Exception e)
        {
            Log.e("JsonInfo_Reservation", e.getMessage());
            return;
        }
    }

    public JsonInfo_Reservations()
    {}

}
