package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONObject;

/**
 * 解析服务器返回的登陆JSon
 * 例子：
 * {"status":"success",
 * "data":{"token":"8ZXSSXN3CC05154011"},
 * "code":"0",
 * "message":""}
 */
public class JsonInfo_Login extends JsonInfo_Base
{
    //token包含在data层
    public String token;

    public JsonInfo_Login(String JsonStr)
    {
        super(JsonStr);
        try
        {
            token=((JSONObject)data).getString("token");
        }
        catch (Exception e)
        {
            Log.e("Jsoninfo_login", e.getMessage());
        }

    }
}
