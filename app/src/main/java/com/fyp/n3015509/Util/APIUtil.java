package com.fyp.n3015509.Util;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.fyp.n3015509.apiDAO.APIBookList;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rwild on 24/03/2017.
 */

public class APIUtil {
    private static final String IP = "192.168.0.4";
    private static final String BaseURL = "http://" + IP + ":8081/api";
    private static final String NewBuzzlistURL = BaseURL + "/buzzlist/shelfimport";
    private static final String RegisterURL = BaseURL + "/users/signup";
    private static final String FacebookLoginURL = BaseURL + "/users/signupfacebook";
    private static final String GoodreadsLoginURL = BaseURL + "/users/signupgoodreads";
    private static final String DeleteBookURL = BaseURL + "/buzzlist/book/";

    public static void SaveShelf(JSONObject booklist, Context ctx) {

        try {
            URL authURL = new URL(NewBuzzlistURL);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(ctx));
            conn.setRequestProperty("Content-Type", "application/json");

            //conn.connect();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String json = booklist.toString();

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

    public JSONObject convertToApi(ArrayList<GoodreadsBook> booklist) {
        JSONObject listJSON = new JSONObject();
        JSONObject bookJSON = new JSONObject();
        JSONObject authorJSON = new JSONObject();

        APIBookList list = new APIBookList();
        //booklist.remove(booklist.size() - 1);
        HashMap<String, String> books = new HashMap<String, String>();

        for (GoodreadsBook book : booklist) {
            if (book != null) {
                try {
                    authorJSON = createJSONAuthor(book.getAuthors());
                    bookJSON = createJSONbook(book, authorJSON);
                    listJSON.put(book.getTitle(), bookJSON);
                    //bookJSON.put("book", new Gson().toJson(book));
                    //  APIBook newBook = new APIBook();
                    //   books.put(book.getTitle(),new Gson().toJson();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // list.setBookList(books);

        int num = listJSON.length();
        return listJSON;

    }

    private JSONObject createJSONbook(GoodreadsBook book, JSONObject authorJSON) {
        JSONObject jsonBook = new JSONObject();
        try {
            jsonBook.put("id", book.getId());
            jsonBook.put("ratings_count", book.getRatingsCount());
            jsonBook.put("txt_reviews_count", book.getTextReviewsCount());
            jsonBook.put("img_url", book.getImage());
            jsonBook.put("sml_img_url", book.getSmallImage());
            jsonBook.put("lrg_img_url", book.getLargeImage());
            jsonBook.put("link", book.getLink());
            jsonBook.put("avg_rating", book.getAverage_rating());
            jsonBook.put("description", book.getDescription());
            jsonBook.put("isbn", book.getIsbn());
            jsonBook.put("isbn13", book.getIsbn13());
            jsonBook.put("title", book.getTitle());
            jsonBook.put("title_without_series", book.getTitleWithoutSeries());

            jsonBook.put("format", book.getFormat());
            jsonBook.put("edition_information", book.getEditionInformation());
            jsonBook.put("publisher", book.getPublisher());
            jsonBook.put("date", book.getPublicationDay() + "/" + book.getPublicationMonth() + "/" + book.getPublicationYear());
            jsonBook.put("average_rating", book.getAverage_rating());
            jsonBook.put("yearPublished", book.getYearPublished());
            jsonBook.put("authors", authorJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonBook;
    }

    public JSONObject createApiBook(GoodreadsBook book, JSONObject author) {
        return null;
    }

    public JSONObject createJSONAuthor(ArrayList<GoodreadsAuthor> authors) {
        JSONObject authorListJson = new JSONObject();
        for (GoodreadsAuthor author : authors) {
            JSONObject temp = new JSONObject();
            try {
                temp.put("name", author.getName());
                temp.put("avg_rating", author.getAverage_rating());
                temp.put("id", author.getId());
                temp.put("img_url", author.getImage());
                temp.put("link", author.getLink());
                temp.put("ratings_count", author.getRatingsCount());
                temp.put("sml_img_url", author.getSmallImage());
                temp.put("txt_reviews_count", author.getTextReviewsCount());
                authorListJson.put(String.valueOf(author.getId()), temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return authorListJson;
    }

    public static Boolean RemoveBookFromBuzzlist(Context mContext, int bookId, String mListId, String token) {
        try {
            String url = DeleteBookURL + URLEncoder.encode(mListId,"UTF-8") + "/" + bookId;
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            int response = conn.getResponseCode();
            if ( response == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static Boolean WatchBook(Context mContext, int mBook, String listName) {
        return true;
    }

    public static Boolean RemoveNotification(FragmentActivity mContext, int mBook, String token) {
        return null;
    }

    public static Boolean MarkNotificationAsRead(FragmentActivity mContext, int mBook, String token) {
        return null;
    }
}
