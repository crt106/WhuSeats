package com.crt.whuseats.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.R;
import com.crt.whuseats.Utils.TimeHelp;

import java.util.LinkedList;
import java.util.List;

public class ChooseTimeDialog extends AlertDialog
{
    //region 控件们
    public Spinner startTime;
    public Spinner endTime;

    public Button buttonOK;
    public Button buttonCancel;

    //可以被隐藏的控件
    public Switch isLoop;
    public RadioGroup  modechoose;
//    public RadioButton rb_tmode;
//    public RadioButton rb_fmode;

    public TextView ListenInfo;

    //endregion
    public ArrayAdapter<String> TimeAdapter;
    private String listeninfostr;

    //region 相关静态字段 供ListenFragment访问 不过貌似这不是一个长久之计
    public static String CHOOSESTARTTIME="480";
    public static String CHOOSEENDTIME="1350";
    public static boolean ISLOOP=true;      //是否循环
    @Deprecated
    public static boolean ISFTYPE;     //是否启用Web端的过滤模式 Ftype="过滤模式"
    //endregion

    public ChooseTimeDialog(Context context,String ListenInfo)
    {
        super(context, R.style.Theme_AppCompat_Dialog);
        listeninfostr=ListenInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window=getWindow();
        window.setGravity(Gravity.CENTER);
        setContentView(R.layout.layout_listentime_dialog);

        //region 获取控件
        startTime=(Spinner) findViewById(R.id.Sp_StartTime);
        endTime=(Spinner)findViewById(R.id.Sp_EndTime);
        isLoop =(Switch)findViewById(R.id.switch_IsLoop);
        buttonOK=(Button)findViewById(R.id.btn_setTimeOK);
        buttonCancel=(Button)findViewById(R.id.btn_setTimeCancel);
        modechoose=(RadioGroup)findViewById(R.id.rg_modechoose);
//        rb_tmode=(RadioButton) findViewById(R.id.rb_tmode);
//        rb_fmode=(RadioButton) findViewById(R.id.rb_fmode);
        ListenInfo =(TextView) findViewById(R.id.tv_listenInfo);

        //endregion


        //给信息指示TextView赋文字
        ListenInfo.setText(listeninfostr);
        //初始化时间选择Adapter
        InitTimeAdapter();


        startTime.setAdapter(TimeAdapter);
        endTime.setAdapter(TimeAdapter);

        //region 读取用户习惯
        startTime.setSelection(BaseActivity.AppSetting.ListenSetting.getInt("startTime",0 ),true);
        //解决由于主题导致的字体颜色问题
        ((TextView)startTime.getSelectedView().findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
        endTime.setSelection(BaseActivity.AppSetting.ListenSetting.getInt("endTime",0 ),true);

        ((TextView)endTime.getSelectedView().findViewById(android.R.id.text1)).setTextColor(Color.BLACK);

        //******注意 这里把相应的值读取到Spinner之后还要改静态变量~*****
        CHOOSESTARTTIME=Integer.toString(TimeHelp.Value2ID.get(startTime.getSelectedItem()));
        CHOOSEENDTIME=Integer.toString(TimeHelp.Value2ID.get(endTime.getSelectedItem()));

        //endregion

        //region 设置不需要外部处理的监听事件

        startTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            //设置选择开始时间字段
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //改变spinner字体颜色
                TextView tv=(TextView)view.findViewById(android.R.id.text1);;
                tv.setTextColor(Color.BLACK);
                //这里这个类型有反复改动啊 有点不好 不过hashmap就不去改了 也有点麻烦
                int timeInt= TimeHelp.Value2ID.get(TimeAdapter.getItem(position));
                CHOOSESTARTTIME=Integer.toString(timeInt);
                //保存用户习惯
                BaseActivity.AppSetting.ListenSettingEditor.putInt("startTime", position);
                BaseActivity.AppSetting.ListenSettingEditor.putString("startTimeValue", TimeAdapter.getItem(position));
                BaseActivity.AppSetting.ListenSettingEditor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        endTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            //设置选择结束时间字段
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //改变spinner字体颜色
                TextView tv=(TextView)view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.BLACK);
                int timeInt=TimeHelp.Value2ID.get(TimeAdapter.getItem(position));
                CHOOSEENDTIME=Integer.toString(timeInt);
                //保存用户习惯
                BaseActivity.AppSetting.ListenSettingEditor.putInt("endTime", position);
                BaseActivity.AppSetting.ListenSettingEditor.putString("endTimeValue", TimeAdapter.getItem(position));
                BaseActivity.AppSetting.ListenSettingEditor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        //开关控件的事件
        //默认开启循环
        ISLOOP=true;
        isLoop.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                {
                    ISLOOP = isChecked;
                });
        isLoop.setChecked(true);
//        //模式选择RadioGroup时间
//        modechoose.setOnCheckedChangeListener((RadioGroup group, int checkedId)->
//        {
//            //根据选择改变当前模式
//            if(checkedId==R.id.rb_fmode)
//                ISFTYPE=true;
//            else
//                ISFTYPE=false;
//        });
//        InitRadioGroup();
        //endregion

    }




    //设置确定按钮的监听
    public void setButtonOKOnClickListener(View.OnClickListener a)
    {
        buttonOK.setOnClickListener(a);
        //保存一遍某些设置
        BaseActivity.AppSetting.ListenSettingEditor.putString("startTimeValue", TimeHelp.ID2Value.get(Integer.parseInt(CHOOSESTARTTIME)));
        BaseActivity.AppSetting.ListenSettingEditor.putString("endTimeValue", TimeHelp.ID2Value.get(Integer.parseInt(CHOOSEENDTIME)));
        BaseActivity.AppSetting.ListenSettingEditor.apply();
    }

    //设置取消按钮的监听
    public void setButtonCancelOnClickListener(View.OnClickListener a)
    {
        buttonCancel.setOnClickListener(a);
    }

    //初始化时间适配器
    public void InitTimeAdapter()
    {
        List<String> tempList=new LinkedList<>();
        for(int i=TimeHelp.TIME_MIN;i<=TimeHelp.TIME_MAX;i+=TimeHelp.TIME_INTERVAL)
        {
            tempList.add(TimeHelp.ID2Value.get(i));
        }
        TimeAdapter=new ArrayAdapter<>(BaseActivity.ApplicationContext,android.R.layout.simple_list_item_1,tempList);

    }

    //初始化模式选择RadioGroup和某些组件
//    public void InitRadioGroup()
//    {
//        if(NetService.ISLOGINPCCOOKIESGET)
//        {
//            //以过滤模式运行
//            rb_fmode.setChecked(true);
//            rb_tmode.setChecked(false);
//            isLoop.setChecked(true);
//            ISLOOP=true;
//        }
//        else
//        {
//            //以遍历模式运行
//            rb_tmode.setChecked(true);
//            rb_fmode.setEnabled(false);
//            rb_fmode.setText("过滤模式(不可用,未强势登录)");
//            isLoop.setChecked(false);
//            ISLOOP=false;
//        }
//    }

    //在预约座位的相关界面呼出本对话框时的调整
    public void RunInBookMode(boolean ison)
    {
        if(this.isShowing())
        {
            if(ison)
            {
                isLoop.setVisibility(View.INVISIBLE);
                modechoose.setVisibility(View.INVISIBLE);
            }
            else
            {
                isLoop.setVisibility(View.VISIBLE);
                modechoose.setVisibility(View.VISIBLE);
            }
        }
    }


}
