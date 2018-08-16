package com.crt.whuseats.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.R;

//PC端登陆的对话框
@Deprecated
public class LoginpcDialog extends AlertDialog
{

    private BaseActivity BindingActivity;//与这个对话框绑定的Activity

    //region 控件
    private EditText et_captcha;
    private ImageView iv_captcha;
    private Button loginpc;
    //endregion
    public LoginpcDialog(Context context,BaseActivity a)
    {
        super(context, R.style.Theme_AppCompat_Dialog);
        BindingActivity=a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window=getWindow();
        window.setGravity(Gravity.CENTER);

//        //解决对话框软键盘不弹出的问题
//        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        //加上下面这一行弹出对话框时软键盘随之弹出
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setContentView(R.layout.layout_loginpc_dialog);
        //region 获取控件和设置监听事件
        loginpc=(Button)findViewById(R.id.btn_loginpc);
        et_captcha=(EditText)findViewById(R.id.et_captcha);

        iv_captcha=(ImageView)findViewById(R.id.iv_captcha);
//        iv_captcha.setOnClickListener((view)->{
//            BindingActivity.mbinder_b.Getcaptcha(new onTaskResultReturn()
//        {
//            @Override
//            public void OnTaskSucceed(Object... data)
//            {
//                Bitmap b=(Bitmap)data[0];
//                iv_captcha.setImageBitmap(b);
//            }
//
//            @Override
//            public void OnTaskFailed(Object... data)
//            {
//
//            }
//        });

//        });


        //endregion
    }

    //在对话框onStart方法里面请求验证码
    @Override
    protected void onStart()
    {
        super.onStart();
        BindingActivity.mbinder.Getcaptcha(new onTaskResultReturn()
        {
            @Override
            public void OnTaskSucceed(Object... data)
            {
                Bitmap b=(Bitmap)data[0];
                iv_captcha.setImageBitmap(b);
            }

            @Override
            public void OnTaskFailed(Object... data)
            {

            }
        });
    }

    public void setButtonLoginPc_Click(View.OnClickListener o)
    {
        loginpc.setOnClickListener(o);
    }

    //获取用户输入的验证码
    public String getInputcaptcha()
    {
        return et_captcha.getText().toString();
    }
}
