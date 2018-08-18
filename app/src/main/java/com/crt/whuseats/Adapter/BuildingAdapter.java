package com.crt.whuseats.Adapter;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.JsonHelps.JsonInfo_Fliters;

import java.util.List;

//用于提供建筑下拉菜单的Adapter
public class BuildingAdapter extends ArrayAdapter<JsonInfo_Fliters.buildings>
{
    List<JsonInfo_Fliters.buildings> buildingsList;
    public BuildingAdapter(List<JsonInfo_Fliters.buildings> objects)
    {
        //这个布局是安卓自带的一个布局 里面只有一个textview;
        super(BaseActivity.ApplicationContext,android.R.layout.simple_list_item_1,objects);
        buildingsList=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        JsonInfo_Fliters.buildings building=getItem(position);//先获取当前的room信息
        View view= LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent,false);
        TextView t=(TextView)view.findViewById(android.R.id.text1); //获取到这个自带布局的textview
        t.setTextSize(12);
        t.setTextColor(Color.BLACK);
        t.setText(building.name);
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        JsonInfo_Fliters.buildings building=getItem(position);//先获取当前的room信息
        View view;
        if(convertView!=null)
            view=convertView;
        else
            view= LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent,false);
        TextView t=(TextView)view.findViewById(android.R.id.text1); //获取到这个自带布局的textview
        t.setText(building.name);
        return view;
    }

    /**
     * 返回建筑名称
     */
    public String GetBuildingname(int id)
    {
        for(JsonInfo_Fliters.buildings bu:buildingsList)
        {
            if(bu.id==id)
                return bu.name;
        }
        return "";
    }
}
