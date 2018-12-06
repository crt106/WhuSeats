package com.crt.whuseats.JsonModels;

import android.util.Log;

import org.json.JSONArray;

import java.util.HashMap;

/**
 * 从服务器（crt）上获取第二天用户已经提交的座位信息
 * Update at v0.6.9 by crt 2018-12-4
 */
public class JsonModel_Tomorrow extends JsonModel_Base
{
    private static final String TAG = "JsonModel_Tomorrow";
    public HashMap<Integer, Integer> tomoInfo;

    public JsonModel_Tomorrow(String JsonStr) throws Exception
    {

        super(JsonStr);

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
}
