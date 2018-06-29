   package com.crt.whuseats.JsonHelps;

   //提供Json解析能力的帮助类 其实这个类好像没什么卵用？？

   import android.util.Log;

   import org.json.JSONException;
   import org.json.JSONObject;

   import java.util.Collection;
   import java.util.Collections;
   import java.util.List;

   public class JsonHelp
   {
       /**
        * 获取如下GET请求返回的JSon对象
        * GET http://seat.lib.whu.edu.cn/rest/auth?username={0}&password={1} HTTP/1.1
        */
       public static JsonInfo_Login GetLogin(String JsonStr)
       {
           return new JsonInfo_Login(JsonStr);
       }

       /**
        * 获取如下GET请求返回的JSon对象
        * GET http://seat.lib.whu.edu.cn/rest/v2/user/reservations?token=8ZXSSXN3CC05154011 HTTP/1.1
        */
       public static JsonInfo_Reservations GetReservation(String JsonStr)
       {
           return new JsonInfo_Reservations(JsonStr);
       }
       /**
        * 获取预约操作完成后返回的结果
        */
       public static JsonInfo_BookReturn GetBookReturn(String JsonStr)
       {
           return new JsonInfo_BookReturn(JsonStr);
       }

       /**
        *JsonInfo_Reservations转换到BookReturn
        */
       public static JsonInfo_BookReturn Reservation2BookReturn(JsonInfo_Reservations obj)
       {
           try
           {
               JsonInfo_BookReturn temp=new JsonInfo_BookReturn();
               temp.begin=obj.begin;
               temp.end=obj.end;
               temp.id=obj.id;
               temp.location=obj.location;
               temp.receipt=obj.receipt;
               temp.onDate=obj.onDate;
               return temp;
           }
           catch (Exception e)
           {
               Log.e("Reservation2BookReturn", e.getMessage());
               return null;
           }
       }

       /**
        * 获取预约历史的列表
        */
       public static JsonInfo_HistoryList GetHistoryList(String JsonStr)
       {
           return new JsonInfo_HistoryList(JsonStr);
       }

       /**
        * 解析预约历史中的单个条目 这方法不经过JsoninfoBase 因为结构有点奇葩 没有固定的data code等字段
        * {
          "id": 2550516,
          "receipt": "0104-516-5",
          "onDate": "2018 年 04 月 20 日",
          "begin": "16 : 33",
          "end": "22 : 00",
          "location": "信息科学分馆1层西区一楼3C创客空间，座位号057"
          }
        */
       public static JsonInfo_BookReturn GetHistoryView(String JsonStr)
       {
           try
           {
               JSONObject Jobj=new JSONObject(JsonStr);
               JsonInfo_BookReturn temp=new JsonInfo_BookReturn();
               temp.id=Jobj.getInt("id");
               temp.receipt=Jobj.getString("receipt");
               temp.onDate=Jobj.getString("onDate");
               temp.begin=Jobj.getString("begin");
               temp.end=Jobj.getString("end");
               temp.location=Jobj.getString("location");
               return temp;
           }
           catch (Exception e)
           {
              Log.e("GetHistoryView", e.getMessage());
              return null;
           }

       }

       /**
        * 获取如下GET请求返回的JSon对象
        * GET http://seat.lib.whu.edu.cn/rest/v2/free/filters?token=8ZXSSXN3CC05154011 HTTP/1.1
        */
       public static JsonInfo_Fliters GetFliters(String JsonStr)
       {
           return new JsonInfo_Fliters(JsonStr);
       }

       /**
        * 检查图书馆某个特定的馆的布局
        * http://seat.lib.whu.edu.cn/rest/v2/room/stats2/1?token=
        */
       public static JsonInfo_HouseStats GetHouseStats(String JsonStr)
       {
           return new JsonInfo_HouseStats(JsonStr);
       }

       /**
        * 检查具体房间情况
        * http://seat.lib.whu.edu.cn/rest/v2/room/layoutByDate/14/2018-05-15?token=
        */
       public static JsonInfo_RoomLayout GetRoomLayout(String JsonStr)
       {
           return new JsonInfo_RoomLayout(JsonStr);
       }

       /**
        * 对座位布局重新按照名称排序
        */

       /**
        * 获取座位的开始时间
        */
       public static JsonInfo_SeatTime_Strat GetSeatStartTime(String JsonStr)
       {
           return new JsonInfo_SeatTime_Strat(JsonStr);
       }

       /**
        * 获取座位的结束时间
        */
       public static JsonInfo_SeatTime_End GetSeatEndTime(String JsonStr)
       {
           return new JsonInfo_SeatTime_End(JsonStr);
       }

       /**
        * 解析座位筛选结果
        */
       @Deprecated
       public static JsonInfo_WebFiltrate GetFiltrateSeats(String JsonStr)
       {
           return new JsonInfo_WebFiltrate(JsonStr);
       }

       /**
        * 解析移动端座位筛选结果
        */
       public static JsonInfo_MobileFiltrate GetMFiltrateSeats(String JsonStr)
       {
           return new JsonInfo_MobileFiltrate(JsonStr);
       }

       public static JsonInfo_User GetUserInfo(String JsonStr)
       {
           return new JsonInfo_User(JsonStr);
       }


       /**
        * 判断当前时间是否是可用的
        */
       public static boolean IsTimeAvaliable(String timeID,List<String> timelist)
       {
           if(timelist==null||timelist.size()==0)
               return false;

           if(timelist.contains(timeID))
                return true;
           else return false;
       }

       /**
        * 获取公告
        */
       public static String Getannounce(String JsonInfo)
       {
           JsonInfo_Base temp=new JsonInfo_Base(JsonInfo);
           JSONObject dataobj=(JSONObject)temp.data;
           try
           {
               return dataobj.getString("announce");
           }
           catch (JSONException e)
           {
               e.printStackTrace();
               return "";
           }
       }



   }




