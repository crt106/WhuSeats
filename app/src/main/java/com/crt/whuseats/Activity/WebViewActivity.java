package com.crt.whuseats.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crt.whuseats.R;

import javax.security.auth.login.LoginException;


//显示网页的Activity
public class WebViewActivity extends BaseActivity
{
    private static final String TAG = "WebViewActivity";
    //region 控件们
    WebView wb;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        //region 控件获取
        wb = (WebView) findViewById(R.id.webview);
        pb = (ProgressBar) findViewById(R.id.wb_progressBar);
        //endregion
        //获取Intent提供的Uri
        String uri = getIntent().getStringExtra("Uri");

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
                if (newProgress == 100)
                    pb.setVisibility(View.GONE);
                else
                {
                    pb.setVisibility(View.VISIBLE);
                    pb.setProgress(newProgress);
                }
            }
        });
        WebSettings wbsetting = wb.getSettings();
        wbsetting.setJavaScriptEnabled(true);
        wbsetting.setDomStorageEnabled(true);

        //这里在api21以下跳过
        if (Build.VERSION.SDK_INT > 21)
            wbsetting.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

        wbsetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        wb.clearCache(true);
//      wb.clearHistory();
        //提供一个JavaScript接口

        wb.addJavascriptInterface(new WebviewJavaScript(), "test");
        //endregion
        wb.loadUrl(uri);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    //javaScript接口类
    public class WebviewJavaScript
    {

        //通过JavaScript跳转包内的Activity
        @JavascriptInterface
        public void jumpActivity(String name)
        {
            try
            {
                Class<?> ac = Class.forName("com.crt.whuseats.Activity." + name);
                Intent i = new Intent(WebViewActivity.this, ac);
                startActivity(i);
            } catch (ClassNotFoundException e)
            {
                Log.e(TAG, "jump2Activity:" + e.getMessage());
            }
        }

        //加入QQ群
        @JavascriptInterface
        public void JoinQQGroup()
        {
            Intent intent = new Intent();
            intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + "mo5gO56OIB_tQ1idGlZDAItUqbPPHHDL"));
            startActivity(intent);
        }

        //呼出浏览器
        @JavascriptInterface
        public void CallBrowser(String uri)
        {
            try
            {
                Intent webIntent = new Intent("android.intent.action.VIEW");
                Uri euri = Uri.parse(uri);
                webIntent.setData(euri);
                startActivity(webIntent);
            } catch (Exception e)
            {
                Log.e(TAG, "CallBrowser:" + e.getMessage());
                Toast.makeText(WebViewActivity.this, "啊嘞 遇到什么错误了呢", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
