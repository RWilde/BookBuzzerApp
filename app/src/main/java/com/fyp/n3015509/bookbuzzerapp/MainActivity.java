package com.fyp.n3015509.bookbuzzerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.fyp.n3015509.Util.LoginUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button mLogoutButton = (Button) findViewById(R.id.logout_button);
        String token = SaveSharedPreference.getToken(getApplicationContext());
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUtil.signout(getApplicationContext());
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                setContentView(R.layout.activity_login);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Boolean imported = SaveSharedPreference.getImported(getApplicationContext());
        if (imported = false)
        {
            ShowAlertDialog();
        }
    }

    private void ShowAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ShelfImportFrag alertDialog = ShelfImportFrag.newInstance();
        alertDialog.show(fm, "fragment_alert");
    }


}
