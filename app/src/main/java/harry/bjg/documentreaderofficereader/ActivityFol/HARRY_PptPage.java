package harry.bjg.documentreaderofficereader.ActivityFol;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import harry.bjg.documentreaderofficereader.R;

public class HARRY_PptPage extends AppCompatActivity {

    Context cn;
    String path;

    WebView webview;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(1024);
        setContentView(R.layout.harry_ppt_page);

        cn = this;
        path = getIntent().getStringExtra("path");
        Log.e("AAA", "Path : " + path);

        webview = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progressBar);

        preView("http://docs.google.com/viewer?url=" + path + "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void preView(String fileUrl) {
        webview.loadUrl(fileUrl);
        webview.setWebChromeClient(webChromeClient);
        webview.setWebViewClient(webViewClient);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true); // Allow js
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // Do not use cache, only get data from the network.
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        // Do not display webview zoom button
        webSettings.setDisplayZoomControls(false);
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            // Page loading is complete
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //The page starts to load
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    };
    // WebChromeClient mainly assists WebView to process Javascript dialogs, website icons, website titles, loading progress, etc.
    private WebChromeClient webChromeClient = new WebChromeClient() {
        // The alert pop-up window of js is not supported, you need to monitor it yourself and then use the dialog pop-up window
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("Yes", null);
            localBuilder.setCancelable(false);
            localBuilder.create().show(); // Note: //This code is required:
            // result.confirm() means: //The processing result is determined and the WebCore thread is awakened at the same time.//Otherwise, you cannot continue to click the button
            result.confirm();
            return true;
        }

        // Get the page title
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i(" Ansen ", " page headline: " + title);
        } // Load progress callback

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Rewrite the return key
        if (webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            // /When you click the back button, judge whether there is a previous page
            webview . goBack(); // goBack() means to return to the previous page of webView
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
