package com.crt.whuseats.Activity;


import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
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
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

/**
 * 主界面 承载数个Fragment
 * Update at v1.0
 */
public class HomeActivity extends BaseActivity
{

    //region 相应的控件
    private SmartTabLayout achomeSmartTL;
    private ViewPager vpAchome;
    private NavigationView NvAchome;


    //这些个东西应该是起到碎片管理的作用来着？
    FragmentPagerItemAdapter adapter;
    //endregion

    //region 相关生命周期和事件响应
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //绑定控件
        achomeSmartTL = (SmartTabLayout) findViewById(R.id.achome_smartTL);
        vpAchome = (ViewPager) findViewById(R.id.vp_achome);
        NvAchome = (NavigationView) findViewById(R.id.Nv_achome);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("首页", HomeFragment.class)
                .add("预约座位", BookFragment.class)
                .add("监听座位",ListenFragment.class)
                .add("更多",SettingFragment.class)
                .create());

        vpAchome.setAdapter(adapter);
        achomeSmartTL.setViewPager(vpAchome);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }


    public void onPageSelected(int position) {

        Fragment page = adapter.getPage(position);


    }










}
