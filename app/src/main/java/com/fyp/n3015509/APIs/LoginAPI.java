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
    private static final String IP = "192.168.0.4";
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

    public static String SignIn(Context cxt, JSONObject login) {
        try {
            URL authURL = new URL(loginURL);
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

            String json_response = "";
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String text = "";
            JSONObject jsonObj = null;

            while ((text = br.readLine()) != null) {
                json_response += text;
                jsonObj = new JSONObject(json_response);
            }

            if (status == 200) {
                String token = jsonObj.getString("token");
                SaveSharedPreference.setToken(cxt, token);
                return token;

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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

    public static String RegisterNewUser(Context cxt, JSONObject login) {
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
                String token = SignIn(cxt, login);
                return token;

            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    public static void RegisterGoodreadsUser(Context cxt, JSONObject login) {
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
                JSONObject jsonObject = new JSONObject(result);

                JSONParser parser_obj = new JSONParser();
                try {
                    JSONArray lists = (JSONArray) parser_obj.parse(jsonObject.getString("list"));
                    JSONArray books = (JSONArray) parser_obj.parse(jsonObject.getString("books"));
                    JSONArray authors = (JSONArray) parser_obj.parse(jsonObject.getString("authors"));

                    HashMap<Integer, GoodreadsAuthor> authorList = new HashMap();
                    HashMap<Integer, GoodreadsBook> bookList = new HashMap();
                    ArrayList<Buzzlist> buzzList = new ArrayList<>();
                    for (int i = 0; i > authors.length(); i++) {
                        GoodreadsAuthor author = CreateGoodreadsAuthor(authors.getJSONObject(i));
                        authorList.put(author.getId(), author);
                    }

                    for (int i = 0; i > books.length(); i++) {
                        GoodreadsBook book = CreateGoodreadsBook(books.getJSONObject(i));
                        JSONArray authorIds = books.getJSONObject(i).getJSONArray("author");
                        ArrayList<GoodreadsAuthor> bAuthors = new ArrayList();
                        for (int m = 0; m > authorIds.length(); m++) {
                            bAuthors.add(authorList.get(authorIds.get(m)));
                        }
                        book.setAuthors(bAuthors);
                        bookList.put(book.getId(), book);
                    }

                    for (int i = 0; i > lists.length(); i++) {
                        Buzzlist buzz = new Buzzlist();
                        buzz.setListName(lists.getJSONObject(i).getString("list_name"));
                        JSONArray bookIds = lists.getJSONObject(i).getJSONArray("book_list");
                        ArrayList<GoodreadsBook> gBooks = new ArrayList();
                        for (int m = 0; m > bookIds.length(); m++) {
                            gBooks.add(bookList.get(bookIds.get(m)));
                        }
                        buzz.setBookList(gBooks);
                        buzzList.add(buzz);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }





                in.close();
                conn.disconnect();


                Log.d("response", Integer.toString(status));

//                String json_response = "";
//                InputStreamReader in = new InputStreamReader(conn.getInputStream());
//                BufferedReader br = new BufferedReader(in);
//                String text = "";
//                JSONObject jsonObj = null;
//
//                while ((text = br.readLine()) != null) {
//                    json_response += text;
//                    jsonObj = new JSONObject(json_response);
//                }

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
    }

    private static GoodreadsAuthor CreateGoodreadsAuthor(JSONObject o) {
        GoodreadsAuthor b = new GoodreadsAuthor();
        try {
            b.setId(o.getInt("goodreads_id"));
            b.setName(o.getString("name"));
            b.setImageDB(getBitmapFromString(o.getString("img")));
            b.setLink(o.getString("link"));
            b.setAverage_rating(o.getDouble("avg_rating"));
            b.setRatingsCount(o.getInt("ratings_count"));
            b.setTextReviewsCount(o.getInt("reviews_count"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private static GoodreadsBook CreateGoodreadsBook(JSONObject o) {
        GoodreadsBook b = new GoodreadsBook();
        try{
        b.setId(o.getInt("work_id"));
        b.setIsbn(o.getString("isbn"));
        b.setIsbn13(o.getString("isbn13"));
        b.setTextReviewsCount(o.getInt("text_reviews_count"));
        b.setTitle(o.getString("title"));
        b.setTitleWithoutSeries(o.getString("title_without_name"));
        b.setImage(getBitmapFromString(o.getString("image_url")));
        b.setSmallImage(getBitmapFromString(o.getString("small_img")));
        b.setLink(o.getString("link"));
       //b.setNumPages(o.getInt(""));
        b.setYearPublished(o.getInt("yearPublished"));
        b.setAverage_rating(o.getDouble("avg_rating"));
        b.setPublisher(o.getString("publisher"));
        b.setReleaseDate(o.getString("release_date"));
        //b.setRatingsCount(o.getInt(""));
        b.setDescription(o.getString("blurb"));
        b.setFormat(o.getString("format"));
        //b.setEditionInformation(o.getString(""));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return b;
    }

    private static byte[] getBitmapFromString(String jsonString) {

        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedString;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
