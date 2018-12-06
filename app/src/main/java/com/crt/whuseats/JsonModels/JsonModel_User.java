package com.crt.whuseats.JsonModels;

import android.util.Log;

import org.json.JSONObject;

/**
 * 获取用户信息返回
 * {
 *     "status": "success",
 *     "data": {
 *         "id": 7402,
 *         "enabled": true,
 *         "name": "晁睿韬",
 *         "username": "2016301610110",
 *         "username2": null,
 *         "status": "NORMAL",
 *         "lastLogin": "2018-09-18T07:59:02.000",
 *         "checkedIn": false,
 *         "reservationStatus": null,
 *         "lastIn": null,
 *         "lastOut": null,
 *         "lastInBuildingId": null,
 *         "lastInBuildingName": null,
 *         "violationCount": 0
 *     },
 *     "message": "",
 *     "code": "0"
 * }
 * Update at v1.0 by crt 2018-12-4
 */
public class JsonModel_User extends JsonModel_Base
{
    private static final String TAG = "JsonModel_User";
    /**
     * 用户ID
     */
    public int id;
    /**
     * 是否启用
     */
    public boolean enabled;
    /**
     * 用户姓名
     */
    public String name;
    /**
     * 用户名 即学号
     */
    public String username;
    /**
     * 用户别名（暂时没看出来有啥用）
     */
    public String username2;
    /**
     * 用户状态
     */
    public String userstatus;
    /**
     * 上次登录事件描述状态
     */
    public String lastLogin;
    /**
     * 是否在馆内
     */
    public boolean checkedIn;
    /**
     * 上次进入馆内时间
     */
    public String lastIn;
    /**
     * 上次出馆时间
     */
    public String lastOut;
    /**
     * 上次进入的建筑ID
     */
    public int lastInBuildingId;
    /**
     * 上次进入的建筑名字
     */
    public String lastInBuildingName;
    /**
     * 近期违约次数
     */
    public int violationCount;

    public JsonModel_User(String JsonStr)
    {
        super(JsonStr);
        try
        {
            JSONObject dataObj=(JSONObject)data;
            id=dataObj.getInt("id");
            enabled=dataObj.getBoolean("enabled");
            name=dataObj.getString("name");
            username=dataObj.getString("username");
            username2=dataObj.getString("username2");
            userstatus=dataObj.getString("status");
            lastLogin=dataObj.getString("lastLogin");
            checkedIn=dataObj.getBoolean("checkedIn");
            lastIn=dataObj.getString("lastIn");
            lastOut=dataObj.getString("lastOut");
            lastInBuildingId=dataObj.getInt("lastInBuildingId");
            lastInBuildingName=dataObj.getString("lastInBuildingName");
            violationCount=dataObj.getInt("violationCount");
        }
        catch (Exception e)
        {
            Log.e("jsonInfo_User", e.toString() );
        }

    }
}
