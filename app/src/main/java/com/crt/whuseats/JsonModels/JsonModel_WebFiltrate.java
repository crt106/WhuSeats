package com.crt.whuseats.JsonModels;

import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

/**
 * 解析由Web端服务器返回的座位筛选结果
 * Deprecated at v0.5.6 改为移动端过滤替代
 */
@Deprecated
public class JsonModel_WebFiltrate
{
    public List<String> SuitableSeatsList=new LinkedList<>();

    //其实下面这三个玩意儿都用处不大
    public String Date;
    public int offset;
    public int SeatNum;

    public JsonModel_WebFiltrate(String JsonStr)
    {
        try
        {
            JSONObject data=new JSONObject(JsonStr);
            Date=data.getString("onDate");
            SeatNum=data.getInt("seatNum");
            offset=data.getInt("offset");
            String seatStr=data.getString("seatStr");

            //开始HTML解析部分
            Document doc= Jsoup.parse(seatStr);
            Elements seats=doc.select("li");
            for(Element s:seats)
            {
                //这一段是从<li class="using" id="seat_9120".../>中解析出"9120"
                String thisID=s.attributes().get("id").split("_")[1];
                //添加到链表
                SuitableSeatsList.add(thisID);
            }
        }

        catch (Exception e)
        {
            Log.e("JsonInfo_webFiltrate", "给定的web返回Json有问题吧老哥\n"+e.getMessage());
        }


    }
}
