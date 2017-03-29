package com.fyp.n3015509.Util;

import android.content.Context;

import com.fyp.n3015509.apiDAO.APIBook;
import com.fyp.n3015509.apiDAO.APIBookList;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by rwild on 24/03/2017.
 */

class APIUtil {
    private static final String IP = "192.168.0.10";
    private static final String BaseURL = "http://" + IP +":8081/api";
    private static final String NewBuzzlistURL = BaseURL + "/buzzlist/shelfimport";
//    private static final String RegisterURL = BaseURL + "/users/signup";
//    private static final String FacebookLoginURL = BaseURL + "/users/signupfacebook";
//    private static final String GoodreadsLoginURL = BaseURL + "/users/signupgoodreads";

    public static void SaveShelf(ArrayList<APIBookList> booklist, Context ctx) {

        try {
            URL authURL = new URL(NewBuzzlistURL);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty ("Authorization", SaveSharedPreference.getToken(ctx));
            conn.setRequestProperty("Content-Type", "application/json");

            //conn.connect();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String json = new Gson().toJson(booklist);

            out.write(json);
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

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return null;

    }

    public APIBookList convertToApi(ArrayList<GoodreadsBook> booklist) {
        APIBookList list = new APIBookList();
        //booklist.remove(booklist.size() - 1);
        ArrayList<APIBook> books= new ArrayList<APIBook>();
            for (GoodreadsBook book : booklist) {
                if(book != null) {
                    try {
                        APIBook newBook = new APIBook();
                        books.add(newBook.createApiBook(book));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            list.setBookList(books);

            return list;

    }
}
