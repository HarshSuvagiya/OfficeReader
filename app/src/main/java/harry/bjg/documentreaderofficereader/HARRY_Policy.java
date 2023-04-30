package harry.bjg.documentreaderofficereader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HARRY_Policy extends Activity {

    WebView webView;
    ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harry_policy);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        webView = findViewById(R.id.web);

        p = new ProgressDialog(this);
        p.setMessage("Loading...");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                p.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                p.dismiss();
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl("file:///android_asset/harry_privacy_policy.html");
    }
}
