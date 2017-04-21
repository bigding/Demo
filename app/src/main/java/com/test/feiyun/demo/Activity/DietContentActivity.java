package com.test.feiyun.demo.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.test.feiyun.demo.R;

public class DietContentActivity extends AppCompatActivity {

    private ImageButton back;
    private WebView webView;
    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_content);
        initView();
        initWebView();
    }

    private void initWebView() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String url = bundle.getString("diet_content_url");
        webView.loadUrl(url);
    }

    private void initView() {
        back = (ImageButton) findViewById(R.id.diet_back);
        webView = (WebView) findViewById(R.id.diet_web);
        progress = (ProgressBar) findViewById(R.id.progress);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
//        settings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
//        settings.setLoadWithOverviewMode(true);
        // 使页面支持缩放
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
//        //支持自动加载图片
//        settings.setLoadsImagesAutomatically(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);// 排版适应屏幕
//        // 缩放按钮
        settings.setDisplayZoomControls(false);


        // 网页加载进度显示
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progress.setVisibility(View.VISIBLE);
                progress.setProgress(newProgress);
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            // 页面加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(progress!=null){
                    progress.setVisibility(View.GONE);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DietContentActivity.this.finish();
            }
        });
    }
}
