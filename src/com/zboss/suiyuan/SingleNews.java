package com.zboss.suiyuan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.graphics.Bitmap;

public class SingleNews extends Activity {

    private WebView webview;

    private static final String TAG = "Main";
    private ProgressDialog progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.singlenews);

        this.webview = (WebView) findViewById(R.id.singlenews);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        Bundle bundle = getIntent().getExtras();
        String newsPath = bundle.getString("newsPath");// 读出数据

        progressBar = new ProgressDialog(SingleNews.this);
        progressBar.setTitle("网页:" + newsPath);
        progressBar.setMessage("加载中...");
        progressBar.show();

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(SingleNews.this, "抱歉！ " + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle("错误");
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });
        webview.loadUrl(newsPath);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getRepeatCount() == 0) {
            dialog_Exit(SingleNews.this);
        }
        return false;
    }

    public void dialog_Exit(Context context) {
        if (progressBar.isShowing()) {
            progressBar.dismiss();
        } else {
            SingleNews.this.finish();
        }
    }

}