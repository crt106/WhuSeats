package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**接收移动端的座位筛选信息
 *    "data": {
 * "seats": {
 "1031": {
 "id": 2478,
 "name": "009",
 "type": "seat",
 "status": "FREE",
 "window": false,
 "power": false,
 "computer": false,
 "local": false
 },
 "2026": {
 "id": 2505,
 "name": "004",
 "type": "seat",
 "status": "FREE",
 "window": false,
 "power": false,
 "computer": false,
 "local": false
 },
 "2028": {
 "id": 2507,
 "name": "006",
 "type": "seat",
 "status": "FREE",
 "window": false,
 "power": false,
 "computer": false,
 "local": false
 },
 */
public class JsonInfo_MobileFiltrate
{

    public List<seat> seatList=new LinkedList<>();

    public JsonInfo_MobileFiltrate(String JsonStr)
    {
        try
        {
            JSONObject data=(new JSONObject(JsonStr)).getJSONObject("data");
            JSONObject seats=data.getJSONObject("seats");
            //遍历所有的键值
            Iterator<String> keys=seats.keys();
            while (keys.hasNext())
            {
                //获取单个座位
                JSONObject oneseat=seats.getJSONObject(keys.next().toString());

                String seattype=oneseat.getString("type");

                //判断该字段下的子字段是否为空
                if(!seattype.equals("seat"))
                    continue;

                int seatid=oneseat.getInt("id");
                String name=oneseat.getString("name");
                String seatstatus=oneseat.getString("status");
                boolean window=oneseat.getBoolean("window");
                boolean power=oneseat.getBoolean("power");
                boolean computer=oneseat.getBoolean("computer");
                boolean local=oneseat.getBoolean("local");
                seatList.add(new seat(seatid,name,seattype,seatstatus,window,power,computer,local));
            }
        }
        catch (Exception e)
        {
            Log.e("JI_MFiltrate", e.toString());
        }
    }
}
