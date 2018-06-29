package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONObject;

/**
 * 解析发起了预约请求之后返回的信息Json
 *
 * 本类为APP中所有预约座位信息的统一使用形式 其他类似的类都转换到本类
 * */
public class JsonInfo_BookReturn extends JsonInfo_Base
{
    public int id;
    public String receipt; //回执 凭证号
    public String onDate;
    public String location;
    public String begin; //预约的开始时间
    public String end;   //预约的结束时间
    public JsonInfo_BookReturn(String JsonStr)
    {
        super(JsonStr);
        try
        {
            JSONObject data=((JSONObject)super.data);//转换成本类的实例 真是麻烦
            id=data.getInt("id");
            receipt=data.getString("receipt");
            onDate=data.getString("onDate");
            location=data.getString("location");
            begin=data.getString("begin");
            end=data.getString("end");
        }
        catch (Exception e)
        {
            Log.e("JsonInfo_BookReturn", e.toString()+"/n"+JsonStr);
            return;
        }
    }

    public JsonInfo_BookReturn()
    {}
}
