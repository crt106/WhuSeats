package com.crt.whuseats.Activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crt.whuseats.R;

//管理员admin进入的Activity
public class AdminActivity extends BaseActivity
{

    //控件们
    private EditText etZhicode;
    private Button btnZhicodeGet;
    private Button buttonZhicodePut;
    private TextView tvZhicodeStatus;

    private TextView tvProcessactive;
    private TextView tvProcessalive;
    private Button btnKillprocess;
    private Button btnRestartprocess;
    private Button btnGetlog;

    private TextView tvProcessMessage;
    private TextView tvProcessLog;



    String Zhicode;              //吱口令内容
    String ZhicodeStatus;        //吱口令状态

    boolean IsProcessAlive;      //服务器进程是否存活
    boolean IsProcessActive;     //服务器进程是否活跃

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        //绑定控件们
        etZhicode = (EditText) findViewById(R.id.et_zhicode);
        btnZhicodeGet = (Button) findViewById(R.id.btn_zhicode_get);
        buttonZhicodePut = (Button) findViewById(R.id.button_zhicode_put);
        tvZhicodeStatus = (TextView) findViewById(R.id.tv_zhicode_status);
        tvProcessactive = (TextView) findViewById(R.id.tv_processactive);
        tvProcessalive = (TextView) findViewById(R.id.tv_processalive);
        btnKillprocess = (Button) findViewById(R.id.btn_killprocess);
        btnRestartprocess = (Button) findViewById(R.id.btn_restartprocess);
        btnGetlog = (Button) findViewById(R.id.btn_getlog);
        tvProcessMessage = (TextView) findViewById(R.id.tv_process_message);
        tvProcessLog = (TextView) findViewById(R.id.tv_process_log);

        //绑定事件们
        btnZhicodeGet.setOnClickListener(v->{
            GetZhicodeFromServer();
        });

        buttonZhicodePut.setOnClickListener(v->{
            PutZhicodeToServer();
        });

        btnKillprocess.setOnClickListener(v->{
            Killprocess();
        });

        btnRestartprocess.setOnClickListener(v->{
            RestartProcess();
        });

        btnGetlog.setOnClickListener(v->{GetLog();});


    }



    @Override
    protected void onServiceBinded()
    {
        super.onServiceBinded();
        //刷新吱口令
        GetZhicodeFromServer();
        //检查winform
        checkprocessAlive();
        checkprocessActive();
    }

    //从服务端刷新吱口令到EditText
    public void GetZhicodeFromServer()
    {
        Thread t=new Thread(()->{
            Zhicode=mbinder.GetZhicode();
            Zhicode=Zhicode.replaceAll("\"","" );
        });
        try
        {
            t.start();
            t.join();
            etZhicode.setText(Zhicode, TextView.BufferType.EDITABLE);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    //修改吱口令 推送到服务器
    public void PutZhicodeToServer()
    {

        Zhicode=etZhicode.getText().toString();

        try
        {
            Thread t=new Thread(()->{
                ZhicodeStatus=mbinder.PutZhicode(Zhicode);
            });
            t.start();
            t.join();
            tvZhicodeStatus.setText(ZhicodeStatus);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    //检查服务器winform存活情况
    public void checkprocessAlive()
    {
        tvProcessalive.setText("检查进程存活状态...");
        Thread t=new Thread(()->{
            IsProcessAlive=mbinder.CheckProcessAlive();

            //这里在子线程中更新Ui了
            tvProcessalive.post(()->{
                tvProcessalive.setText(IsProcessAlive?"进程存活":"进程GG");
                tvProcessalive.setTextColor(IsProcessAlive? Color.GREEN:Color.RED);
            });
        });
        t.start();
    }

    //检查服务器winform活跃状况
    public void checkprocessActive()
    { 
        tvProcessactive.setText("检查进程活跃状态...");
        Thread t=new Thread(()->{
            IsProcessActive=mbinder.IsWinFormActive();

            //这里在子线程中更新Ui了
            tvProcessactive.post(()->{
                tvProcessactive.setText(IsProcessActive?"进程活跃":"进程连接失败");
                tvProcessactive.setTextColor(IsProcessActive? Color.GREEN:Color.RED);
            });
        });
        t.start();

    }

    //杀死winform进程
    public void Killprocess()
    {
        Thread t=new Thread(()->{
            mbinder.killProcess();
        });
        t.start();
        try
        {
            t.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        checkprocessActive();
        checkprocessAlive();
    }

    //重启进程
    public void RestartProcess()
    {
        Thread t=new Thread(()->{
            mbinder.RestartProcess();

        });
        t.start();
        try
        {
            t.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        checkprocessActive();
        checkprocessAlive();
    }

    //获取日志
    public void GetLog()
    {
        Thread t=new Thread(()->{
            String value=mbinder.GetLog();
            tvProcessLog.post(()->{
                tvProcessLog.setText(value);
            });
        });
        t.start();
    }
}
