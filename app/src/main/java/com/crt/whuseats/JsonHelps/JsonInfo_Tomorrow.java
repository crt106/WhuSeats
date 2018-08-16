package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Struct;
import java.util.HashMap;
import java.util.List;

public class JsonInfo_Tomorrow extends JsonInfo_Base
{
    public HashMap<Integer, Integer> tomoInfo;

    public JsonInfo_Tomorrow(String JsonStr)
    {

        super(JsonStr);
        try
        {
            tomoInfo = new HashMap<>();
            JSONArray reArray = (JSONArray) data;
            //构建hashmap
            for (int i = 0; i < reArray.length(); i++)
            {
                int seatID = reArray.getJSONObject(i).getInt("seatID");
                int count = reArray.getJSONObject(i).getInt("count");
                tomoInfo.put(seatID, count);
            }
            
        } 
        catch (Exception e)
        {
            Log.e("JsonInfo_Tomorrow",e.getMessage());
        }


    }
}
