package com.crt.whuseats.Activity;

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


    String Zhicode;              //吱口令内容
    String ZhicodeStatus;        //吱口令状态

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


        btnZhicodeGet.setOnClickListener(v->{
            GetZhicodeFromServer();
        });

        buttonZhicodePut.setOnClickListener(v->{
            PutZhicodeToServer();
        });
    }

    @Override
    protected void onServiceBinded()
    {
        super.onServiceBinded();
        //刷新吱口令
        GetZhicodeFromServer();
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
}
