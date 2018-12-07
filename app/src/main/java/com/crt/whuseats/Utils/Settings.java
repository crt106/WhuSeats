package com.crt.whuseats.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.crt.whuseats.ApplicationV;
import com.crt.whuseats.Model.TimeItems;

import org.jsoup.select.Evaluator;

/**
 * 控制本程序各项保存在SharedPreferences的设置
 * 在1.0里将所有项目设置单列为方法，避免混用
 * Updated at v1.0
 */
public class Settings
{
    private static final String TAG = "Settings";
    private static SharedPreferences.Editor UserInfoEditor;      //控制用户名与密码的设置编辑器
    private static SharedPreferences UserInfo;
    private static SharedPreferences.Editor ListenSettingEditor;   //控制监听的设置编辑器
    private static SharedPreferences ListenSetting;
    private static SharedPreferences.Editor BookSettingEditor;     //控制预约信息的编辑器
    private static SharedPreferences BookSetting;

    public Settings()
    {
        //利用BaseActivity来获取相应的Preference文件
        UserInfoEditor = ApplicationV.ApplicationContext.getSharedPreferences("userdata", Context.MODE_PRIVATE).edit();
        UserInfo = ApplicationV.ApplicationContext.getSharedPreferences("userdata", Context.MODE_PRIVATE);
        ListenSettingEditor = ApplicationV.ApplicationContext.getSharedPreferences("listendata", Context.MODE_PRIVATE).edit();
        ListenSetting = ApplicationV.ApplicationContext.getSharedPreferences("listendata", Context.MODE_PRIVATE);
        BookSettingEditor = ApplicationV.ApplicationContext.getSharedPreferences("bookdata", Context.MODE_PRIVATE).edit();
        BookSetting = ApplicationV.ApplicationContext.getSharedPreferences("bookdata", Context.MODE_PRIVATE);
    }

    //region 控制用户信息的读写
    public String GetUserName()
    {
        return UserInfo.getString("Username", "");
    }

    public void SetUserName(String Username)
    {
        UserInfoEditor.putString("Username", Username);
        UserInfoEditor.apply();
    }

    public String GetPassWord()
    {
        return UserInfo.getString("Password", "");
    }

    public void SetPassWord(String Password)
    {
        UserInfoEditor.putString("Password", Password);
        UserInfoEditor.apply();
    }

    /**
     * 清除用户信息
     */
    public void ClearUserInfo()
    {
        UserInfoEditor.clear();
        UserInfoEditor.apply();
    }
    //endregion

    //region 控制监听信息的读写

    //默认所在建筑ID
    public int GetDefaultBuilding()
    {
        return ListenSetting.getInt("DefaultBuilding",1 );
    }
    public void SetDefaultBuilding(int ID)
    {
        ListenSettingEditor.putInt("DefaultBuilding", ID);
        ListenSettingEditor.apply();
    }

    //发送预约前延迟
    public int GetDELAY_SENDBOOK()
    {
       return ListenSetting.getInt("DELAY_SENDBOOK",ApplicationV.DELAY_SENDBOOK_DEFAULT);
    }

    public void SetDELAY_SENDBOOK(int delay)
    {
        ListenSettingEditor.putInt("DELAY_SENDBOOK", delay);
        ListenSettingEditor.apply();
    }

    //最大循环次数
    public int GetMAXLOOPCOUNT()
    {
        return ListenSetting.getInt("MAXLOOPCOUNT",ApplicationV.MAXLOOPCOUNT);
    }

    public void SetMAXLOOPCOUNT(int count)
    {
        ListenSettingEditor.putInt("MAXLOOPCOUNT", count);
        ListenSettingEditor.apply();
    }

    //过滤前延迟
    public int GetDELAY_BEFOREFILTRATE()
    {
        return ListenSetting.getInt("DELAY_BEFOREFILTRATE",ApplicationV.DELAY_BEFOREFILTRATE);
    }

    public void SetDELAY_BEFOREFILTRATE(int count)
    {
        ListenSettingEditor.putInt("DELAY_BEFOREFILTRATE", count);
        ListenSettingEditor.apply();
    }

    //开始时间 存储值为ID(480等)
    public TimeItems.TimeStruct GetstartTime()
    {
        String ID=ListenSetting.getString("startTime","480");
        return TimeHelp.GetTimeStructByID(ID);
    }
    public void SetstartTime(TimeItems.TimeStruct time)
    {
        ListenSettingEditor.putString("startTime", time.ID);
        ListenSettingEditor.apply();
    }

    //结束时间 存储值为ID
    public TimeItems.TimeStruct GetendTime()
    {
        String ID=ListenSetting.getString("endTime","480");
        return TimeHelp.GetTimeStructByID(ID);
    }
    public void SetendTime(TimeItems.TimeStruct time)
    {
        ListenSettingEditor.putString("endTime", time.ID);
        ListenSettingEditor.apply();
    }


    //endregion

    //region 控制提前预约信息的读写

    //选择的座位唯一ID
    public int GetChooseSeatID()
    {
        return BookSetting.getInt("ChooseSeatID",0);
    }
    public void setChooseSeatID(int chooseSeatID)
    {
        BookSettingEditor.putInt("ChooseSeatID",chooseSeatID );
        BookSettingEditor.apply();
    }

    //选择的房间ID
    public int GetChooseRoomID()
    {
        return BookSetting.getInt("ChooseRoomID",0);
    }
    public void setChooseRoomID(int chooseRoomID)
    {
        BookSettingEditor.putInt("ChooseRoomID",chooseRoomID );
        BookSettingEditor.apply();
    }

    //选择的建筑ID
    public int GetChooseHouseID()
    {
        return BookSetting.getInt("ChooseHouseID",0);
    }
    public void setChooseHouseID(int chooseHouseID)
    {
        BookSettingEditor.putInt("ChooseHouseID",chooseHouseID );
        BookSettingEditor.apply();
    }

    //选择的房间名称
    public String GetChooseRoomName()
    {
        return BookSetting.getString("ChooseRoomName","");
    }
    public void setChooseRoomName(String chooseRoomName)
    {
        BookSettingEditor.putString("ChooseRoomName",chooseRoomName );
        BookSettingEditor.apply();
    }

    //选择的房屋名称
    public String GetChooseHouseName()
    {
        return BookSetting.getString("ChooseHouseName","");
    }
    public void setChooseHouseName(String chooseHouseName)
    {
        BookSettingEditor.putString("ChooseHouseName",chooseHouseName );
        BookSettingEditor.apply();
    }

    //选择的座位名称(001 002 003)
    public String GetChooseSeatName()
    {
        return BookSetting.getString("ChooseSeatName","");
    }
    public void setChooseSeatName(String chooseSeatName)
    {
        BookSettingEditor.putString("ChooseSeatName",chooseSeatName );
        BookSettingEditor.apply();
    }

    //选择的座位名称(001 002 003)
    public boolean GetIsClockon()
    {
        return BookSetting.getBoolean("IsClockon",false);
    }
    public void setIsClockon(boolean isClockon)
    {
        BookSettingEditor.putBoolean("ChooseSeatName",isClockon );
        BookSettingEditor.apply();
    }
    //endregion

}
