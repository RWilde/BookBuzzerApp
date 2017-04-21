package com.fyp.n3015509.bookbuzzerapp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.APIs.LoginAPI;
import com.fyp.n3015509.bookbuzzerapp.R;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsLogin;
import com.fyp.n3015509.db.DBUtil;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private UserGoodreadsLoginTask mGoodreadsAuthTask = null;
    private UserRegisterTask mRegisterTask = null;


    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mSignupLink;
    private View mLoginFormView;
    private CallbackManager mFacebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        if (SaveSharedPreference.getToken(this).length() != 0) {
            // Stay at the current activity.
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }

        mFacebookCallbackManager = CallbackManager.Factory.create();

        AppEventsLogger.activateApp(this);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mSignupLink = (TextView) findViewById(R.id.link_signup);

        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.email_signin_btn || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_signin_btn);
        // Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        Button mEmailGoodreadsButton = (Button) findViewById(R.id.goodreads_sign_in_button);
        final TextView info = (TextView) findViewById(R.id.info);

        final LoginButton mFacebookSignInButton = (LoginButton) findViewById(R.id.facebook_sign_in_button);
        mFacebookSignInButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_friends"));
        mFacebookSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFacebookSignInButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String email = object.getString("email");
                                    String password = object.getString("birthday"); // 01/31/1980 format

                                    mRegisterTask = new UserRegisterTask(email, object.toString());
                                    mRegisterTask.execute((Void) null);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        info.setText("Login attempt canceled.");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        info.setText("Login attempt failed.");
                    }

                });
            }
        });


        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mEmailGoodreadsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignInGoodreads();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void attemptSignInGoodreads() {
        if (mGoodreadsAuthTask != null) {
            return;
        }


        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        mGoodreadsAuthTask = new UserGoodreadsLoginTask();
        mGoodreadsAuthTask.execute((Void) null);

//        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

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
//        else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);

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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        //addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

//    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
//        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(LoginActivity.this,
//                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
//
//        mEmailView.setAdapter(adapter);
//    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private JSONObject login = new JSONObject();
        private ProgressDialog progress;

        UserRegisterTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(LoginActivity.this);
            progress.setMessage("Registering...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                login.put("name", mEmail);
                login.put("password", mPassword);

                String token = com.fyp.n3015509.APIs.LoginAPI.RegisterNewUser(getApplicationContext(), login);
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
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private JSONObject login = new JSONObject();
        private ProgressDialog progress;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(LoginActivity.this);
            progress.setMessage("Authenticating...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                login.put("name", mEmail);
                login.put("password", mPassword);

                String token = com.fyp.n3015509.APIs.LoginAPI.SignIn(getApplicationContext(), login);
                if (token != null) {
                    return true;
                }

                Thread.sleep(2000);
            } catch (JSONException e) {
                return false;
            } catch (InterruptedException e) {
                return false;
            }

            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            progress.dismiss();

            if (success) {
                //finish();
                com.fyp.n3015509.APIs.LoginAPI.verifyStoragePermissions(LoginActivity.this);

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                setContentView(R.layout.activity_main);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    private class UserGoodreadsLoginTask extends AsyncTask<Void, Void, Boolean> {
        private JSONObject login = new JSONObject();
        private ProgressDialog progress;
        private GoodreadsLogin gLogin = new GoodreadsLogin();

        UserGoodreadsLoginTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(LoginActivity.this);
            progress.setMessage("Authenticating...");
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //http://www.techrepublic.com/blog/software-engineer/calling-restful-services-from-your-android-app/

            try {
                JSONObject login = new JSONObject();
                int goodreads_id = gLogin.GetGoodreadsAuthentication(LoginActivity.this);
                if (goodreads_id != 0) {
                    login.put("goodreads_id", goodreads_id);
                    SaveSharedPreference.setGoodreadsId(getApplicationContext(), Integer.toString(goodreads_id));
                    try {
                        JSONObject lists = LoginAPI.RegisterGoodreadsUser(getApplicationContext(), login);
                        progress.dismiss();
                        DBUtil.InsertFromAPI(lists);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            progress.dismiss();

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //  showProgress(false);
        }
    }
}

