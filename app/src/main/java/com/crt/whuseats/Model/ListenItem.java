package com.crt.whuseats.Model;

import android.content.Context;
import android.util.Log;

import com.crt.whuseats.ApplicationV;
import com.crt.whuseats.Utils.TimeHelp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * ListenFragment 所使用的监听列表Model
 */
public class ListenItem implements Serializable
{

    public String Housename;
    public int HouseID;
    public String Location;
    public int RoomID;
    public ListenDateType dateType; //区分当前监听请求是今日还是明日
    public TimeItems.TimeStruct starttime;           //开始时间
    public TimeItems.TimeStruct availableStarttime;  //当前可用开始时间
    public TimeItems.TimeStruct endtime;             //结束时间
    public boolean IsEnable;        //是否启用

    public ListenItem(String housename, int houseID, String location, int roomID, ListenDateType dateType, TimeItems.TimeStruct starttime, TimeItems.TimeStruct endtime)
    {
        Housename = housename;
        HouseID = houseID;
        Location = location;
        RoomID = roomID;
        this.dateType = dateType;
        this.starttime = starttime;
        this.endtime = endtime;
        IsEnable = true;
    }

    /**
     * 获取起止时间连接字符串
     */
    public String getTimestr()
    {
        String ststr = starttime.Value;
        String etstr = endtime.Value;
        String date = "";
        if (dateType == ListenDateType.Today)
        {
            date = TimeHelp.GetTodayStr();
        } else date = TimeHelp.GetTomorrowStr();

        return date + "   " + ststr + "---" + etstr;
    }

    /**
     * 获取本Item的日期
     */
    public String getDate()
    {
        String date = "";
        if (dateType == ListenDateType.Today)
        {
            date = TimeHelp.GetTodayStr();
        } else date = TimeHelp.GetTomorrowStr();
        return date;
    }


    /**
     * 设置可用性
     *
     * @param b
     */
    public void SetEnable(boolean b)
    {
        IsEnable = b;
    }

    /**
     * 将ItemList存储至文件
     */
    public static void Save2File(Context context)
    {
        try
        {
            String filename = context.getFilesDir() + "ListenItemList.txt";
            ObjectOutputStream op = new ObjectOutputStream(new FileOutputStream(filename));
            op.writeObject(ApplicationV.ListenItemList);
            op.close();
        } catch (IOException e)
        {
            Log.e("Save2File: ", "存储监听链表出错" + e.getMessage());
        }
    }

    /**
     * 从文件中读取ItemList
     */
    public static void ReadFromFile(Context context)
    {
        try
        {
            String filename = context.getFilesDir() + "ListenItemList.txt";
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            ApplicationV.ListenItemList = (List<ListenItem>) in.readObject();
            in.close();
        } catch (Exception e)
        {
            Log.e("Save2File: ", "存储监听链表出错" + e.getMessage());
        }
    }
}

