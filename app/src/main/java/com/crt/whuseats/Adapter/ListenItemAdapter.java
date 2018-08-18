package com.crt.whuseats.Adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.crt.whuseats.Interface.ListenSwitchChangeListener;
import com.crt.whuseats.Model.ListenItem;
import com.crt.whuseats.R;

import java.util.List;

/**
 * 监听界面 RecyclerView 的适配器
 */
public class ListenItemAdapter extends RecyclerView.Adapter<ListenItemAdapter.ViewHolder>
{

    List<ListenItem> ItemList;                       //Adapter数据源
    private OnDeleteButtonClick onDeleteButtonClick; //删除按钮点击事件
    private ListenSwitchChangeListener switchChangeListener; //切换开关响应事件

    public ListenItemAdapter(List<ListenItem> itemList,OnDeleteButtonClick oc,ListenSwitchChangeListener sw)
    {
        super();
        ItemList=itemList;
        onDeleteButtonClick=oc;
        switchChangeListener=sw;
    }

    //本Adapter对外的事件响应接口
    public interface OnDeleteButtonClick{
        //点击事件 id为编号
        void OnClick(int id);
    }


    static class ViewHolder extends RecyclerView.ViewHolder
    {
        Button btnListenitemDelete;
        LinearLayout listenInfoArea;
        TextView tvListenHousename;
        TextView tvListenLocation;
        TextView tvListenTime;
        Switch swIsEnable;

        public ViewHolder(View itemView)
        {
            super(itemView);
            btnListenitemDelete = (Button) itemView.findViewById(R.id.btn_listenitem_delete);
            listenInfoArea = (LinearLayout) itemView.findViewById(R.id.listen_infoArea);
            tvListenHousename = (TextView) itemView.findViewById(R.id.tv_listen_housename);
            tvListenLocation = (TextView) itemView.findViewById(R.id.tv_listen_location);
            tvListenTime = (TextView) itemView.findViewById(R.id.tv_listen_time);
            swIsEnable=(Switch)itemView.findViewById(R.id.switch_IsListenEnable);
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listenitem, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        ListenItem item=ItemList.get(position);
        //设置文字
        if(item.IsEnable)
        holder.tvListenHousename.setText(item.Housename);
        else holder.tvListenHousename.setText(item.Housename+"(未启用)");

        holder.tvListenLocation.setText(item.Location);
        holder.tvListenTime.setText(item.getTimestr());

        //设置事件
        holder.btnListenitemDelete.setOnClickListener(v -> onDeleteButtonClick.OnClick(position));
        holder.swIsEnable.setChecked(item.IsEnable);
        //设置按钮开关事件
        holder.swIsEnable.setOnCheckedChangeListener((compoundButton,ischecked)->{switchChangeListener.onCheckedChanged(compoundButton,ischecked ,item);});
    }

    @Override
    public int getItemCount()
    {
        return ItemList.size();
    }




}
