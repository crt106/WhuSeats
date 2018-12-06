package com.crt.whuseats.Model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 存放各个格式的时间的静态对象
 * Created on v1.0 by crt 2018-12-4
 */
public class TimeItems
{
    private static List<TimeStruct> times=new LinkedList<>();
    public static Map<String,TimeStruct> IDMap=new HashMap();
    public static Map<String,TimeStruct> ValueMap=new HashMap();

    public static TimeStruct Time_8_00;
    public static TimeStruct Time_8_30;
    public static TimeStruct Time_9_00;
    public static TimeStruct Time_9_30;
    public static TimeStruct Time_10_00;
    public static TimeStruct Time_10_30;
    public static TimeStruct Time_11_00;
    public static TimeStruct Time_11_30;
    public static TimeStruct Time_12_00;
    public static TimeStruct Time_12_30;
    public static TimeStruct Time_13_00;
    public static TimeStruct Time_13_30;
    public static TimeStruct Time_14_00;
    public static TimeStruct Time_14_30;
    public static TimeStruct Time_15_00;
    public static TimeStruct Time_15_30;
    public static TimeStruct Time_16_00;
    public static TimeStruct Time_16_30;
    public static TimeStruct Time_17_00;
    public static TimeStruct Time_17_30;
    public static TimeStruct Time_18_00;
    public static TimeStruct Time_18_30;
    public static TimeStruct Time_19_00;
    public static TimeStruct Time_19_30;
    public static TimeStruct Time_20_00;
    public static TimeStruct Time_20_30;
    public static TimeStruct Time_21_00;
    public static TimeStruct Time_21_30;
    public static TimeStruct Time_22_00;
    public static TimeStruct Time_22_30;
    public static TimeStruct Time_Now;

    static
    {
        times.clear();

        Time_8_00=new TimeStruct("480", "8:00");
        Time_8_30=new TimeStruct("510", "8:30");
        Time_9_00=new TimeStruct("540", "9:00");
        Time_9_30=new TimeStruct("570", "9:30");
        Time_10_00=new TimeStruct("600", "10:00");
        Time_10_30=new TimeStruct("630", "10:30");
        Time_11_00=new TimeStruct("660", "11:00");
        Time_11_30=new TimeStruct("690", "11:30");
        Time_12_00=new TimeStruct("720", "12:00");
        Time_12_30=new TimeStruct("750", "12:30");
        Time_13_00=new TimeStruct("780", "13:00");
        Time_13_30=new TimeStruct("810", "13:30");
        Time_14_00=new TimeStruct("840", "14:00");
        Time_14_30=new TimeStruct("870", "14:30");
        Time_15_00=new TimeStruct("900", "15:00");
        Time_15_30=new TimeStruct("930", "15:30");
        Time_16_00=new TimeStruct("960", "16:00");
        Time_16_30=new TimeStruct("990", "16:30");
        Time_17_00=new TimeStruct("1020", "17:00");
        Time_17_30=new TimeStruct("1050", "17:30");
        Time_18_00=new TimeStruct("1080", "18:00");
        Time_18_30=new TimeStruct("1110", "18:30");
        Time_19_00=new TimeStruct("1140", "19:00");
        Time_19_30=new TimeStruct("1170", "19:30");
        Time_20_00=new TimeStruct("1200", "20:00");
        Time_20_30=new TimeStruct("1230", "20:30");
        Time_21_00=new TimeStruct("1260", "21:00");
        Time_21_30=new TimeStruct("1290", "21:30");
        Time_22_00=new TimeStruct("1320", "22:00");
        Time_22_30=new TimeStruct("1350", "22:30");
        Time_Now = new TimeStruct("now","现在" );

        times.add(Time_8_00);
        times.add(Time_8_30);
        times.add(Time_9_00);
        times.add(Time_9_30);
        times.add(Time_10_00);
        times.add(Time_10_30);
        times.add(Time_11_00);
        times.add(Time_11_30);
        times.add(Time_12_00);
        times.add(Time_12_30);
        times.add(Time_13_00);
        times.add(Time_13_30);
        times.add(Time_14_00);
        times.add(Time_14_30);
        times.add(Time_15_00);
        times.add(Time_15_30);
        times.add(Time_16_00);
        times.add(Time_16_30);
        times.add(Time_17_00);
        times.add(Time_17_30);
        times.add(Time_18_00);
        times.add(Time_18_30);
        times.add(Time_19_00);
        times.add(Time_19_30);
        times.add(Time_20_00);
        times.add(Time_20_30);
        times.add(Time_21_00);
        times.add(Time_21_30);
        times.add(Time_22_00);
        times.add(Time_22_30);

        for(TimeStruct time:times)
        {
            IDMap.put(time.ID, time);
            ValueMap.put(time.Value, time);
        }

    }


    /**
     * 时间格式数据结构
     */
    public static class TimeStruct
    {
        /**
         * 诸如480 1350的数字ID 用于向服务器请求时调用
         */
        public String ID;

        /**
         * 诸如10:00、13:30这样的显示字符串
         */
        public String Value;

        /**
         * 获取ID转Integer之后的值 用于
         * @return
         */
        public Integer GetID_Interger()
        {
            return Integer.parseInt(ID);
        }

        public TimeStruct(String ID, String value)
        {
            this.ID = ID;
            Value = value;
        }
    }
}
