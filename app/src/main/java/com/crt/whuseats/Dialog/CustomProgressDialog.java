package com.crt.whuseats.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crt.whuseats.R;

public class CustomProgressDialog extends Dialog
{

    //region 控件们
    TextView TitleView;
    ProgressBar progressBar;
    TextView MessageView;

    public CustomProgressDialog(Context context)
    {
        super(context, R.style.Theme_AppCompat_Dialog);
        setContentView(R.layout.layout_custompdialog);
        //获取控件
        TitleView=(TextView)findViewById(R.id.tv_pdialog_title);
        progressBar=(ProgressBar) findViewById(R.id.pb_listenroom);
        MessageView=(TextView)findViewById(R.id.tv_pdialogmsg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    /**
     * 报告进度
     * @param value 进度值
     */
    public void setProgress(int value)
    {
        progressBar.setProgress(value);
    }

    /**
     * 设置进度条的最大值
     * @param max
     */
    public void setMax(int max)
    {
        progressBar.setMax(max);
    }


    /**
     * 设置消息框
     * @param message
     */
    public void setMessage(CharSequence message)
    {
        MessageView.setText(message);
    }

    /**
     * 设置标题框
     * @param title
     */
    @Override
    public void setTitle(CharSequence title)
    {
        TitleView.setText(title);
    }
}
