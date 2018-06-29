package com.crt.whuseats.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.crt.whuseats.R;


//显示网页的Activity
public class WebViewActivity extends BaseActivity
{

    //region 控件们
    WebView wb;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        //region 控件获取
        wb=(WebView) findViewById(R.id.webview);
        pb=(ProgressBar)findViewById(R.id.wb_progressBar);
        //endregion
        //获取Intent提供的Uri
        String uri=getIntent().getStringExtra("Uri");
        //region 初始化Webview
        wb.setWebViewClient(new WebViewClient()
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
        wb.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                if(newProgress==100)
                    pb.setVisibility(View.GONE);
                else
                {
                    pb.setVisibility(View.VISIBLE);
                    pb.setProgress(newProgress);
                }
            }
        });
        WebSettings wbsetting=wb.getSettings();
        wbsetting.setJavaScriptEnabled(true);
        wbsetting.setDomStorageEnabled(true);
        //这里在api21以下跳过
        if(Build.VERSION.SDK_INT>21)
        wbsetting.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

        wbsetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        wb.clearCache(true);
        wb.clearHistory();
        //提供一个JavaScript接口

        wb.addJavascriptInterface(new WebviewJavaScript(), "test");
        //endregion
        wb.loadUrl(uri);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //wb.loadUrl("www.baidu.com");
    }

    //javaScript接口类
    public class WebviewJavaScript
    {
        @JavascriptInterface
        public void jumptoAutoSetting()
        {
            Intent i=new Intent(WebViewActivity.this,AutoCreatSettingActivity.class);
            startActivity(i);
        }
        @JavascriptInterface
        public void JoinQQGroup()
        {
            Intent intent = new Intent();
            intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + "mo5gO56OIB_tQ1idGlZDAItUqbPPHHDL"));
            startActivity(intent);
        }
    }

}
