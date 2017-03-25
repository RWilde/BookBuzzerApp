package com.fyp.n3015509.goodreadsapi;

import android.app.Fragment;
import android.content.Context;

import com.fyp.n3015509.Util.GoodreadsUtil;
import com.fyp.n3015509.Util.XMLUtil;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.goodreads.GoodreadsBook;
import com.fyp.n3015509.goodreads.GoodreadsShelf;
import com.google.api.client.http.HttpResponse;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 * Created by tomha on 23-Mar-17.
 */

public class GoodreadsShelves {

    public static final String GOODREADS_KEY = "8vvXeL81U1l6yxnUT1c9Q";
    public static final String BASE_URL = "https://www.goodreads.com";
    private static final String ShelvesURL = BASE_URL + "/shelf/list.xml?key=" + GOODREADS_KEY + "&user_id=";
    private static final String BookShelvesURL = BASE_URL + "/review/list/";

    StringBuffer response = new StringBuffer();

    public String getShelvesURL(Context ctx) {
        String userId = SaveSharedPreference.getGoodreadsId(ctx);
        return ShelvesURL + userId;
    }

    public ArrayList<GoodreadsShelf> getShelves(Context ctx) {
        try {
            URL authURL = new URL(getShelvesURL(ctx));
            HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine.toString());
            }

            //close input stream
            in.close();

            //parse response to get user Id
            Document doc = XMLUtil.getXMLDocument(response.toString());
            NodeList nodeList = doc.getElementsByTagName("*");
            ArrayList<GoodreadsShelf> shelves = new ArrayList<GoodreadsShelf>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nodeList.item(i);
                    if (el.getNodeName().contains("user_shelf")) {
                        int id = Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
                        String name = el.getElementsByTagName("name").item(0).getTextContent();
                        int count = Integer.parseInt(el.getElementsByTagName("book_count").item(0).getTextContent());
                        GoodreadsShelf shelf = GoodreadsUtil.createGoodreadsShelf(id, name, count);
                        shelves.add(shelf);
                    }
                }
            }
            return shelves;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createBookShelvesUrl(Context ctx, String shelf, int page)
    {
        return BookShelvesURL + SaveSharedPreference.getGoodreadsId(ctx) + ".xml?key=" + GOODREADS_KEY  + "&v=2&shelf=" + shelf + "&per_page=200&page=" + page;
    }

    public ArrayList<GoodreadsBook> getBookShelf(Context ctx, GoodreadsShelf shelf)
    {
        ArrayList<GoodreadsBook> books = new ArrayList<GoodreadsBook>();

        try {
                URL authURL = new URL(createBookShelvesUrl(ctx, shelf.getShelfName(), 1));
                HttpURLConnection conn = (HttpURLConnection) authURL.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("GET");

                int status = conn.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine.toString());
                }

                //close input stream
                in.close();

                //parse response to get user Id
                Document doc = XMLUtil.getXMLDocument(response.toString());
                JSONObject xmlJSONObj = XML.toJSONObject(doc.toString());

//                NodeList nodeList = doc.getElementsByTagName("*");
//                ArrayList<GoodreadsShelf> shelves = new ArrayList<GoodreadsShelf>();
//                for (int i = 0; i < nodeList.getLength(); i++) {
//                    Node node = nodeList.item(i);
//                    if (node.getNodeType() == Node.ELEMENT_NODE) {
//                        Element el = (Element) nodeList.item(i);
//                        if (el.getNodeName().contains("reviews")) {
//                            if (el.getElementsByTagName("book"))
//                            String name = el.getElementsByTagName("name").item(0).getTextContent();
//                            String phone = el.getElementsByTagName("phone").item(0).getTextContent();
//                            String email = el.getElementsByTagName("email").item(0).getTextContent();
//                            String area = el.getElementsByTagName("area").item(0).getTextContent();
//                            String city = el.getElementsByTagName("city").item(0).getTextContent();
//
//
//                            GoodreadsBook book = GoodreadsUtil.createGoodreadsBook(id, name, count);
//                            books.add(book);
//                        }
//                    }
//                }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return books;
    }

}
