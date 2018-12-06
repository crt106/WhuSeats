package com.crt.whuseats.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.crt.whuseats.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Random;

/**
 * 利用开源库显示全屏小菊花对话框
 * Updated at v1.0 by crt 2018-12-5
 */
public class LoadingDialog extends AlertDialog
{

    private static final String TAG = "LoadingDialog";
    private AVLoadingIndicatorView aviview;
    private static LoadingDialog instance;
    private static String[] AVItypeArray = {"BallClipRotatePulseIndicator", "TriangleSkewSpinIndicator", "PacmanIndicator", "SemiCircleSpinIndicator"};

    private LoadingDialog(@NonNull Context context)
    {
        super(context, R.style.LoadingDialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loadingdialog);
        aviview = (AVLoadingIndicatorView) findViewById(R.id.avi);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }

    /**
     * 静态的展示对话框方法
     */
    public static void LoadingShow(Context context)
    {
        LoadingDialog tmp = new LoadingDialog(context);
        instance = tmp;
        tmp.show();
    }

    /**
     * 调用这个方法就会无条件随机式的切换loading风格 与random参数无关
     *
     * @param context
     * @param random
     */
    public static void LoadingShow(Context context, boolean random)
    {
        LoadingDialog tmp = new LoadingDialog(context);
        int index = new Random().nextInt(4);
        instance = tmp;
        tmp.show();
        tmp.aviview.setIndicator(AVItypeArray[index]);
    }

    /**
     * 隐藏该对话框
     */
    public static void LoadingHide()
    {
        try
        {
            instance.dismiss();
        } catch (Exception e)
        {
            Log.e(TAG, "LoadingHide: " + e.getMessage());
        }
    }
}
