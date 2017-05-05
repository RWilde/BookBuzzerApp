package com.fyp.n3015509.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;

import com.fyp.n3015509.APIs.BookBuzzerAPI;
import com.fyp.n3015509.APIs.GoodreadsShelves;
import com.fyp.n3015509.bookbuzzerapp.other.ListViewAdapter;
import com.fyp.n3015509.dao.BookAdapter;
import com.fyp.n3015509.dao.PriceChecker;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsShelf;
import com.fyp.n3015509.db.DBUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tomha on 16-Apr-17.
 */

public class AppUtil {

    public ListViewAdapter setAdapter(ArrayList<GoodreadsBook> booklist, int listId, String listName, FragmentActivity activity) {
        ArrayList<String> buzzlistNames = new ArrayList<>();
        ArrayList<Bitmap> buzzlistImages = new ArrayList<>();
        ArrayList<String> buzzlistAuthors = new ArrayList<>();
        ArrayList<Integer> buzzlistIds = new ArrayList<>();
        ArrayList<String> buzzlistIsbns = new ArrayList<>();

        Bitmap[] images = new Bitmap[booklist.size()];
        String[] values = new String[booklist.size()];
        String[] authors = new String[booklist.size()];
        Integer[] ids = new Integer[booklist.size()];
        String[] isbns = new String[booklist.size()];

        ArrayList<BookAdapter> adapter = new ArrayList<>();

        for (GoodreadsBook buzz : booklist) {
            BookAdapter b = new BookAdapter();
            b.setBuzzlistNames(buzz.getTitle());
            b.setBuzzlistImages(buzz.getSmallImage());
            b.setBuzzlistIds(buzz.getId());
            b.setBuzzlistIsbns(buzz.getIsbn());

            String authorList = "";
            int count = 0;
            for (GoodreadsAuthor auth : buzz.getAuthors()) {
                authorList += auth.getName();
                if (count >= 1) {
                    authorList += ", ";
                }
                count++;
            }
            b.setBuzzlistAuthors(authorList);
            b.setAdded(false);
            adapter.add(b);

        }


        return new ListViewAdapter(activity, adapter , listId, listName);
    }

    public Boolean SaveShelves(Context mContext, GoodreadsShelves util, ArrayList<GoodreadsShelf> options)
    {
        ArrayList<JSONObject> result = util.RetrieveSelectedShelves(mContext, options);
        boolean success = false;

        for (JSONObject shelf : result)
        {
            success = BookBuzzerAPI.SaveShelf(shelf, mContext);
        }

        return success;
    }

    public ConcurrentHashMap<String, ArrayList<PriceChecker>> GetLatestPrices(Context ctx)
    {
        BookBuzzerAPI bb = new BookBuzzerAPI();
        DBUtil db = new DBUtil();
        ConcurrentHashMap<String, ArrayList<PriceChecker>> results = new ConcurrentHashMap<>();
        ArrayList<PriceChecker> p = new ArrayList<>();

        String[] isbns = db.GetISBNFromWatch(ctx);
        if (isbns != null) {
            for (String isbn : isbns) {
                 p = bb.RunPriceChecker(ctx, isbn);
                results.put(isbn, p);
            }
        }
        return results;
    }
}
