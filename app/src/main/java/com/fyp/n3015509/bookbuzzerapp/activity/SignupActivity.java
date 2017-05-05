package com.fyp.n3015509.bookbuzzerapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fyp.n3015509.APIs.LoginAPI;
import com.fyp.n3015509.bookbuzzerapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    private UserRegisterTask mRegisterTask = null;
    private LoginActivity.UserLoginTask mAuthTask = null;

    private EditText mName;
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mLoginLink;
    private Button mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mPasswordView = (EditText) findViewById(R.id.input_password);
        mEmailView = (EditText) findViewById(R.id.input_email);
        mName = (EditText) findViewById(R.id.input_name);
        mLoginLink = (TextView) findViewById(R.id.link_login);
        mSignUp = (Button) findViewById(R.id.btn_signup);

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);
            mRegisterTask = new UserRegisterTask(email, password, name);
            mRegisterTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mName;

        private JSONObject login = new JSONObject();
        private ProgressDialog progress;

        UserRegisterTask(String email, String password, String name) {
            mEmail = email;
            mPassword = password;
            mName = name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(SignupActivity.this);
            progress.setMessage("Registering...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                login.put("email", mEmail);
                login.put("password", mPassword);
                login.put("name", mName);

                String token = LoginAPI.RegisterNewUser(getApplicationContext(), login);
                if (token != null) {
                    return true;
                }
                //header
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            } catch (JSONException e) {
                return false;
            }

            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            progress.dismiss();

            if (success) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                setContentView(R.layout.activity_main);
            } else {
                mPasswordView.setError("There seems to have been an issues, please try again");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            progress.dismiss();
        }
    }

}
