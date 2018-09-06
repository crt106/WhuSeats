package com.crt.whuseats.Activity;


import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.crt.whuseats.Dialog.AlipayDialog;
import com.crt.whuseats.Fragment.BookFragment;
import com.crt.whuseats.Fragment.HomeFragment;
import com.crt.whuseats.Fragment.ListenFragment;
import com.crt.whuseats.Fragment.ListenFragment_old;
import com.crt.whuseats.R;
import com.crt.whuseats.Fragment.SettingFragment;
import com.crt.whuseats.Utils.TimeHelp;


//主界面 承载多个碎片
public class MainActivity extends BaseActivity
{

    //region 相应的控件
    private RadioGroup downGruop;
    private RadioButton home;
    private RadioButton book;
    private RadioButton listen;
    private RadioButton settings;

    //这些个东西应该是起到碎片管理的作用来着？
    private FragmentTransaction transaction;
    public HomeFragment homeFragment;
    public ListenFragment_old listenFragment_old;
    public ListenFragment listenFragment;
    public SettingFragment settingFragment;
    public BookFragment bookFragment;
    //endregion

    //region 相关生命周期和事件响应
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //绑定控件
        downGruop=(RadioGroup)findViewById(R.id.rg_menu);
        home=(RadioButton)findViewById(R.id.rb_1);
        book=(RadioButton)findViewById(R.id.rb_2);
        listen=(RadioButton)findViewById(R.id.rb_3);
        settings=(RadioButton)findViewById(R.id.rb_4);
        downGruop.setOnCheckedChangeListener(downGroupChange);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //如果收到了展示吱口令的消息
        if(getIntent().getBooleanExtra("ShowAlipay", false))
        {
            boolean isshown=BaseActivity.AppSetting.UserAndPwd.getBoolean("Alipay_"+ TimeHelp.GetTodayStr(), false);
            //展示支付宝对话框
            if(!isshown)
            {
                AlipayDialog alipayDialog=new AlipayDialog(this);
                alipayDialog.show();
            }
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }

    @Override
    protected void onServiceBinded()
    {
        super.onServiceBinded();
        //现在先默认切换到监听界面
        home.setChecked(true);
    }

    //导航栏按钮切换响应事件
    private RadioGroup.OnCheckedChangeListener downGroupChange=(RadioGroup group, int checkedId)->
    {
        transaction=getSupportFragmentManager().beginTransaction();

        switch (checkedId)
        {

            case R.id.rb_1:
            {
                //如果是第一次切换 则实例化一个新对象
                if(homeFragment==null)
                    homeFragment=new HomeFragment();
                transaction.replace(R.id.body_layout, homeFragment);
                transaction.commit();
                //Toast.makeText(this,"这个界面还没有制作完成哦~要进行相关操作请去原来的app~" , Toast.LENGTH_LONG).show();
                break;

            }
            case R.id.rb_2:
            {
                if(bookFragment==null)
                    bookFragment=new BookFragment();
                transaction.replace(R.id.body_layout,bookFragment);
                transaction.commit();
//                Toast.makeText(this,"预约功能Beta测试中 粗问题不要打我~" , Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.rb_3:
            {
                if(listenFragment==null)
                    listenFragment=new ListenFragment();
                transaction.replace(R.id.body_layout, listenFragment);
                transaction.commit();
                break;
            }
            case R.id.rb_4:
            {
                if(settingFragment==null)
                    settingFragment=new SettingFragment();
                transaction.replace(R.id.body_layout, settingFragment);
                transaction.commit();
                break;
            }
        }
    };
    //endregion







}
