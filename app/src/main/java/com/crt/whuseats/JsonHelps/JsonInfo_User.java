package com.crt.whuseats.JsonHelps;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonInfo_User extends JsonInfo_Base
{
    public int id;
    public boolean enabled;
    public String name;
    public String username;
    public String username2;
    public String userstatus;
    public String lastLogin;
    public boolean checkedIn;
    public String lastIn;
    public String lastOut;
    public int lastInBuildingId;
    public String lastInBuildingName;
    public int violationCount;
    public JsonInfo_User(String JsonStr)
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
