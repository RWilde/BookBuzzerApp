package com.fyp.n3015509.APIs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.bookbuzzerapp.activity.LoginActivity;
import com.fyp.n3015509.dao.Buzzlist;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsShelf;
import com.fyp.n3015509.db.DBUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tomha on 23-Mar-17.
 */

public class LoginAPI {
    private static final String IP = "192.168.0.5";
    private static final String BaseURL = "http://" + IP + ":8081/api";
    private static final String loginURL = BaseURL + "/users/authenticate";
    private static final String RegisterURL = BaseURL + "/users/signup";
    private static final String FacebookLoginURL = BaseURL + "/users/signupfacebook";
    private static final String GoodreadsLoginURL = BaseURL + "/users/signupgoodreads";


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void signout(Context cxt) {
        SaveSharedPreference.clearToken(cxt);
    }

    public JSONObject SignIn(Context cxt, JSONObject login) {
        JSONObject jsonObject = new JSONObject();
        try {
            URL authURL = new URL(loginURL);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            out.write(login.toString());
            out.close();

            int status = conn.getResponseCode();

            if (status == 200) {
                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = convertStreamToString(in);
                jsonObject = new JSONObject(result);

                in.close();
                conn.disconnect();

                String token = jsonObject.getString("token");
                SaveSharedPreference.setToken(cxt, token);
            }
            else
            {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static void verifyStoragePermissions(LoginActivity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public String RegisterNewUser(Context cxt, JSONObject login) {
        try {
            URL authURL = new URL(RegisterURL);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            out.write(login.toString());
            out.close();

            int status = conn.getResponseCode();

            if (status == 200) {
                JSONObject jsonObject = SignIn(cxt, login);
                if (jsonObject != null)
                {
                    return jsonObject.getString("token");
                }
                else return null;
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String RegisterFacebookUser(Context cxt, JSONObject login) {
        try {
            URL authURL = new URL(FacebookLoginURL);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            //conn.connect();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            out.write(login.toString());
            out.close();

            int status = conn.getResponseCode();

            Log.d("response", Integer.toString(status));

            String json_response = "";
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String text = "";
            JSONObject jsonObj = null;

            while ((text = br.readLine()) != null) {
                json_response += text;
            }

            if (status == 200) {
                String token = jsonObj.getString("token");
                SaveSharedPreference.setToken(cxt, token);
                return token;

            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject RegisterGoodreadsUser(Context cxt, JSONObject login) {
        JSONObject jsonObject = null;
        try {
            URL authURL = new URL(GoodreadsLoginURL);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(login.toString().getBytes("UTF-8"));
            os.flush();

            int status = conn.getResponseCode();

            if (status == 200) {
                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = convertStreamToString(in);
                jsonObject = new JSONObject(result);

                in.close();
                conn.disconnect();

                String token = jsonObject.getString("token");
                SaveSharedPreference.setToken(cxt, token);
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }



    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
