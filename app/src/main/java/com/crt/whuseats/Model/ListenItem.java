package com.crt.whuseats.Model;

import android.content.Context;
import android.util.Log;

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
 * ListenFragment_old 所使用的监听列表
 */
public class ListenItem implements Serializable
{

    public static List<ListenItem> ItemList=new LinkedList<>();

    public String Housename;
    public int HouseID;
    public String Location;
    public int RoomID;
    public ListenDateType dateType;
    public int starttime;     //这个是开始时间的数字形式
    public int endtime;       //这个是结束时间的数字形式
    public boolean IsEnable;  //是否启用

    public ListenItem(String housename, int houseID, String location, int roomID, ListenDateType dateType, int starttime, int endtime)
    {
        Housename = housename;
        HouseID = houseID;
        Location = location;
        RoomID = roomID;
        this.dateType = dateType;
        this.starttime = starttime;
        this.endtime = endtime;
        IsEnable=true;
    }

    /**
     * 获取起止时间连接字符串
     */
    public String getTimestr()
    {
        TimeHelp tmptimehelp=new TimeHelp();
        String ststr= TimeHelp.ID2Value.get(starttime);
        String etstr=TimeHelp.ID2Value.get(endtime);
        String date="";
        if(dateType==ListenDateType.Today)
        {
            date=TimeHelp.GetTodayStr();
        }
        else date=TimeHelp.GetTomorrowStr();

        return date+"   "+ststr+"---"+etstr;
    }

    /**
     * 获取本Item的日期
     */
    public String getDate()
    {
        String date="";
        if(dateType==ListenDateType.Today)
        {
            date=TimeHelp.GetTodayStr();
        }
        else date=TimeHelp.GetTomorrowStr();
        return date;
    }


    /**
     * 设置可用性
     * @param b
     */
    public void SetEnable(boolean b)
    {
        IsEnable=b;
    }
    /**
     * 将ItemList存储至文件
     */
    public static void Save2File(Context context)
    {
        try
        {
            String filename=context.getFilesDir()+"ListenItemList.txt";
            ObjectOutputStream op=new ObjectOutputStream(new FileOutputStream(filename));
            op.writeObject(ItemList);
            op.close();
        } catch (IOException e)
        {
            Log.e("Save2File: ", "存储监听链表出错"+e.getMessage());
        }
    }

    /**
     * 从文件中读取ItemList
     */
    public static void ReadFromFile(Context context)
    {
        try
        {
            String filename=context.getFilesDir()+"ListenItemList.txt";
            ObjectInputStream in=new ObjectInputStream(new FileInputStream(filename));
            ItemList=(List<ListenItem>) in.readObject();
            in.close();
        } catch (Exception e)
        {
            Log.e("Save2File: ", "存储监听链表出错"+e.getMessage());
        }
    }
}

