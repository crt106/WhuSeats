package com.crt.whuseats.Dialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.JsonModels.JsonModel_BookReturn;
import com.crt.whuseats.R;
import com.crt.whuseats.Utils.TimeHelp;

public class SuccessDialog extends Dialog
{

    //region 控件们
    TextView tv_successTitle;
    TextView tv_receipt;
    TextView tv_datetime;
    TextView tv_seat;
    Button buttonOK;
    //endregion
    //注意 这个Dialog只接受BookReturn类的info
    JsonModel_BookReturn bookReturn;

    private Context Dialogcontext;
    //构造函数
    public SuccessDialog(@NonNull Context context,JsonModel_BookReturn returnInfo)
    {
        super(context, R.style.Theme_AppCompat_Dialog);
        bookReturn=returnInfo;
        Dialogcontext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window=getWindow();
        window.setGravity(Gravity.CENTER);
        setContentView(R.layout.layout_success);
        tv_successTitle=(TextView)findViewById(R.id.tv_SuccessTitle);
        tv_receipt=(TextView)findViewById(R.id.tv_receipt);
        tv_datetime=(TextView)findViewById(R.id.tv_datetime);
        tv_seat=(TextView)findViewById(R.id.tv_seat);
        buttonOK=(Button)findViewById(R.id.btn_success_ok);
        buttonOK.setOnClickListener((v)->
        {

            //如果是预约成功状态 则判断要不要弹出吱口令窗口
            if(tv_successTitle.getText().equals(Dialogcontext.getString(R.string.dialog_title)))
            {
               boolean isshown=BaseActivity.AppSetting.UserAndPwd.getBoolean("Alipay_"+ TimeHelp.GetTodayStr(), false);
               //展示支付宝对话框
               if(!isshown)
               {
                   AlipayDialog alipayDialog=new AlipayDialog(Dialogcontext);
                   alipayDialog.show();
               }
            }
            this.dismiss();
        });
    }

    @Override
    public void show()
    {
        super.show();
        tv_receipt.setText(bookReturn.receipt);
        tv_datetime.setText(bookReturn.onDate+"   "+bookReturn.begin+"----"+bookReturn.end);
        tv_seat.setText(bookReturn.location);
    }

    //设置标题
    public void SetTitle(CharSequence c)
    {
        tv_successTitle.setText(c);
    }
}
