package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * 解析特定的座位开始时间的Json
 * {
  "status": "success",
  "data": {
  "startTimes": [{
  "id": "1290",
  "value": "21:30"
  }, {
  "id": "1320",
  "value": "22:00"
  }]
  },
  "message": "",
  "code": "0"
 }
 */
public class JsonInfo_SeatTime_Strat extends JsonInfo_Base
{
    //注意为什么这里的时间ID为String 因为这个ID有可能为:"now"
    public List<String> StartTimeList=new LinkedList<>();
    public JsonInfo_SeatTime_Strat(String JsonStr)
    {
        super(JsonStr);
        try
        {
            //这里不要偷懒啦~
            JSONArray StartTimeArray=((JSONObject)data).getJSONArray("startTimes");

            if(StartTimeArray.length()==0) //如果开始时间可用长度为0直接跳走
                return;
            for(int i=0;i<StartTimeArray.length();i++)
            {
                String timeID=StartTimeArray.getJSONObject(i).getString("id");
                if(timeID.equals("now"))
                    continue;
                else
                    StartTimeList.add(timeID);
            }

        }
        catch (Exception e)
        {
            Log.e("JsonInfo_TimeStart", e.getMessage() );
            return;
        }
    }
}
