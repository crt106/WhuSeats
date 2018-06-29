package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class JsonInfo_SeatTime_End extends JsonInfo_Base
{

    public List<String> EndTimeList=new LinkedList<>();
    public JsonInfo_SeatTime_End(String JsonStr)
    {
        super(JsonStr);
        try
        {
            //这里不要偷懒啦~
            JSONArray StartTimeArray=((JSONObject)data).getJSONArray("endTimes");

            if(StartTimeArray.length()==0) //如果开始时间可用长度为0直接跳走
                return;
            for(int i=0;i<StartTimeArray.length();i++)
            {
                String timeID=StartTimeArray.getJSONObject(i).getString("id");
                EndTimeList.add(timeID);
            }
        }
        catch (Exception e)
        {
            Log.e("JsonInfo_TimeEnd", e.getMessage() );
            return;
        }
    }
}
