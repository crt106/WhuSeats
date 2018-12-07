package com.crt.whuseats.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.crt.whuseats.Adapter.ReservationAdapter;
import com.crt.whuseats.Dialog.LoadingDialog;
import com.crt.whuseats.Dialog.SuccessDialog;
import com.crt.whuseats.Interface.onTaskResultReturn;
import com.crt.whuseats.JsonModels.JsonHelp;
import com.crt.whuseats.JsonModels.JsonModel_Base;
import com.crt.whuseats.JsonModels.JsonModel_BookReturn;
import com.crt.whuseats.JsonModels.JsonModel_HistoryList;
import com.crt.whuseats.R;

public class HistoryActivity extends BaseActivity {

//
//    //region 控件们
//    RecyclerView historyView;
//    //endregion
//    ReservationAdapter Re_adapter;
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my);
//        historyView=(RecyclerView)findViewById(R.id.Rv_history);
//    }
//
//    @Override
//    protected void onServiceBinded()
//    {
//        super.onServiceBinded();
//        GetHistory();
//    }
//
//    //检查历史列表
//    public void GetHistory()
//    {
//        LoadingDialog.LoadingShow(this,true);
//        mbinder.GetHistory(new onTaskResultReturn()
//        {
//            @Override
//            public void OnTaskSucceed(Object... data)
//            {
//                String datastr=(String)data[0];
//                JsonModel_HistoryList his=JsonHelp.GetHistoryList(datastr);
//
//                //初始化Adapter
//                Re_adapter=new ReservationAdapter(his.reservationsList);
//                //设置两类按键事件
//                Re_adapter.setOnButtonAreaClick(onCancelButtonClick);
//                Re_adapter.setonInfoAreaClick(onInfoViewButtonClick);
//                //设置布局管理器 这好像是RecyclerView必须的
//                LinearLayoutManager layoutManager=new LinearLayoutManager(HistoryActivity.this);
//                historyView.setLayoutManager(layoutManager);
//                //Adapter赋值
//                historyView.setAdapter(Re_adapter);
//                LoadingDialog.LoadingHide();
//            }
//
//            @Override
//            public void OnTaskFailed(Object... data)
//            {
//                LoadingDialog.LoadingHide();
//            }
//        });
//    }
//
//    //region RecyclerView的按钮响应事件
//
//    //取消预约按钮回调~
//    onTaskResultReturn CancelReturn=new onTaskResultReturn()
//    {
//        @Override
//        public void OnTaskSucceed(Object... data)
//        {
//            try
//            {
//                String datastr=(String)data[0];
//                JsonModel_Base info=new JsonModel_Base(datastr);
//                if(info.status.equals("fail"))
//                {
//                    Toast.makeText(HistoryActivity.this, info.message, Toast.LENGTH_SHORT).show();
//                }
//                else if(info.status.equals("success"))
//                {
//                    Toast.makeText(HistoryActivity.this, "取消预约成功", Toast.LENGTH_SHORT).show();
//                }
//                GetHistory();
//            }
//            catch (Exception e)
//            {
//                Log.e("HistoryActivity", e.getMessage());
//            }
//        }
//
//        @Override
//        public void OnTaskFailed(Object... data)
//        {
//
//        }
//    };
//
//    //查看详情按钮回调~
//    onTaskResultReturn InfoReturn=new onTaskResultReturn()
//    {
//        @Override
//        public void OnTaskSucceed(Object... data)
//        {
//            try
//            {
//                String datastr=(String)data[0];
//                //这里调用了辣个比较特殊的JsonHelp处理方法
//                JsonModel_BookReturn info=JsonHelp.GetHistoryView(datastr);
//                SuccessDialog temp=new SuccessDialog(HistoryActivity.this,info);
//                temp.show();
//                temp.SetTitle("历史预约");
//            }
//            catch (Exception e)
//            {
//                Log.e("HistoryActivity", e.getMessage());
//            }
//
//        }
//
//        @Override
//        public void OnTaskFailed(Object... data)
//        {
//
//        }
//    };
//
//    //取消预约按钮按下之后
//    ReservationAdapter.onItemClick onCancelButtonClick=(Reid -> {
//        AlertDialog conformDialog=new AlertDialog.Builder(HistoryActivity.this)
//                .setTitle("确认")
//                .setMessage("是否取消该预约?")
//                .setPositiveButton("是滴是滴",(DialogInterface dialog, int which)->{mbinder.Cancel(Integer.toString(Reid), CancelReturn);})
//                .setNegativeButton("手滑了",null )
//                .create();
//        conformDialog.show();
//    });
//    //查看详情按下之后
//    ReservationAdapter.onItemClick onInfoViewButtonClick=(Reid ->{
//
//        mbinder.GetHistoryOneView(Integer.toString(Reid), InfoReturn);
//    });


    //endregion
}
