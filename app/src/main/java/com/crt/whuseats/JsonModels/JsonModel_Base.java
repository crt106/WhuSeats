package com.crt.whuseats.JsonModels;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

//Json信息基类
public class JsonModel_Base implements Serializable
{
    private static final String TAG = "JsonModel_Base";
    /**
     * 返回码
     */
    public int code;
    /**
     * 数据--这可能是个Json数组，也可能是个对象 有点迷 交给子类自己去处理
     */
    public Object data;
    /**
     * 最外层消息
     */
    public String message;
    /**
     * 最外层状态
     */
    public String status;

    //构造方法 直接在构造方法里面解析？不然子类无法获得上述基本的四个字段
    public JsonModel_Base(String JsonStr) throws Exception
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

    public JsonModel_Base()
    {

    }

    /**
     * 利用Gson进行转换为字符串
     * @return
     */
    public String toString()
    {
        Gson gson=new Gson();
        return gson.toJson(this);
    }


}
