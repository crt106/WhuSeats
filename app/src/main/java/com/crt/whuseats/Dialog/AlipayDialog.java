package com.crt.whuseats.Dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.R;
import com.crt.whuseats.Utils.TimeHelp;

public class AlipayDialog extends AlertDialog
{
    //控件们
    private TextView textView;
    private Button btnZhicodeOk;
    private Button btnZhicodeCancel;

    public static String Zhicodestr="";        //吱口令内容
    private Context Dialogcontext;

    public AlipayDialog(@NonNull Context context)
    {
        super(context,R.style.Theme_AppCompat_Dialog);
        Dialogcontext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        Window window=getWindow();
//        window.setGravity(Gravity.CENTER);
        setContentView(R.layout.layout_alipayzhicode);

        textView = (TextView) findViewById(R.id.textView);
        btnZhicodeOk = (Button) findViewById(R.id.btn_zhicode_ok);
        btnZhicodeCancel = (Button) findViewById(R.id.btn_zhicode_cancel);
        btnZhicodeCancel.setOnClickListener(v->{this.dismiss();});
        btnZhicodeOk.setOnClickListener(v->{

            //复制吱口令到剪贴板 并且启动支付宝

            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) Dialogcontext.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("红包吱口令", Zhicodestr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);

            try
            {
                Intent intent = new Intent();
                //通过包名启动
                PackageManager packageManager = Dialogcontext.getPackageManager();
                intent =packageManager.getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                Dialogcontext.startActivity(intent);

                //保存本地信息 指示今天已经点击过红包
                BaseActivity.AppSetting.UserAndPwdEditor.putBoolean("Alipay_"+ TimeHelp.GetTodayStr(), true).apply();
                this.dismiss();
            } catch (Exception e)
            {
                Toast.makeText(Dialogcontext, "什么?连支付宝都没有装?", Toast.LENGTH_SHORT).show();
                this.dismiss();
                e.printStackTrace();
            }
        });

    }

    @Override
    public void show()
    {
        super.show();
        if(Zhicodestr.equals(""))
            this.dismiss();
    }
}
