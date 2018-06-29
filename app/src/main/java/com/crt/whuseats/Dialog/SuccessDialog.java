package com.crt.whuseats.Dialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.crt.whuseats.JsonHelps.JsonInfo_BookReturn;
import com.crt.whuseats.R;

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
    JsonInfo_BookReturn bookReturn;
    //构造函数
    public SuccessDialog(@NonNull Context context,JsonInfo_BookReturn returnInfo)
    {
        super(context, R.style.Theme_AppCompat_Dialog);
        bookReturn=returnInfo;
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
