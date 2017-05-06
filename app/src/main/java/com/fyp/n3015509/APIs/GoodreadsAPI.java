package com.fyp.n3015509.APIs;

import android.content.Context;

import com.fyp.n3015509.Util.XMLUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.dao.SearchResult;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsShelf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by tomha on 23-Mar-17.
 */

public class GoodreadsAPI {

    public static final String GOODREADS_KEY = "8vvXeL81U1l6yxnUT1c9Q";
    public static final String BASE_URL = "https://www.goodreads.com";
    private static final String ShelvesURL = BASE_URL + "/shelf/list.xml?key=" + GOODREADS_KEY + "&user_id=";
    private static final String BookShelvesURL = BASE_URL + "/review/list/";

    private static final String SearchURL = BASE_URL + "/search/index.xml?key=" + GOODREADS_KEY + "&q=";
    private static final String SearchAuthorURL = BASE_URL + "/api/author_url/";
    private static final String DownloadBookURL = BASE_URL +"/book/title.xml?key=" + GOODREADS_KEY+"&title=";
    private static final String IdToWorkIdURL = BASE_URL + "/book/id_to_work_id/";
    private static final String SeriesIdURL = BASE_URL + "/work/";
    //https://www.goodreads.com/book/title.xml?author=Arthur+Conan+Doyle&key=8vvXeL81U1l6yxnUT1c9Q&title=Hound+of+the+Baskervilles

    public String getShelvesURL(Context ctx) {
        String userId = SaveSharedPreference.getGoodreadsId(ctx);
        return ShelvesURL + userId;
    }

    public String getAuthorURL(String author) {
        return SearchAuthorURL + author + "key=" + GOODREADS_KEY;
    }

    public ArrayList<GoodreadsShelf> getShelves(Context ctx) {
        StringBuffer response = new StringBuffer();
        XMLUtil xmlUtil = new XMLUtil();
        try {
            URL authURL = new URL(getShelvesURL(ctx));
            HttpsURLConnection conn = (HttpsURLConnection) authURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15 * 1000);

            int status = conn.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine.toString());
            }
            //close input stream
            in.close();
            ArrayList<GoodreadsShelf> shelves = xmlUtil.xmlToGoodreadsShelves(response.toString());
            //parse response to get user Id
            return shelves;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createBookShelvesUrl(Context ctx, String shelf, int page) {
        return BookShelvesURL + SaveSharedPreference.getGoodreadsId(ctx) + ".xml?key=" + GOODREADS_KEY + "&v=2&shelf=" + shelf + "&per_page=200&page=" + page;
    }

    public ArrayList<GoodreadsBook> getBookShelf(Context ctx, GoodreadsShelf shelf) {
        StringBuffer response = new StringBuffer();
        XMLUtil xmlUtil = new XMLUtil();
        ArrayList<GoodreadsBook> books = new ArrayList<GoodreadsBook>();

        try {
            URL authURL = new URL(createBookShelvesUrl(ctx, shelf.getShelfName(), 1));
            HttpsURLConnection conn = (HttpsURLConnection) authURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15 * 1000);

            int status = conn.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine.toString());
            }
            //close input stream
            in.close();

            //parse response to get list of books
            books = xmlUtil.xmlToGoodreadsBooks(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }

    public ArrayList<SearchResult> getSearch(Context ctx, String search) {
        StringBuffer response = new StringBuffer();
        XMLUtil xmlUtil = new XMLUtil();
        ArrayList<SearchResult> result = new ArrayList();

        try {
            URL authURL = new URL(SearchURL + URLEncoder.encode(search, "UTF-8"));
            HttpsURLConnection conn = (HttpsURLConnection) authURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15 * 1000);

            int status = conn.getResponseCode();
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine.toString());
                }
                //close input stream
                in.close();
                result = xmlUtil.xmlToSearchResult(response.toString());
            }

            URL authorURL = new URL(getAuthorURL(search));
            HttpsURLConnection authConn = (HttpsURLConnection) authURL.openConnection();
            authConn.setRequestMethod("GET");
            authConn.setReadTimeout(15 * 1000);

            int authstatus = conn.getResponseCode();
            {

                BufferedReader in = new BufferedReader(new InputStreamReader(authConn.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine.toString());
                }
                //close input stream
                in.close();
                ArrayList<SearchResult> temp = xmlUtil.xmlToSearchResult(response.toString());

                if (temp != null) {
                    for (int i = 0; i < temp.size(); i++) {
                        result.add(temp.get(i));
                    }
                }

            }
            //parse response to get user Id
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GoodreadsBook DownloadBook(Context ctx, String bookName, String authorName) {
        StringBuffer response = new StringBuffer();
        XMLUtil xmlUtil = new XMLUtil();
        GoodreadsBook book = null;
        try {
            URL authURL = new URL(DownloadBookURL + URLEncoder.encode(bookName, "UTF-8") + "&author=" + URLEncoder.encode(authorName, "UTF-8"));
            HttpsURLConnection conn = (HttpsURLConnection) authURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15 * 1000);

            int status = conn.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine.toString());
            }
            //close input stream
            in.close();

            //parse response to get list of books
            book = xmlUtil.xmlToGoodreadsBook(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return book;
    }

    public int getWorkId(int i) {
        StringBuffer response = new StringBuffer();
        XMLUtil xmlUtil = new XMLUtil();
        GoodreadsBook book = null;
        try {
            URL authURL = new URL(IdToWorkIdURL + i + "?key=" + GOODREADS_KEY);
            HttpsURLConnection conn = (HttpsURLConnection) authURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15 * 1000);

            int status = conn.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine.toString());
            }
            //close input stream
            in.close();

            //parse response to get list of books
            return xmlUtil.xmlToWorkId(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getSeriesId(int workId) {
        StringBuffer response = new StringBuffer();
        XMLUtil xmlUtil = new XMLUtil();
        GoodreadsBook book = null;
        try {
            URL authURL = new URL(SeriesIdURL + workId + "/series?format=xml&key=" + GOODREADS_KEY);
            HttpsURLConnection conn = (HttpsURLConnection) authURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15 * 1000);

            int status = conn.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine.toString());
            }
            //close input stream
            in.close();

            //parse response to get list of books
            return xmlUtil.xmlToSeriesId(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
