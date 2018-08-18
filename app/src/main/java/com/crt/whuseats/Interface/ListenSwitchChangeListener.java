package com.crt.whuseats.Interface;

import android.widget.CompoundButton;

import com.crt.whuseats.Model.ListenItem;

//给ListenItem使用的 当开关按钮按下时的接口
public interface ListenSwitchChangeListener
{
    void onCheckedChanged(CompoundButton buttonView, boolean isChecked, ListenItem listenItem);
}
