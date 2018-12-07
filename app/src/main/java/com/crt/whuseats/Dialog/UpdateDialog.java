package com.crt.whuseats.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.crt.whuseats.Activity.BaseActivity;
import com.crt.whuseats.ApplicationV;
import com.crt.whuseats.R;

import org.jsoup.Connection;

public class UpdateDialog extends Dialog
{

    //region 控件们
    TextView Title;
    TextView nowVersion;
    WebView UpdateInfo;
    Button DoUpdate;
    Button IngoreUpdate;
    //endregion
    public boolean IsForceUpdate;// 区分这次是不是强制更新

    public UpdateDialog(@NonNull Context context,boolean isForceUpdate)
    {

        super(context, R.style.Theme_AppCompat_Dialog);
        setContentView(R.layout.layout_update);

        this.IsForceUpdate=isForceUpdate;
        Title=(TextView)findViewById(R.id.tv_update_title);
        nowVersion=(TextView)findViewById(R.id.tv_nowVersion);
        UpdateInfo=(WebView)findViewById(R.id.wb_update);
        DoUpdate=(Button)findViewById(R.id.btn_update_ok);
        IngoreUpdate=(Button)findViewById(R.id.btn_update_cancel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        //获取控件
        nowVersion.setText(String.format("当前版本:V%s(%d)",ApplicationV.VERSIONNAME, ApplicationV.VERSIONCODE));
        //region 初始化Webview
        UpdateInfo.setWebViewClient(new WebViewClient()
        {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String URI)
            {
                view.loadUrl(URI);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
            {
                handler.proceed();
            }
        });
        UpdateInfo.setWebChromeClient(new WebChromeClient());
        WebSettings wbsetting=UpdateInfo.getSettings();
        wbsetting.setJavaScriptEnabled(true);
        wbsetting.setDomStorageEnabled(true);
        //wbsetting.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        wbsetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        UpdateInfo.clearCache(true);
        UpdateInfo.clearHistory();
        //endregion
        UpdateInfo.loadUrl("http://120.79.7.230/appUpdateLog.html");
        //初始化CancelButton点击事件
        if(IsForceUpdate)
        {
            Title.setText("检测到强制更新版本！");
            setCanceledOnTouchOutside(false);
            setCancelable(false);
        }
        else
        {
            IngoreUpdate.setOnClickListener((V)->{this.dismiss();});
        }
    }

    public void setButtonOKListenner(View.OnClickListener r)
    {
        DoUpdate.setOnClickListener(r);
    }

}
