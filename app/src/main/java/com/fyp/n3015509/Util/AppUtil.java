package com.fyp.n3015509.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;

import com.fyp.n3015509.bookbuzzerapp.other.ListViewAdapter;
import com.fyp.n3015509.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.goodreadsDAO.GoodreadsShelf;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tomha on 16-Apr-17.
 */

public class AppUtil {

    public ListViewAdapter setAdapter(ArrayList<GoodreadsBook> booklist, int listId, String listName, FragmentActivity activity) {
        ArrayList<String> buzzlistNames = new ArrayList<>();
        ArrayList<Bitmap> buzzlistImages = new ArrayList<>();
        ArrayList<String> buzzlistAuthors = new ArrayList<>();
        ArrayList<Integer> buzzlistIds = new ArrayList<>();

        Bitmap[] images = new Bitmap[booklist.size()];
        String[] values = new String[booklist.size()];
        String[] authors = new String[booklist.size()];
        Integer[] ids = new Integer[booklist.size()];

        for (GoodreadsBook buzz : booklist) {
            buzzlistNames.add(buzz.getTitle());
            buzzlistImages.add(buzz.getSmallImage());
            buzzlistIds.add(buzz.getId());
            String authorList = "";
            int count = 0;
            for (GoodreadsAuthor auth : buzz.getAuthors()) {
                authorList += auth.getName();
                if (count >= 1) {
                    authorList += ", ";
                }
                count++;
            }
            buzzlistAuthors.add(authorList);
        }
        values = buzzlistNames.toArray(values);
        images = buzzlistImages.toArray(images);
        authors = buzzlistAuthors.toArray(authors);
        ids = buzzlistIds.toArray(ids);

        return new ListViewAdapter(activity, values, images, authors, listId, ids, listName);
    }

    public Boolean SaveShelves(Context mContext, GoodreadsUtil util, ArrayList<GoodreadsShelf> options)
    {
        ArrayList<JSONObject> result = util.RetrieveSelectedShelves(mContext, options);
        boolean success = false;

        for (JSONObject shelf : result)
        {
            success = APIUtil.SaveShelf(shelf, mContext);
        }

        return success;
    }
}
