package com.crt.whuseats.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.crt.whuseats.JsonHelps.JsonInfo_HouseStats;
import com.crt.whuseats.JsonHelps.JsonInfo_RoomLayout;
import com.crt.whuseats.JsonHelps.seat;
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
            view= LayoutInflater.from(getContext()).inflate(R.layout.seat_item, parent,false);
        //获取控件
        TextView seatname=(TextView) view.findViewById(R.id.seat_name);

        //设置文字
        seatname.setTextColor(Color.BLACK);
        seatname.setText(thisseat.name);
        return view;
    }
}
