package com.crt.whuseats.Utils;

import android.widget.Toast;

import com.crt.whuseats.ApplicationV;

public class ToastUtils
{
    /**
     * 快速显示短Toast
     */
    public static void ShowShortToast(String msg)
    {
        Toast.makeText(ApplicationV.ApplicationContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 快速显示长Toast
     * @param msg
     */
    public static void ShowLongToast(String msg)
    {
        Toast.makeText(ApplicationV.ApplicationContext, msg, Toast.LENGTH_SHORT).show();
    }
}
