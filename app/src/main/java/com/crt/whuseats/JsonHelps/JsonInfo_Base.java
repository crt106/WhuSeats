package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

//Json信息基类
public class JsonInfo_Base implements Serializable
{
    public int code;         //返回码
    public Object data;   //数据--这可能是个Json数组，也可能是个对象 有点迷 交给子类自己去处理
    public String message;   //消息
    public String status;    //状态

    //构造方法 直接在构造方法里面解析？不然子类无法获得上述基本的四个字段
    public JsonInfo_Base(String JsonStr)
    {
        try
        {
            if(JsonStr==null||JsonStr.equals(""))
            {
                throw new Exception("给定的Json字符串有问题吧大哥");
            }
            JSONObject job=new JSONObject(JsonStr);
            code=job.getInt("code");
            data=job.get("data");
            message=job.getString("message");
            status=job.getString("status");
        }
        catch (Exception e)
        {
            Log.e("Json", e.getMessage());
        }

    }

    public JsonInfo_Base()
    {

    }


}
