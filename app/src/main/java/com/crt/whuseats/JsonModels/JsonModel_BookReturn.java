package com.crt.whuseats.JsonModels;

import android.util.Log;

import com.crt.whuseats.Model.TimeItems;
import com.crt.whuseats.Utils.TimeHelp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析发起了预约请求之后返回的信息Json
 *
 * 本类为APP中所有预约座位信息的统一使用形式 其他类似的类都转换到本类
 * */
public class JsonModel_BookReturn extends JsonModel_Base
{
    private static final String TAG = "JsonModel_BookReturn";
    public int id;
    public String receipt; //回执 凭证号
    /**
     * 预约日期
     */
    public String onDate;
    /**
     * 位置描述字符串
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

    public JsonModel_BookReturn(String JsonStr) throws Exception
    {
        super(JsonStr);
            JSONObject data=((JSONObject)super.data);//转换成本类的实例 真是麻烦
            id=data.getInt("id");
            receipt=data.getString("receipt");
            onDate=data.getString("onDate");
            location=data.getString("location");
            String beginValue=data.getString("begin");
            begin=TimeHelp.GetTimeStructByValue(beginValue);
            String endValue=data.getString("end");
            end=TimeHelp.GetTimeStructByValue(endValue);
    }

    public JsonModel_BookReturn()
    {

    }
}
