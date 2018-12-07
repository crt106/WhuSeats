package com.crt.whuseats.JsonModels;

//提供Json解析能力的帮助类 其实这个类好像没什么卵用？？

import android.util.Log;

import com.crt.whuseats.Model.TimeItems;
import com.crt.whuseats.Utils.TimeHelp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonHelp
{
    /**
     * 获取如下GET请求返回的JSon对象
     * GET http://seat.lib.whu.edu.cn/rest/auth?username={0}&password={1} HTTP/1.1
     */
    public static JsonModel_Login GetLogin(String JsonStr) throws Exception
    {
        return new JsonModel_Login(JsonStr);
    }

    /**
     * 获取如下GET请求返回的JSon对象
     * GET http://seat.lib.whu.edu.cn/rest/v2/user/reservations?token=8ZXSSXN3CC05154011 HTTP/1.1
     */
    public static JsonModel_Reservations GetReservation(String JsonStr) throws Exception
    {
        return new JsonModel_Reservations(JsonStr);
    }

    /**
     * 获取预约操作完成后返回的结果
     */
    public static JsonModel_BookReturn GetBookReturn(String JsonStr) throws Exception
    {
        return new JsonModel_BookReturn(JsonStr);
    }

    /**
     * JsonModel_Reservations转换到BookReturn
     */
    @Deprecated
    public static JsonModel_BookReturn Reservation2BookReturn(JsonModel_Reservations obj)
    {
        try
        {
            JsonModel_BookReturn temp = new JsonModel_BookReturn();
            temp.begin = obj.begin;
            temp.end = obj.end;
            temp.id = obj.id;
            temp.location = obj.location;
            temp.receipt = obj.receipt;
            temp.onDate = obj.onDate;
            return temp;
        } catch (Exception e)
        {
            Log.e("Reservation2BookReturn", e.getMessage());
            return null;
        }
    }

    /**
     * 获取预约历史的列表
     */
    public static JsonModel_HistoryList GetHistoryList(String JsonStr) throws Exception
    {
        return new JsonModel_HistoryList(JsonStr);
    }

    /**
     * 解析预约历史中的单个条目 这方法不经过Base 因为结构有点奇葩 没有固定的data code等字段
     * {
     * "id": 2550516,
     * "receipt": "0104-516-5",
     * "onDate": "2018 年 04 月 20 日",
     * "begin": "16 : 33",
     * "end": "22 : 00",
     * "location": "信息科学分馆1层西区一楼3C创客空间，座位号057"
     * }
     */
    public static JsonModel_BookReturn GetHistoryView(String JsonStr)
    {
        try
        {
            JSONObject Jobj = new JSONObject(JsonStr);
            JsonModel_BookReturn temp = new JsonModel_BookReturn();
            temp.id = Jobj.getInt("id");
            temp.receipt = Jobj.getString("receipt");
            temp.onDate = Jobj.getString("onDate");
            temp.begin = TimeHelp.GetTimeStructByID(Jobj.getString("begin"));
            temp.end = TimeHelp.GetTimeStructByID(Jobj.getString("end"));
            temp.location = Jobj.getString("location");
            return temp;
        } catch (Exception e)
        {
            Log.e("GetHistoryView", e.getMessage());
            return null;
        }

    }

    /**
     * 获取如下GET请求返回的JSon对象
     * GET http://seat.lib.whu.edu.cn/rest/v2/free/filters?token=8ZXSSXN3CC05154011 HTTP/1.1
     */
    public static JsonModel_Fliters GetFliters(String JsonStr) throws Exception
    {
        return new JsonModel_Fliters(JsonStr);
    }

    /**
     * 检查图书馆某个特定的馆的布局
     * http://seat.lib.whu.edu.cn/rest/v2/room/stats2/1?token=
     */
    public static JsonModel_HouseStats GetHouseStats(String JsonStr) throws Exception
    {
        return new JsonModel_HouseStats(JsonStr);
    }

    /**
     * 检查具体房间情况
     * http://seat.lib.whu.edu.cn/rest/v2/room/layoutByDate/14/2018-05-15?token=
     */
    public static JsonModel_RoomLayout GetRoomLayout(String JsonStr) throws Exception
    {
        return new JsonModel_RoomLayout(JsonStr);
    }


    /**
     * 获取座位的开始或结束时间
     */
    public static JsonModel_SeatTime GetSeatStartTime(String JsonStr) throws Exception
    {
        return new JsonModel_SeatTime(JsonStr);
    }


    /**
     * 解析座位筛选结果
     * @deprecated 替换为移动端过滤
     */
    @Deprecated
    public static JsonModel_WebFiltrate GetFiltrateSeats(String JsonStr)
    {
        return new JsonModel_WebFiltrate(JsonStr);
    }

    /**
     * 解析移动端座位筛选结果
     */
    public static JsonModel_MobileFiltrate GetMFiltrateSeats(String JsonStr) throws Exception
    {
        return new JsonModel_MobileFiltrate(JsonStr);
    }

    /**
     * 解析用户信息
     * @param JsonStr
     * @return
     */
    public static JsonModel_User GetUserInfo(String JsonStr) throws Exception
    {
        return new JsonModel_User(JsonStr);
    }


    /**
     * 判断当前时间是否是可用的
     */
    public static boolean IsTimeAvaliable(TimeItems.TimeStruct time, List<TimeItems.TimeStruct> timelist)
    {
        if (timelist == null || timelist.size() == 0)
            return false;

        if (timelist.contains(time))
            return true;
        else return false;
    }

    /**
     * 获取公告
     * @return 直接返回公告内容组成的字符串
     */
    public static String Getannounce(String JsonInfo) throws Exception
    {
        JsonModel_Base temp = new JsonModel_Base(JsonInfo);
        JSONObject dataobj = (JSONObject) temp.data;
        try
        {
            return dataobj.getString("announce");
        } catch (JSONException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 从自己的服务器 获取第二天信息
     *
     * @param Jsonstr
     * @return
     */
    public static JsonModel_Tomorrow GetTomorrowInfo(String Jsonstr) throws Exception
    {
        return new JsonModel_Tomorrow(Jsonstr);
    }


}




