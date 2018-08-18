package com.crt.whuseats.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**提供时间字符串和对应的服务器编码时间的转换 以及日期的获取和转换
 1350	22:30
 1320	22:00
 1290	21:30
 1260	21:00
 1230	20:30
 1200	20:00
 1170	19:30
 1140	19:00
 1110	18:30
 1080	18:00
 1050	17:30
 1020	17:00
 990	16:30
 960	16:00
 930	15:30
 900	15:00
 870	14:30
 840	14:00
 810	13:30
 780	13:00
 750	12:30
 720	12:00
 690	11:30
 660	11:00
 630	10:30
 600	10:00
 570	9:30
 540	9:00
 510	8:30
 480	8:00
 */
public class TimeHelp
{
    public static Map<Integer,String> ID2Value=new HashMap<>();
    public static Map<String,Integer> Value2ID=new HashMap<>();
    public static final int TIME_MIN=480;
    public static final int TIME_MAX=1350;
    public static final int TIME_INTERVAL=30;

    public TimeHelp()
    {
        ID2Value.clear();
        Value2ID.clear();

        ID2Value.put(480, "8:00");
        ID2Value.put(510, "8:30");
        ID2Value.put(540, "9:00");
        ID2Value.put(570, "9:30");
        ID2Value.put(600, "10:00");
        ID2Value.put(630, "10:30");
        ID2Value.put(660, "11:00");
        ID2Value.put(690, "11:30");
        ID2Value.put(720, "12:00");
        ID2Value.put(750, "12:30");
        ID2Value.put(780, "13:00");
        ID2Value.put(810, "13:30");
        ID2Value.put(840, "14:00");
        ID2Value.put(870, "14:30");
        ID2Value.put(900, "15:00");
        ID2Value.put(930, "15:30");
        ID2Value.put(960, "16:00");
        ID2Value.put(990, "16:30");
        ID2Value.put(1020, "17:00");
        ID2Value.put(1050, "17:30");
        ID2Value.put(1080, "18:00");
        ID2Value.put(1110, "18:30");
        ID2Value.put(1140, "19:00");
        ID2Value.put(1170, "19:30");
        ID2Value.put(1200, "20:00");
        ID2Value.put(1230, "20:30");
        ID2Value.put(1260, "21:00");
        ID2Value.put(1290, "21:30");
        ID2Value.put(1320, "22:00");
        ID2Value.put(1350, "22:30");

        Value2ID.put("8:00", 480);
        Value2ID.put("8:30", 510);
        Value2ID.put("9:00", 540);
        Value2ID.put("9:30", 570);
        Value2ID.put("10:00", 600);
        Value2ID.put("10:30", 630);
        Value2ID.put("11:00", 660);
        Value2ID.put("11:30", 690);
        Value2ID.put("12:00", 720);
        Value2ID.put("12:30", 750);
        Value2ID.put("13:00", 780);
        Value2ID.put("13:30", 810);
        Value2ID.put("14:00", 840);
        Value2ID.put("14:30", 870);
        Value2ID.put("15:00", 900);
        Value2ID.put("15:30", 930);
        Value2ID.put("16:00", 960);
        Value2ID.put("16:30", 990);
        Value2ID.put("17:00", 1020);
        Value2ID.put("17:30", 1050);
        Value2ID.put("18:00", 1080);
        Value2ID.put("18:30", 1110);
        Value2ID.put("19:00", 1140);
        Value2ID.put("19:30", 1170);
        Value2ID.put("20:00", 1200);
        Value2ID.put("20:30", 1230);
        Value2ID.put("21:00", 1260);
        Value2ID.put("21:30", 1290);
        Value2ID.put("22:00", 1320);
        Value2ID.put("22:30", 1350);
    }

    /**
     *获取今天的日期字符串 例如2018-08-25
     */
    public static String GetTodayStr()
    {
        Calendar ca=Calendar.getInstance();
        int year=ca.get(Calendar.YEAR);
        int month=ca.get(Calendar.MONTH)+1; //月份从0开始计算 所以要+1
        int day=ca.get(Calendar.DATE);
        String Today,monthstr,daystr;
        if(month<10)
            monthstr="0"+month;
        else
            monthstr= Integer.toString(month);
        if(day<10)
            daystr="0"+day;
        else
            daystr=Integer.toString(day);
        Today=year+"-"+monthstr+"-"+daystr;
        return Today;
    }

    /**
     * 获取明天的日期字符串 例如2018-08-25
     * @return
     */
    public static String GetTomorrowStr()
    {
        Calendar ca=Calendar.getInstance();
        String Tomorrow,monthstr_y,daystr_y;
        ca.add(Calendar.DATE,1);
        int year_y=ca.get(Calendar.YEAR);
        int month_y=ca.get(Calendar.MONTH)+1;
        int day_y=ca.get(Calendar.DATE);
        if(month_y<10)
            monthstr_y="0"+month_y;
        else
            monthstr_y= Integer.toString(month_y);
        if(day_y<10)
            daystr_y="0"+day_y;
        else
            daystr_y=Integer.toString(day_y);
        Tomorrow=year_y+"-"+monthstr_y+"-"+daystr_y;
       return Tomorrow;
    }

    //获取监听座位的合法的日期列表
    public static List<String> GetLegalTimeList()
    {
        //用于返回的链表
        List<String> DateList=new LinkedList<>();
        Calendar ca=Calendar.getInstance();
        int hour=ca.get(Calendar.HOUR_OF_DAY);
        int min=ca.get(Calendar.MINUTE);

        //如果超过了22:00则无法预约当天座位咯~
        if(hour<22)
        DateList.add(GetTodayStr());
        else
        //如果超过了22:30则可以查询第二天的座位咯~
        DateList.add(GetTomorrowStr());

        return DateList;
    }

    //获取到预约时间的剩余毫秒数量
    public static long GetBookLeftTime()
    {
        Date curDate=new Date(System.currentTimeMillis());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try
        {
            Date furDate=sdf.parse(GetTodayStr()+" 22:45");
            return furDate.getTime()-curDate.getTime();
        }
        catch (Exception e)
        {
            Log.e("getBookltime", e.toString() );
            return 0;
        }

    }

    //获取预约时间(22:45)
    public static long GetBookTime()
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try
        {
            Date furDate=sdf.parse(GetTodayStr()+" 22:45");
            return furDate.getTime();
        }
        catch (Exception e)
        {
            Log.e("getBookltime", e.toString() );
            return 0;
        }
    }

    /**
     * 判断是不是超过某个时间
     * @param hhmm 形如"22:45"的字符串
     * @return
     */
    public static boolean IsOverTime(String hhmm)
    {
        try
        {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date nowDate=new Date(System.currentTimeMillis());
            Date maxDate=sdf.parse(TimeHelp.GetTodayStr()+" "+hhmm);
            return nowDate.getTime()<maxDate.getTime()? false:true;
        }
        catch (ParseException e)
        {
            Log.e("TimeHelp",e.toString() );
            return false;
        }
    }
}
