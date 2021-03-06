package com.fyp.n3015509.APIs;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.dao.APIdao.APIBookList;
import com.fyp.n3015509.dao.BuzzNotification;
import com.fyp.n3015509.dao.enums.EditionTypes;
import com.fyp.n3015509.dao.enums.NotificationTypes;
import com.fyp.n3015509.dao.PriceChecker;
import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rwild on 24/03/2017.
 */

public class BookBuzzerAPI {
    private static final String IP = "192.168.0.5";
    private static final String BaseURL = "http://" + IP + ":8081/api";
    private static final String BuzzlistURL = BaseURL + "/buzzlist/";
    private static final String NewBuzzlistURL = BaseURL + "/buzzlist/shelfimport";
    private static final String CreateBuzzlistURL = BaseURL + "/buzzlist/emptybuzz";
    private static final String NewBookURL = BaseURL + "/buzzlist/newbook";
    private static final String RegisterURL = BaseURL + "/users/signup";
    private static final String FacebookLoginURL = BaseURL + "/users/signupfacebook";
    private static final String GoodreadsLoginURL = BaseURL + "/users/signupgoodreads";
    private static final String DeleteBookURL = BaseURL + "/buzzlist/book/";
    private static final String GoodreadsSync = BaseURL + "/users/sync";
    private static final String GoodreadsUpdateId = BaseURL + "/users/updategoodreadsId";
    private static final String UpdateName= BaseURL + "/users/updatename";;
    private static final String RemoveBuzzlistURL = BaseURL + "/buzzlist/";

    private static final String WatchBookURL = BaseURL + "/watch/book/";
    private static final String NotificationURL = BaseURL + "/notification/";
    private static final String PriceCheckerURL = BaseURL + "/watch/price";
    private static final String TAG = "price checker";
    private static final String ExpectedPubURL = BaseURL + "/watch/expected/";


    public static boolean SaveShelf(JSONObject booklist, Context ctx) {

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

        return false;
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

    public JSONObject convertToApi(GoodreadsBook book, String listName) {
        JSONObject bookJSON = new JSONObject();
        JSONObject authorJSON = new JSONObject();

        if (book != null) {
            try {
                authorJSON = createJSONAuthor(book.getAuthors());
                bookJSON = createJSONbook(book, authorJSON);
                bookJSON.put("list_name", listName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bookJSON;
    }

    private JSONObject createJSONbook(GoodreadsBook book, JSONObject authorJSON) {
        JSONObject jsonBook = new JSONObject();
        try {
            jsonBook.put("id", book.getId());
            jsonBook.put("ratings_count", book.getRatingsCount());
            jsonBook.put("txt_reviews_count", book.getTextReviewsCount());
            jsonBook.put("img_url", book.getImgUrl());
            jsonBook.put("sml_img_url", book.getSmallImgUrl());
            jsonBook.put("lrg_img_url", book.getLrgImgUrl());
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
                temp.put("img_url", author.getImgLink());
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

    public static Boolean RemoveBookFromBuzzlist(Context mContext, int bookId, String mListId) {
        try {
            String url = DeleteBookURL + URLEncoder.encode(mListId, "UTF-8") + "/" + bookId;
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            int response = conn.getResponseCode();
            if (response == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public Boolean WatchBook(Context mContext, int mBook, String listName) {
        try {
            JSONObject object = convertToApi(DBUtil.getBook(mContext, mBook), listName);
            String url = WatchBookURL;
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String json = object.toString();

            out.write(json);
            out.close();

            int response = conn.getResponseCode();
            if (response == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public Boolean SaveNotifications(Context ctx, ArrayList<BuzzNotification> buzzList) {
        try {
            String url = NotificationURL;
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(ctx));
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            Gson gson = new Gson();
            String json = gson.toJson(buzzList);

            out.write(json);
            out.close();

            int response = conn.getResponseCode();
            if (response == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static Boolean RemoveNotification(FragmentActivity mContext, int mBook, NotificationTypes mType) {
        try {
            String url = NotificationURL +"/id="+ mBook + "&type="+mType.toString();
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            int response = conn.getResponseCode();
            if (response == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static Boolean MarkNotificationAsRead(FragmentActivity mContext, int mBook, NotificationTypes mType) {
        try {
            String url = NotificationURL +"read/id="+ mBook + "&type="+mType.toString();
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            int response = conn.getResponseCode();
            if (response == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public ArrayList<PriceChecker> RunPriceChecker(Context mContext, String isbn) {
        ArrayList<PriceChecker> cheaperOptions = new ArrayList<>();
        String jsonReply;
        try {
            String url = PriceCheckerURL;
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            JSONObject json = new JSONObject();
            json.put("isbn", isbn);

            String jsonString = json.toString();

            out.write(jsonString);
            out.close();

            InputStream response = conn.getInputStream();
            jsonReply = convertStreamToString(response);

            JSONObject obj = new JSONObject(jsonReply);
            JSONArray array = (JSONArray) obj.get("jsonArray");

            ArrayList<PriceChecker> priceCheckValues = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject priceCheck = array.getJSONObject(i);
                PriceChecker priceChecker = null;
                String type = (String) priceCheck.get("type");
                if(type.contains("Paperback")) {
                    priceChecker = new PriceChecker();
                    priceChecker.setType(EditionTypes.PAPERBACK);
                    String price = checkIfNotNull(priceCheck, "price");
                    if (!price.contentEquals("")) {
                        price = price.replace("£", "");
                        priceChecker.setPrice(Double.parseDouble(price));
                        priceCheckValues.add(priceChecker);
                    }
                }
                else if(type.contains("Hardcover"))
                {
                    priceChecker = new PriceChecker();
                    priceChecker.setType(EditionTypes.HARDBACK);
                    String price = checkIfNotNull(priceCheck, "price");
                    if (!price.contentEquals("")) {
                        price = price.replace("£", "");
                        priceChecker.setPrice(Double.parseDouble(price));
                        priceCheckValues.add(priceChecker);
                    }
                }
                else if (type.contains("Kindle Edition"))
                {
                    priceChecker = new PriceChecker();
                    priceChecker.setType(EditionTypes.KINDLE_EDITION);
                    String price = checkIfNotNull(priceCheck, "price");
                    if (!price.contentEquals("")) {
                        price = price.replace("£", "");
                        priceChecker.setPrice(Double.parseDouble(price));
                        priceCheckValues.add(priceChecker);
                    }
                }
            }
            return priceCheckValues;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String checkIfNotNull(JSONObject priceCheck, String price) {
        if (priceCheck.has(price) && !priceCheck.isNull(price)) {
            try {
                return priceCheck.getString(price);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public Boolean CreateBookAndList(Context mContext, GoodreadsBook mBook, String listName) {
        try {
            URL authURL = new URL(NewBookURL);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");

            //conn.connect();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            GoodreadsBook gb = new GoodreadsBook();
            Gson gson = new Gson();

            JSONObject o = convertToApi(mBook, listName);

            out.write(o.toString());
            out.close();

            int status = conn.getResponseCode();

            if (status == 200) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //return null;

        return false;
    }

    public Boolean AddBookToList(Context mContext, GoodreadsBook mBook, String listName) {
        try {
            URL authURL = new URL(NewBookURL);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");

            //conn.connect();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            Gson gson = new Gson();

            GoodreadsBook gb = new GoodreadsBook();

            gb.createGoodreadsBookJSON(mBook.getId(), mBook.getIsbn(), mBook.getIsbn13(), mBook.getTextReviewsCount(), mBook.getTitle(), mBook.getTitleWithoutSeries(), mBook.getImgUrl(), mBook.getSmallImgUrl(), mBook.getLrgImgUrl(), mBook.getLink(), mBook.getNumPages(), mBook.getFormat(),mBook.getEditionInformation(), mBook.getPublisher(), mBook.getPublicationDay(), mBook.getPublicationYear(), mBook.getPublicationMonth(), mBook.getAverage_rating(), mBook.getRatingsCount(), mBook.getDescription(), mBook.getYearPublished(), mBook.getAuthors());

            String book = gson.toJson(gb);
            JSONObject o = new JSONObject();
            o.put("book", book);
            o.put("list_name", listName);

            out.write(o.toString());
            out.close();


            int status = conn.getResponseCode();
            if (status == 200) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void SaveBuzzlist(Context mContext, String name) {
        try {
            URL authURL = new URL(CreateBuzzlistURL);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");

            //conn.connect();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            Gson gson = new Gson();
            JSONObject o = new JSONObject();
            o.put("name", name);

            out.write(o.toString());
            out.close();

            int status = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void ModifyBuzzlist(Context mContext, String name, String newName) {
        try {
            URL authURL = new URL(BuzzlistURL);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");

            //conn.connect();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            Gson gson = new Gson();
            JSONObject o = new JSONObject();
            o.put("name", name);
            o.put("newName", newName);

            out.write(o.toString());
            out.close();

            int status = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean RemoveFromWatched(Context mContext, Integer goodreadsIdgoodreadsId) {
        try {
            URL authURL = new URL(BuzzlistURL + goodreadsIdgoodreadsId);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");

            int status = conn.getResponseCode();

            if (status == 200)
            {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean ChangeUserName(Context mContext, String mName) {
        try {
            URL authURL = new URL(UpdateName);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            JSONObject o = new JSONObject();

            o.put("name", mName);
            out.write(o.toString());

            out.close();
            int status = conn.getResponseCode();

            if (status == 200)
            {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean AddGoodreadsAccount(Context mContext, JSONObject login) {
        try {
            URL authURL = new URL(GoodreadsUpdateId);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            out.write(login.toString());
            out.close();

            int status = conn.getResponseCode();

            if (status == 200)
            {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONObject Sync(Context mContext) {
        JSONObject jsonObject = new JSONObject();
        try {
            URL authURL = new URL(GoodreadsSync);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            int status = conn.getResponseCode();

            if (status == 200) {
                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = convertStreamToString(in);
                jsonObject = new JSONObject(result);

                in.close();
                conn.disconnect();
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

    public static Boolean RemoveBuzzlist(FragmentActivity mContext, String mName) {
        try {
            URL authURL = new URL(RemoveBuzzlistURL + URLEncoder.encode(mName, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");

            int status = conn.getResponseCode();

            if (status == 200)
            {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public JSONObject GetExpectedPub(Context mContext, int seriesId) {
        JSONObject o = new JSONObject();
        try {
            URL authURL = new URL(ExpectedPubURL + seriesId);
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SaveSharedPreference.getToken(mContext));
            conn.setRequestProperty("Content-Type", "application/json");

            int status = conn.getResponseCode();

            if (status == 200)
            {
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = convertStreamToString(in);
                o = new JSONObject(result);

                in.close();
                conn.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }
}
