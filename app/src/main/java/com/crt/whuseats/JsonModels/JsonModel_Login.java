package com.crt.whuseats.JsonModels;

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
public class JsonModel_Login extends JsonModel_Base
{
    private static final String TAG = "JsonModel_Login";
    //token包含在data层
    public String token;

    public JsonModel_Login(String JsonStr)
    {
        super(JsonStr);
        try
        {
            token=((JSONObject)data).getString("token");
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }

    }
}
