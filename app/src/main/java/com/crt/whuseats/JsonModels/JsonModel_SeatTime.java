package com.crt.whuseats.JsonModels;

import android.util.Log;

import com.crt.whuseats.Model.TimeItems;
import com.crt.whuseats.Utils.TimeHelp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * 解析特定的座位开始或者结束时间的Json
 * 将原来的Start和End合并为一起，并且把以前忽略的now加入
 * Update at v1.0 by crt 2018-12-4
 * 例子：
 *  * {
 *  *     "status": "success",
 *  *     "data": {
 *  *         "startTimes": [
 *  *             {
 *  *                 "id": "now",
 *  *                 "value": "现在"
 *  *             },
 *  *             {
 *  *                 "id": "1230",
 *  *                 "value": "20:30"
 *  *             },
 *  *             {
 *  *                 "id": "1260",
 *  *                 "value": "21:00"
 *  *             },
 *  *             {
 *  *                 "id": "1290",
 *  *                 "value": "21:30"
 *  *             },
 *  *             {
 *  *                 "id": "1320",
 *  *                 "value": "22:00"
 *  *             }
 *  *         ]
 *  *     },
 *  *     "message": "",
 *  *     "code": "0"
 *  * }
 *  结束时间例子
 *  {
 *  *     "status": "success",
 *  *     "data": {
 *  *         "endTimes": [
 *  *             {
 *  *                 "id": "1290",
 *  *                 "value": "21:30"
 *  *             },
 *  *             {
 *  *                 "id": "1320",
 *  *                 "value": "22:00"
 *  *             },
 *  *             {
 *  *                 "id": "1350",
 *  *                 "value": "22:30"
 *  *             }
 *  *         ]
 *  *     },
 *  *     "message": "",
 *  *     "code": "0"
 *  * }
 *
 *
 */
public class JsonModel_SeatTime extends JsonModel_Base
{
    private static final String TAG = "JsonModel_SeatTime";
    public List<TimeItems.TimeStruct> TimeList =new LinkedList<>();
    public JsonModel_SeatTime(String JsonStr) throws Exception
    {
        super(JsonStr);

            //这里不要偷懒啦~
            JSONArray StartTimeArray=((JSONObject)data).getJSONArray("startTimes");

            if(StartTimeArray.length()==0) //如果开始时间可用长度为0直接跳走
                return;

            for(int i=0;i<StartTimeArray.length();i++)
            {
                try
                {
                    String timeID=StartTimeArray.getJSONObject(i).getString("id");
                    TimeList.add(TimeHelp.GetTimeStructByID(timeID));
                } catch (JSONException e)
                {
                    Log.e(TAG, "JsonModel_SeatTime: 添加可用时间到列表出错"+e.toString());
                    continue;
                }

            }

    }

}
