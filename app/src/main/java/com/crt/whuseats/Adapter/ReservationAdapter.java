package com.crt.whuseats.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crt.whuseats.JsonHelps.JsonInfo_Reservations;
import com.crt.whuseats.R;

import java.util.List;

//历史记录列表Adapter
//这是第一次学习用RecyclerView 不要慌！
//哇！！！这个Adapter写了好久啊woc
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder>
{

    List<JsonInfo_Reservations> HistroyList;

    //构造函数
    public ReservationAdapter(List<JsonInfo_Reservations> list)
    {
        super();
        HistroyList=list;
    }

    //用于外部响应两种控件点击的接口
    public interface onItemClick
    {
        //这个position用于向外界传出点击的是第几个子项
        void onclick(int Reid);
    }
    private onItemClick onInfoAreaClick;    //信息面板点击
    private onItemClick onButtonAreaClick;  //按钮点击

    //傻逼java没有属性还行
    public void setonInfoAreaClick(onItemClick or)
    {
        this.onInfoAreaClick=or;
    }
    public void setOnButtonAreaClick(onItemClick or)
    {
        this.onButtonAreaClick=or;
    }

    //viewholder 其实就是辣个每一个子项的承载view吧
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView dateview;
        TextView locview;
        Button ButtonCancel;
        View InfoArea;

        public ViewHolder(View view)
        {
            super(view);
            dateview=(TextView)view.findViewById(R.id.tv_his_date);
            locview=(TextView)view.findViewById(R.id.tv_his_loc);
            ButtonCancel=(Button) view.findViewById(R.id.btn_his_cancel);
            InfoArea=view.findViewById(R.id.his_infoArea);
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        JsonInfo_Reservations reinfo=HistroyList.get(position);
        holder.dateview.setText(reinfo.onDate);
        holder.locview.setText(reinfo.location);
        //根据不同状况给按钮赋不同的值
        switch (reinfo.datastatus)
        {
            case("RESERVE"):
                holder.ButtonCancel.setText("取消预约");
                holder.ButtonCancel.setEnabled(true);
                break;
            case ("CHECK_IN"):
                holder.ButtonCancel.setText("正用着呢");
                holder.ButtonCancel.setEnabled(false);
                break;
            case ("STOP"):
                holder.ButtonCancel.setText("溜了溜了");
                holder.ButtonCancel.setEnabled(false);
                break;
            case ("CANCEL"):
                holder.ButtonCancel.setText("已取消");
                holder.ButtonCancel.setEnabled(false);
                break;
            case ("COMPLETE"):
                holder.ButtonCancel.setText("已完成");
                holder.ButtonCancel.setEnabled(false);
                break;
            case("MISS"):
                holder.ButtonCancel.setText("忘去了吧");
                holder.ButtonCancel.setEnabled(false);
                break;
            case("INCOMPLETE"):
                holder.ButtonCancel.setText("中断");
                holder.ButtonCancel.setEnabled(false);
                break;
            default:
                holder.ButtonCancel.setText("其他状况");
                holder.ButtonCancel.setEnabled(false);
                break;

        }

        //判断是否设置了按钮监听 若设置了则给对应控件赋上
        if(onInfoAreaClick!=null)
        {
            holder.InfoArea.setOnClickListener((v)->{
                onInfoAreaClick.onclick(reinfo.id);
            });
        }
        if(onButtonAreaClick!=null)
        {
            holder.ButtonCancel.setOnClickListener((v)->{
                onButtonAreaClick.onclick(reinfo.id);
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewTye)
    {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.preordain_info_item, parent,false);
        ViewHolder temp=new ViewHolder(view);
        return temp;
    }

    @Override
    public int getItemCount()
    {
        return HistroyList.size();
    }
}
