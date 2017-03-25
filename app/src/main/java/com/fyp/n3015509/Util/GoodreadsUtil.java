package com.fyp.n3015509.Util;

import android.content.Context;

import com.fyp.n3015509.db.dao.Book;
import com.fyp.n3015509.goodreads.GoodreadsAuthor;
import com.fyp.n3015509.goodreads.GoodreadsBook;
import com.fyp.n3015509.goodreads.GoodreadsShelf;
import com.fyp.n3015509.goodreadsapi.GoodreadsShelves;

import java.util.ArrayList;

/**
 * Created by tomha on 24-Mar-17.
 */

public class GoodreadsUtil {
    public static GoodreadsShelf createGoodreadsShelf(int id, String name, int count) {
        GoodreadsShelf shelf = new GoodreadsShelf();
        return shelf.createGoodreadsShelf(name, count, id);
    }

    public static GoodreadsAuthor createGoodreadsAuthor(int authorId, String authorName, String authorImageURL, String authorSmallImageURL, String authorLink, double authorAverageRating, int authorRatingsCount, int authorTextReviewsCount) {
        GoodreadsAuthor author = new GoodreadsAuthor();
        return author.createGoodreadsAuthor(authorId, authorName, authorImageURL, authorSmallImageURL, authorLink, authorAverageRating, authorRatingsCount, authorTextReviewsCount);
    }

    public static ArrayList<GoodreadsShelf> getShelves(Context ctx)
    {
        GoodreadsShelves shelves = new GoodreadsShelves();
        return shelves.getShelves(ctx);
    }

    public static Boolean RetrieveSelectedShelves(Context ctx, ArrayList<GoodreadsShelf> options) {
        GoodreadsShelves shelves = new GoodreadsShelves();
        ArrayList<ArrayList<GoodreadsBook>> allBooks = new ArrayList<ArrayList<GoodreadsBook>>();
        for (GoodreadsShelf shelf : options)
        {
            ArrayList<GoodreadsBook> booklist = shelves.getBookShelf(ctx,shelf);
            DBUtil.SaveShelf();
            APIUtil.SaveShelf();
        }
        return true;
    }
}
