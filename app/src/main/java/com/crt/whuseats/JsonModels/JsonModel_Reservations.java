package com.crt.whuseats.JsonModels;

import android.util.Log;

import com.crt.whuseats.Model.TimeItems;
import com.crt.whuseats.Utils.TimeHelp;

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
public class JsonModel_Reservations extends JsonModel_Base
{
    private static final String TAG = "JsonModel_Reservations";
    /**
     * 该预约的id，注意与其他区分
     */
    public int id;
    /**
     * 回执号
     */
    public String receipt;
    /**
     * 预约日期
     */
    public String onDate;
    /**
     * 预约的座位唯一ID
     */
    public int SeatId;
    /**
     * 这是指示当前座位的状态，与外层status区分
     */
    public String datastatus;
    /**
     * 预约座位的描述字符串
     */
    public String location;
    /**
     * 预约的开始时间
     */
    public TimeItems.TimeStruct begin;
    /**
     * 预约的结束时间
     */
    public TimeItems.TimeStruct end;

    //region 这三个键还没有看出来有什么用

    public String actualBegin;
    public String awayBegin;
    public String awayEnd;
    //endregion

    public boolean userEnded;
    /**
     * data内部的消息，与外层消息区分
     */
    public String datamessage;

    public JsonModel_Reservations(String JsonStr)
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
            String beginValue=data.getString("begin");
            begin=TimeHelp.GetTimeStructByValue(beginValue);
            String endValue=data.getString("end");
            end=TimeHelp.GetTimeStructByValue(endValue);
            actualBegin=data.getString("actualBegin");
            awayBegin=data.getString("awayBegin");
            awayEnd=data.getString("awayEnd");
            userEnded=data.getBoolean("userEnded");
            userEnded=data.getBoolean("userEnded");
            datamessage=data.getString("message");
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            return;
        }
    }

    public JsonModel_Reservations()
    {

    }

}
