package com.fyp.n3015509.bookbuzzerapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.fyp.n3015509.bookbuzzerapp.R;

public class GoodreadsLoginActivity extends Activity {
    private WebView web;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodreads_login);

        Intent intent = getIntent();
        String url = intent.getExtras().getString("url");

       // String url = getAuthURL();

        Button mCloseGoodreadsLogin = (Button) findViewById(R.id.close_goodreads_login);
        mCloseGoodreadsLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(backToLogin);
            }
        });

        web = (WebView) findViewById(R.id.webview01);
        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(url);
    }


    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
//
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            view.loadUrl(url);
            String shortUrl;
            String auth_url = "https://www.goodreads.com/?oauth_token=";
            if(url.length()>39)
            {
                shortUrl = url.substring(0,39);
                if(shortUrl.equals(auth_url)){
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            }

            //view.loadUrl(url);

            return true;
        }
    }

    // To handle &quot;Back&quot; key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
