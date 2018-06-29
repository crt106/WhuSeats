package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * 解析楼层布局信息Json
 * 例子：
 * {
 "status": "success",
 "data": {
 "buildings": [
 [1, "信息科学分馆", 5],
 [2, "工学分馆", 5],
 [3, "医学分馆", 5],
 [4, "总馆", 6]
 ],
 "rooms": [
 [4, "一楼3C创客空间", 1, 1],
 [5, "一楼创新学习讨论区", 1, 1],
 [6, "二楼西自然科学图书借阅区", 1, 2],
 [7, "二楼东自然科学图书借阅区", 1, 2],
 [8, "三楼西社会科学图书借阅区", 1, 3],
 [9, "四楼西图书阅览区", 1, 4],
 [10, "三楼东社会科学图书借阅区", 1, 3],
 [11, "四楼东图书阅览区", 1, 4],
 [12, "三楼自主学习区", 1, 3],
 [13, "3C创客-电子资源阅览区（20台）", 1, 1],
 [14, "3C创客-双屏电脑（20台）", 1, 1],
 [15, "创新学习-MAC电脑（12台）", 1, 1],
 [16, "创新学习-云桌面（42台）", 1, 1],
 [19, "201室自科图书借阅区", 2, 2],
 [20, "204教学参考书借阅区", 3, 2],
 [21, "302中文科技图书借阅B区", 3, 3],
 [23, "305科技期刊阅览区", 3, 3],
 [24, "402中文文科图书借阅区", 3, 4],
 [26, "502外文图书借阅区", 3, 5],
 [27, "506医学人文阅览区", 3, 5],
 [29, "2楼走廊", 2, 2],
 [31, "205室电阅笔记本区", 2, 2],
 [32, "301室+东部自科图书借阅区", 2, 3],
 [33, "305室+中部自科图书借阅区", 2, 3],
 [34, "401室+东部自科图书借阅区", 2, 4],
 [35, "405室+中部期刊阅览区", 2, 4],
 [37, "501室+东部外文图书借阅区", 2, 5],
 [38, "505室+中部自科图书借阅区", 2, 5],
 [39, "A1-座位区", 4, 1],
 [40, "C1自习区", 4, 1],
 [51, "A2", 4, 2],
 [52, "A3", 4, 3],
 [56, "B3", 4, 3],
 [59, "B2", 4, 2],
 [60, "A4", 4, 4],
 [61, "A5", 4, 5],
 [62, "A1-沙发区", 4, 1],
 [63, "A1-电子阅览室", 4, 1],
 [65, "B1", 4, 1],
 [66, "A1-苹果区", 4, 1],
 [68, "205室电阅云桌面区", 2, 2]
 ],
 "hours": 15,
 "dates": ["2018-05-15"]
 },
 "message": "",
 "code": "0"
 }
 */
public class JsonInfo_Fliters extends JsonInfo_Base
{
    //两个相应数据结构
    public class buildings
    {
       public int id;
       public String name;
       public int floors;        //总层数
        public buildings(int id,String name,int... moreValue)
        {
            this.id=id;
            this.name=name;
            if(moreValue!=null&&moreValue.length!=0)
            {
                floors=moreValue[0];
            }
        }
    }
    public class rooms
    {
        public int id;
        public String name;
        public int belongBuilding;   //属于哪个建筑
        public int whichfloor;       //在几层
        public rooms(int id,String name,int... moreValue)
        {
            this.id=id;
            this.name=name;
            if(moreValue!=null)
            {
                belongBuilding=moreValue[0];
                whichfloor=moreValue[1];
            }
        }
    }
    public List<buildings> buildingsList=new LinkedList<>();
    public List<rooms> roomsList=new LinkedList<>();

    public int hours;
    public String dates;
    public JsonInfo_Fliters(String JsonStr)
    {
        super(JsonStr);
        try
        {
            JSONArray buildingsArray=((JSONObject)data).getJSONArray("buildings");
            JSONArray roomsArray=((JSONObject)data).getJSONArray("rooms");
            //循环建立两个List
            for(int i=0;i<buildingsArray.length();i++)
            {
                JSONArray line=buildingsArray.getJSONArray(i);
                int id=line.getInt(0);
                String name=line.getString(1);
                int floors=line.getInt(2);
                buildingsList.add(new buildings(id,name,floors));
            }
            for (int j=0;j<roomsArray.length();j++)
            {
                JSONArray line=roomsArray.getJSONArray(j);
                int id=line.getInt(0);
                String name=line.getString(1);
                int belongBuilding=line.getInt(2);
                int whichfloor=line.getInt(3);
                roomsList.add(new rooms(id,name,belongBuilding,whichfloor));
            }
            //读取data内的剩余数据
            hours=((JSONObject)data).getInt("hours");
            dates=((JSONObject)data).getJSONArray("dates").getString(0);
        }
        catch (Exception e)
        {
            Log.e("JsonInfo_Fliters", e.getMessage());
        }
    }


}
