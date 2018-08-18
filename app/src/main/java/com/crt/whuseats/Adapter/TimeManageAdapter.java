package com.crt.whuseats.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crt.whuseats.R;

import java.util.LinkedList;
import java.util.List;

public class TimeManageAdapter extends RecyclerView.Adapter<TimeManageAdapter.ViewHolder>
{



    /**这里本Adapter的格式要求为
     8:00_false
     9:00_free
     11:30_user
     三种类型
     */
    List<String> TimeList=new LinkedList<>();//时间链表

    //本Adapter对外的事件处理接口
    public interface OnItemClick
    {
        void OnClick(String timeStr,View v);
    }
    private OnItemClick TimeChooseClick;



    public TimeManageAdapter(List<String> t,OnItemClick oc)
    {
        super();
        TimeList=t;
        TimeChooseClick=oc;
    }



    //创建ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTimemaTime;
        ImageButton btnTimemaSet;

        public ViewHolder(View view)
        {
            super(view);
            tvTimemaTime = (TextView) view.findViewById(R.id.tv_timema_time);
            btnTimemaSet = (ImageButton) view.findViewById(R.id.btn_timema_set);
        }

    }

    //RecyclerView三巨头

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        String thisstr=TimeList.get(position);
        String TimeStr="",StatusStr="";
        try
        {
            TimeStr=thisstr.split("_")[0];
            StatusStr=thisstr.split("_")[1];
        }
        catch (Exception e)
        {
            Log.e("TimeMAdapter", "获取时间或者状态错误");
            return;
        }
        //设置时间文字
        holder.tvTimemaTime.setText(TimeStr);
        //设置底色
        switch (StatusStr)
        {
            //当前用户预约
            case("user"):
                holder.tvTimemaTime.setBackgroundResource(R.drawable.more_item_selector_blue);
                holder.btnTimemaSet.setEnabled(true);
                break;
            //空闲
            case("free"):
                holder.tvTimemaTime.setBackgroundResource(R.drawable.more_item_selector_green);
                holder.btnTimemaSet.setEnabled(true);
                break;
            //说明当前时间为不可用时间
            default:
                holder.tvTimemaTime.setBackgroundResource(R.drawable.more_item_selector_red);
                holder.btnTimemaSet.setEnabled(false);
                break;
        }
        //final中转量
        final String TimeStrfinal=TimeStr;
        if(TimeChooseClick!=null)
        {
            holder.btnTimemaSet.setOnClickListener((v -> { TimeChooseClick.OnClick(TimeStrfinal,holder.btnTimemaSet);}));
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_timestatus, parent,false);
        ViewHolder temp=new ViewHolder(view);
        return temp;
    }

    @Override
    public int getItemCount()
    {
        return TimeList.size();
    }
}
