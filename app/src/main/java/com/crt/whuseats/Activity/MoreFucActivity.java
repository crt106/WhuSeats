package com.crt.whuseats.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.crt.whuseats.R;

import org.jsoup.Connection;

public class MoreFucActivity extends BaseActivity
{

    //region 控件们
    private LinearLayout moreJavascript;
    private LinearLayout moreTrainingroom;
    private LinearLayout moreAdmin;






    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_fuc);
        //region 绑定可爱的控件和事件们
        moreJavascript = (LinearLayout) findViewById(R.id.more_javascript);
        moreTrainingroom = (LinearLayout) findViewById(R.id.more_trainingroom);
        moreAdmin = (LinearLayout) findViewById(R.id.more_admin);

        //打开Javascript版whuseats
        moreJavascript.setOnClickListener(v->{
            Intent webIntent=new Intent("android.intent.action.VIEW");
            Uri uri=Uri.parse("http://120.79.7.230/Whuseats_web/index.html");
            webIntent.setData(uri);
            startActivity(webIntent);
        });

        //打开研修室预约链接
        moreTrainingroom.setOnClickListener(v->{
            Intent webIntent=new Intent("android.intent.action.VIEW");
            Uri uri=Uri.parse("http://reserv.lib.whu.edu.cn/day.php");
            webIntent.setData(uri);
            startActivity(webIntent);
        });

        moreAdmin.setOnClickListener(v->{
            String user=AppSetting.UserAndPwd.getString("Username","" );
            if(user.equals("2016301610110"))
            {
                Intent intent=new Intent(this,AdminActivity.class);
                startActivity(intent);
            }
            else
            {
                AlertDialog tmp=new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("您没有权限进入此页面")
                        .create()
                        ;
                tmp.show();
            }

        });

        //打开管理员Activity

        //endregion
    }
}
