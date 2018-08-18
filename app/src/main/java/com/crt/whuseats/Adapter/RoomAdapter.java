package com.crt.whuseats.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.crt.whuseats.JsonHelps.JsonInfo_HouseStats;
import com.crt.whuseats.R;

import java.util.List;

public class RoomAdapter extends ArrayAdapter<JsonInfo_HouseStats.room>
{

    List<JsonInfo_HouseStats.room> roomList;

    public RoomAdapter(@NonNull Context context, @NonNull List<JsonInfo_HouseStats.room> objects)
    {
        super(context, R.layout.layout_rooms, objects);
        roomList=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        JsonInfo_HouseStats.room thisroom=getItem(position);
        //这里是创建view的过程 如果已经创建了就读取现有的 不然就创建咯
        View view;
        if(convertView!=null)
            view=convertView;
        else
            view= LayoutInflater.from(getContext()).inflate(R.layout.layout_rooms, parent,false);
        //获取控件
        TextView roomTitle=(TextView) view.findViewById(R.id.btn_room_name);
        TextView roomFree=(TextView) view.findViewById(R.id.btn_room_count);
        //设置文字
        roomTitle.setText(String.format("%dF:%s",thisroom.whichfloor,thisroom.roomname));
        roomFree.setText(String.format("可能空闲的座位:%d",thisroom.free));

        return view;
    }

    /**
     * 获取房间名称
     * @param roomID 房间的ID
     * @return
     */
    public String GetRoomname(int roomID)
    {
        for(JsonInfo_HouseStats.room ro:roomList)
        {
            if(ro.roomId==roomID)
                return ro.roomname;
        }
        return "";
    }
}
