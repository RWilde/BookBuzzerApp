package com.fyp.n3015509.Util;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.fyp.n3015509.apiDAO.APIBookList;
import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.db.MySQLiteHelper;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.fyp.n3015509.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.goodreadsDAO.GoodreadsShelf;
import com.fyp.n3015509.tasks.WatchBooksService;
import com.google.android.gms.gcm.PeriodicTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by n3015509 on 24/03/2017.
 */

public class DBUtil {

    public static int SaveShelf(ArrayList<GoodreadsShelf> booklist, Context ctx) {
        ArrayList<GoodreadsBook> dbBooks = new ArrayList<>();
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);

            for (GoodreadsShelf shelf : booklist) {
                ArrayList<GoodreadsBook> books = shelf.getBooks();
                long shelfId = db.insertBouzzlist(shelf);
                for (GoodreadsBook book : books) {
                    long id = db.insertBook(book);
                    ArrayList<GoodreadsAuthor> authors = book.getAuthors();
                    for (GoodreadsAuthor author : authors) {
                        long authorId = db.insertAuthor(author, ctx);
                        if (authorId != 0) db.insertBookInterim(id, authorId);
                    }
                    db.insertBouzzlistInterim(shelfId, id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // int count = db.getBooksCount();
        int count = 0;
        return count;
    }

    public static ArrayList<Buzzlist> GetBuzzlist(Context ctx) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            return db.getBuzzlists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ArrayList<GoodreadsBook> getBooksFromBuzzlist(FragmentActivity ctx, int listId) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            return db.getBooksFromBuzzlist(listId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GoodreadsBook getBookFromBuzzlist(FragmentActivity ctx, int bookId) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            return db.getBook(bookId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean RemoveBookFromBuzzList(Context ctx, int name, int list) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            return db.removeBookFromBuzzlist(name, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean WatchBook(Context ctx, int mBookName) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            Date lastActive = SaveSharedPreference.getLastActiveDate(ctx);
            return db.addBookToWatchList(mBookName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getBuzzlistName(FragmentActivity activity, int listId) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(activity);
            return db.getBuzzlistName(listId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Integer[] createNotifications(Context ctx) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            return db.addToNotifications();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
