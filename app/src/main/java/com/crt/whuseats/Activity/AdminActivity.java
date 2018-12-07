package com.crt.whuseats.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crt.whuseats.R;

//管理员admin进入的Activity
public class AdminActivity extends BaseActivity
{

//    private static final String TAG = "AdminActivity";
//    //控件们
//    private EditText etZhicode;
//    private Button btnZhicodeGet;
//    private Button buttonZhicodePut;
//    private TextView tvZhicodeStatus;
//
//    private TextView tvProcessactive;
//    private TextView tvProcessalive;
//    private Button btnKillprocess;
//    private Button btnRestartprocess;
//    private Button btnGetlog;
//
//    private TextView tvProcessMessage;
//    private TextView tvProcessLog;
//
//    private EditText etMoreFucMsg;
//    private Button btnSetmorefuc;
//    private EditText etEasteregg;
//    private Button btnSetEasterEgg;
//
//
//    String Zhicode;              //吱口令内容
//    String ZhicodeStatus;        //吱口令状态
//
//    boolean IsProcessAlive;      //服务器进程是否存活
//    boolean IsProcessActive;     //服务器进程是否活跃
//
//    String MoreFucMsg;           //更多功能消息
//    String EasterEggCode;        //彩蛋消息
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin);
//        //绑定控件们
//        etZhicode = (EditText) findViewById(R.id.et_zhicode);
//        btnZhicodeGet = (Button) findViewById(R.id.btn_zhicode_get);
//        buttonZhicodePut = (Button) findViewById(R.id.button_zhicode_put);
//        tvZhicodeStatus = (TextView) findViewById(R.id.tv_zhicode_status);
//        tvProcessactive = (TextView) findViewById(R.id.tv_processactive);
//        tvProcessalive = (TextView) findViewById(R.id.tv_processalive);
//        btnKillprocess = (Button) findViewById(R.id.btn_killprocess);
//        btnRestartprocess = (Button) findViewById(R.id.btn_restartprocess);
//        btnGetlog = (Button) findViewById(R.id.btn_getlog);
//        tvProcessMessage = (TextView) findViewById(R.id.tv_process_message);
//        tvProcessLog = (TextView) findViewById(R.id.tv_process_log);
//        etMoreFucMsg = (EditText) findViewById(R.id.et_MoreFucMsg);
//        btnSetmorefuc = (Button) findViewById(R.id.btn_setmorefuc);
//        etEasteregg = (EditText) findViewById(R.id.et_easteregg);
//        btnSetEasterEgg = (Button) findViewById(R.id.btn_setEasterEgg);
//
//        //绑定事件们
//        btnZhicodeGet.setOnClickListener(v ->
//        {
//            GetZhicodeFromServer();
//        });
//
//        buttonZhicodePut.setOnClickListener(v ->
//        {
//            PutZhicodeToServer();
//        });
//
//        btnKillprocess.setOnClickListener(v ->
//        {
//            Killprocess();
//        });
//
//        btnRestartprocess.setOnClickListener(v ->
//        {
//            RestartProcess();
//        });
//
//        btnGetlog.setOnClickListener(v ->
//        {
//            GetLog();
//        });
//        btnSetmorefuc.setOnClickListener(v -> SetMoreFucMsg());
//        btnSetEasterEgg.setOnClickListener(v -> SetEasterEggCode());
//
//
//    }
//
//
//    @Override
//    protected void onServiceBinded()
//    {
//        super.onServiceBinded();
//        //刷新吱口令
//        GetZhicodeFromServer();
//        //检查winform
//        checkprocessAlive();
//        checkprocessActive();
//        GetMoreFucMsg();
//        GetEasterEggCode();
//        AuthCheck();
//    }
//
//    /**
//     * 身份验证 不是管理员不能进入哦~
//     */
//    public void AuthCheck()
//    {
//        String user = AppSetting.UserAndPwd.getString("Username", "");
//        String text = EasterEggCode;
//        if (!user.equals("2016301610110"))
//        {
//            setScreenBgDarken();
//            AlertDialog tmp = new AlertDialog.Builder(this)
//                    .setTitle(text)
//                    .setMessage("您没有权限进入此页面哦~但是你能进到这里很不错了哦~是看了源码还是运气好碰到了呢~\n" +
//                            "嘛反正不管咋样 可以找我来领红包哦~")
//                    .setCancelable(false)
//                    .setPositiveButton("口令在哪里呢", (dialog, which) ->
//                    {
//                        Jump2QQ();
//                        this.finish();
//                    })
//                    .create();
//            tmp.setCanceledOnTouchOutside(false);
//            tmp.show();
//        }
//    }
//
//
//    //region 管理方法
//
//    //从服务端刷新吱口令到EditText
//    public void GetZhicodeFromServer()
//    {
//        Thread t = new Thread(() ->
//        {
//            Zhicode = mbinder.GetZhicode();
//            Zhicode = Zhicode.replaceAll("\"", "");
//        });
//        try
//        {
//            t.start();
//            t.join();
//            etZhicode.setText(Zhicode, TextView.BufferType.EDITABLE);
//        } catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    //修改吱口令 推送到服务器
//    public void PutZhicodeToServer()
//    {
//
//        Zhicode = etZhicode.getText().toString();
//
//        try
//        {
//            Thread t = new Thread(() ->
//            {
//                ZhicodeStatus = mbinder.PutZhicode(Zhicode);
//            });
//            t.start();
//            t.join();
//            tvZhicodeStatus.setText(ZhicodeStatus);
//        } catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    //检查服务器winform存活情况
//    public void checkprocessAlive()
//    {
//        tvProcessalive.setText("检查进程存活状态...");
//        Thread t = new Thread(() ->
//        {
//            IsProcessAlive = mbinder.CheckProcessAlive();
//
//            //这里在子线程中更新Ui了
//            tvProcessalive.post(() ->
//            {
//                tvProcessalive.setText(IsProcessAlive ? "进程存活" : "进程GG");
//                tvProcessalive.setTextColor(IsProcessAlive ? Color.GREEN : Color.RED);
//            });
//        });
//        t.start();
//    }
//
//    //检查服务器winform活跃状况
//    public void checkprocessActive()
//    {
//        tvProcessactive.setText("检查进程活跃状态...");
//        Thread t = new Thread(() ->
//        {
//            IsProcessActive = mbinder.IsWinFormActive();
//
//            //这里在子线程中更新Ui了
//            tvProcessactive.post(() ->
//            {
//                tvProcessactive.setText(IsProcessActive ? "进程活跃" : "进程连接失败");
//                tvProcessactive.setTextColor(IsProcessActive ? Color.GREEN : Color.RED);
//            });
//        });
//        t.start();
//
//    }
//
//    //杀死winform进程
//    public void Killprocess()
//    {
//        Thread t = new Thread(() ->
//        {
//            mbinder.killProcess();
//        });
//        t.start();
//        try
//        {
//            t.join();
//        } catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }
//        checkprocessActive();
//        checkprocessAlive();
//    }
//
//    //重启进程
//    public void RestartProcess()
//    {
//        Thread t = new Thread(() ->
//        {
//            mbinder.RestartProcess();
//
//        });
//        t.start();
//        try
//        {
//            t.join();
//        } catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }
//        checkprocessActive();
//        checkprocessAlive();
//    }
//
//    //获取日志
//    public void GetLog()
//    {
//        Thread t = new Thread(() ->
//        {
//            String value = mbinder.GetLog();
//            tvProcessLog.post(() ->
//            {
//                tvProcessLog.setText(value);
//            });
//        });
//        t.start();
//    }
//
//    //刷新更多功能消息 显示到EditText
//    public void GetMoreFucMsg()
//    {
//        Thread t = new Thread(() ->
//        {
//            MoreFucMsg = mbinder.GetMoreFucMsg();
//            MoreFucMsg = MoreFucMsg.replace("\"", "");
//            etMoreFucMsg.post(() ->
//            {
//                etMoreFucMsg.setText(MoreFucMsg);
//            });
//        });
//        t.start();
//    }
//
//    //更新更多功能消息到服务器
//    public void SetMoreFucMsg()
//    {
//        String msg = etMoreFucMsg.getText().toString();
//        Thread t = new Thread(() ->
//        {
//            mbinder.SetMoreFucMsg(msg);
//        });
//        t.start();
//    }
//
//    //刷新彩蛋消息 显示到EditText
//    public void GetEasterEggCode()
//    {
//        Thread t = new Thread(() ->
//        {
//            EasterEggCode = mbinder.GetEasterEggCode();
//            EasterEggCode = EasterEggCode.replace("\"", "");
//            etEasteregg.post(() ->
//            {
//                etEasteregg.setText(EasterEggCode);
//            });
//        });
//        t.start();
//        try
//        {
//            t.join();
//        } catch (InterruptedException e)
//        {
//            Log.e(TAG, "GetEasterEggCode:" + e.getMessage());
//        }
//    }
//
//    //更新彩蛋消息到服务器
//    public void SetEasterEggCode()
//    {
//        String msg = etMoreFucMsg.getText().toString();
//        Thread t = new Thread(() ->
//        {
//            mbinder.SetEasterEggCode(msg);
//        });
//        t.start();
//    }
//
//    //endregion
//
//    /**
//     * 跳到QQ和本傻子对话
//     */
//    public void Jump2QQ()
//    {
//        try
//        {
//            String url = "mqqwpa://im/chat?chat_type=wpa&uin=814909233";
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//        } catch (Exception e)
//        {
//            Log.e(TAG, "Jump2QQ:" + e.getMessage());
//            Toast.makeText(this, "什么鬼玩意儿QQ都不装", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // 设置屏幕背景变暗
//    private void setScreenBgDarken()
//    {
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = 0.05f;
//        lp.dimAmount = 0.05f;
//        getWindow().setAttributes(lp);
//    }
//
//    // 设置屏幕背景变亮
//    private void setScreenBgLight()
//    {
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = 1.0f;
//        lp.dimAmount = 1.0f;
//        getWindow().setAttributes(lp);
//    }

}
