package com.crt.whuseats.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.crt.whuseats.Activity.SeatsActivity;
import com.crt.whuseats.JsonModels.seat;
import com.crt.whuseats.R;

import java.util.List;


public class SeatsAdapter extends ArrayAdapter<seat>
{
    public SeatsAdapter(@NonNull Context context, @NonNull List<seat> objects)
    {
        super(context, R.layout.seat_item, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        seat thisseat=getItem(position);
        //这里是创建view的过程 如果已经创建了就读取现有的 不然就创建咯
        View view;
        if(convertView!=null)
            view=convertView;
        else
        {
            //进行座位界面渲染
            view= LayoutInflater.from(getContext()).inflate(R.layout.seat_item, parent,false);
            //判断当前座位是否在第二天预约名单中
            try
            {
                //如果是
//                if(SeatsActivity.tomoinfolist.contains(thisseat.seatid))
                {
                    view.findViewById(R.id.ll_seat_bg).setBackgroundResource(R.drawable.seat_02);
                }
            } catch (Exception e)
            {
                Log.e("SeatAdapter",String.format("渲染座位%d出错"+e.getMessage(),thisseat.seatid));
            }
        }

        //获取控件
        TextView seatname=(TextView) view.findViewById(R.id.seat_name);

        //设置文字
        seatname.setTextColor(Color.BLACK);
        seatname.setText(thisseat.name);
        return view;
    }
}
