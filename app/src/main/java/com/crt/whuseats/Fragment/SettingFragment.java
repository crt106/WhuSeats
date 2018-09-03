package com.crt.whuseats.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.Activity.MainActivity;
import com.crt.whuseats.Activity.MoreFucActivity;
import com.crt.whuseats.Activity.WebViewActivity;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.JsonHelps.JsonHelp;
import com.crt.whuseats.Service.NetService;
import com.crt.whuseats.R;

/**
 * A simple {@link Fragment} subclass.
 * 控制App相关设置的Fragment
 */
public class SettingFragment extends Fragment
{

    //与Activity的连接

    public MainActivity ActivityConnect;

//    public EditText et_newSeatDelay;
//    public EditText et_startTimeDelay;
//    public EditText et_endTimeDelay;
    public EditText et_bookDelay;
    public EditText et_DelayFiltrate;
    public EditText et_MaxLoop;
    public Button SaveDelaySetting;
    public View LearnAboutus;
    public View MoreInfo;
    public View Announce;
    private View morefuction;




    public SettingFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ActivityConnect=(MainActivity)getActivity();
        return  inflater.inflate(R.layout.frag_setting,container,false );
    }


    //在活动创建后调用控件以及注册啥的

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //获取控件
//        et_newSeatDelay=(EditText)getView().findViewById(R.id.et_Delay_newSeats);
//        et_startTimeDelay=(EditText)getView().findViewById(R.id.et_Delay_StartTime);
//        et_endTimeDelay=(EditText)getView().findViewById(R.id.et_Delay_EndTime);
        et_bookDelay=(EditText)getView().findViewById(R.id.et_Delay_Book);
        et_DelayFiltrate = (EditText) getView().findViewById(R.id.et_Delay_Filtrate);
        et_MaxLoop = (EditText)getView().findViewById(R.id.et_MaxLoop);
        SaveDelaySetting=(Button)getView().findViewById(R.id.btn_SaveDelaySetting);
        LearnAboutus=getView().findViewById(R.id.ll_us);
        MoreInfo=getView().findViewById(R.id.instruction);
        Announce=getView().findViewById(R.id.announce);
        morefuction = getView().findViewById(R.id.morefuction);

        SaveDelaySetting.setOnClickListener(SaveDelaySettingClick);
        LearnAboutus.setOnClickListener(learnusClick);
        MoreInfo.setOnClickListener(moreInfoClick);
        Announce.setOnClickListener(AnnounceClick);
        morefuction.setOnClickListener(morefucClick);


        CheckIfNewVersion();
        ImportDelaySetting();
        //保存当前版本号
        BaseActivity.AppSetting.ListenSettingEditor.putInt("AppVersion",BaseActivity.VERSIONCODE);
    }

    //导入已经保存的设置
    public void ImportDelaySetting()
    {

        try
        {
            //五个延时
            NetService.DELAY_SENDBOOK =BaseActivity.AppSetting.ListenSetting.getInt("DELAY_SENDBOOK",NetService.DELAY_SENDBOOK_DEFAULT);
            NetService.DELAY_BEFOREFILTRATE =BaseActivity.AppSetting.ListenSetting.getInt("DELAY_BEFOREFILTRATE",NetService.DELAY_BEFOREFILTRATE_DEFAULT);
            //最大循环次数
            ListenFragment.MAXLOOPCOUNT =BaseActivity.AppSetting.ListenSetting.getInt("MAXLOOPCOUNT", ListenFragment_old.MAXLOOPCOUT_DEFAULT);

            //显示文字
            et_bookDelay.setText(Integer.toString(NetService.DELAY_SENDBOOK));
            et_DelayFiltrate.setText(Integer.toString(NetService.DELAY_BEFOREFILTRATE));
            et_MaxLoop.setText(Integer.toString(ListenFragment.MAXLOOPCOUNT));
        }
        catch (Exception e)
        {
            Log.e("SettingFragment", e.getMessage());
        }
    }

    //保存延迟的设置
    public View.OnClickListener SaveDelaySettingClick=(View v)->
    {
        try
        {

            NetService.DELAY_SENDBOOK =Integer.parseInt(et_bookDelay.getText().toString());
            NetService.DELAY_BEFOREFILTRATE =Integer.parseInt(et_DelayFiltrate.getText().toString());
            ListenFragment_old.MAXLOOPCOUNT =Integer.parseInt(et_MaxLoop.getText().toString());

            BaseActivity.AppSetting.ListenSettingEditor.putInt("DELAY_SENDBOOK",NetService.DELAY_SENDBOOK);
            BaseActivity.AppSetting.ListenSettingEditor.putInt("DELAY_BEFOREFILTRATE",NetService.DELAY_BEFOREFILTRATE);
            BaseActivity.AppSetting.ListenSettingEditor.putInt("MAXLOOPCOUNT", ListenFragment_old.MAXLOOPCOUNT);

            BaseActivity.AppSetting.ListenSettingEditor.apply();
            Toast.makeText(ActivityConnect,"保存成功" ,Toast.LENGTH_SHORT ).show();
        } catch (NumberFormatException e)
        {
            Log.e("SettingFragment", e.getMessage());
        }
    };

    //region关于按钮的响应

    //关于按钮相应
    public View.OnClickListener learnusClick=(v)->
    {

        AlertDialog tempdialog=new AlertDialog.Builder(ActivityConnect)
                .setTitle("关于？什么关于")
                .setMessage("炒鸡辣鸡的抢座系统v" +BaseActivity.VERSIONNAME+"("+BaseActivity.VERSIONCODE+")"+
                        "\n Madeby crt106\n我欲升天 法力无边")
                .setPositiveButton("加入QQ群",(DialogInterface dialog, int which)->
                {
                    dialog.dismiss();
                    joinQQGroup();
                })
                .setNegativeButton("溜了溜了", null)
                .create();
        tempdialog.show();
    };

    //说明书点击按钮事件
    public View.OnClickListener moreInfoClick=(v)->{

        Intent intent=new Intent(ActivityConnect, WebViewActivity.class);
        intent.putExtra("Uri","http://120.79.7.230/WhuSeats_info.html");
        startActivity(intent);
    };

    //公告点击按钮
    public View.OnClickListener AnnounceClick=(v)->{
        ActivityConnect.mbinder.GetAnnounce(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                String announce= JsonHelp.Getannounce((String)data[0]);
                if(announce.contains("请勿使用程序脚本等非正常方式使用本系统"))
                {
                    announce+="\n看到没有 叫你不要用脚本程序!";
                }
                AlertDialog temp=new AlertDialog.Builder(ActivityConnect)
                        .setTitle("公告")
                        .setMessage(announce)
                        .create();
                temp.show();
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        });

    };

    //更多功能点击按钮
    public View.OnClickListener morefucClick=v->{
        Intent intent=new Intent(ActivityConnect, MoreFucActivity.class);
        startActivity(intent);
    };

    //endregion

    //如果是刚刚更新新版本 则清除部分数据
    public void CheckIfNewVersion()
    {
        try
        {
            int savedVersion=BaseActivity.AppSetting.ListenSetting.getInt("AppVersion", 0);
            //清除部分数据
            if(savedVersion!=BaseActivity.VERSIONCODE)
            {
                BaseActivity.AppSetting.ListenSettingEditor.remove("DELAY_NEWSEATS");
                BaseActivity.AppSetting.ListenSettingEditor.remove("DELAY_BEFORESTARTTIME");
                BaseActivity.AppSetting.ListenSettingEditor.remove("DELAY_BEFOREENDTIME");
                BaseActivity.AppSetting.ListenSettingEditor.remove("DELAY_SENDBOOK");
                BaseActivity.AppSetting.ListenSettingEditor.apply();
            }
        } catch (Exception e)
        {
            Log.e("SettingFragment",e.getMessage());
        }
    }

    /****************
     *
     * 发起添加群流程。群号：WhuSeats(789729207) 的 key 为： mo5gO56OIB_tQ1idGlZDAItUqbPPHHDL
     * 调用 joinQQGroup(mo5gO56OIB_tQ1idGlZDAItUqbPPHHDL) 即可发起手Q客户端申请加群 WhuSeats(789729207)
     *
     * key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public boolean joinQQGroup() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + "mo5gO56OIB_tQ1idGlZDAItUqbPPHHDL"));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

}
