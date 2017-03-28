package com.fyp.n3015509.bookbuzzerapp.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.bookbuzzerapp.R;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(SaveSharedPreference.getToken(SplashActivity.this).length() == 0)
                {
                    // call Login Activity
                    Intent loginIntent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(loginIntent);
                    SplashActivity.this.finish();
                }
                else
                {
                    // Stay at the current activity.
                    Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }


            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
