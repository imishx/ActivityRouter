package com.github.imishx.activityrouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.imishx.activityrouter.annotation.ActivityInterceptor;
import com.github.imishx.activityrouter.annotation.ActivityMapping;
import com.github.imishx.activityrouter.router.Router;

/**
 * @author 李博
 * @date 2016年12月13日
 * @desc
 */
@ActivityMapping(value = {"http", "https"}, stringParams = {"title"})
@ActivityInterceptor({WebViewInterceptor.class})
public class WebInfo extends AppCompatActivity {

    ProgressBar mBar;
    WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mBar = (ProgressBar) findViewById(R.id.pb_view);
        mWebView = (WebView) findViewById(R.id.web_view);
        configWebView();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mWebView.loadUrl(getIntent().getStringExtra(Router.URL));
    }

    private void configWebView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    mBar.setVisibility(View.INVISIBLE);
                } else {
                    mBar.setProgress(newProgress);
                }

            }
        });
    }
}
