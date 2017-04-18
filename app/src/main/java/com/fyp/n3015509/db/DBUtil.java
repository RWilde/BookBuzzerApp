package com.fyp.n3015509.db;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.bookbuzzerapp.WatchBooksService;
import com.fyp.n3015509.dao.BuzzNotification;
import com.fyp.n3015509.dao.NotificationTypes;
import com.fyp.n3015509.dao.PriceChecker;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsShelf;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

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

    public static ArrayList<BuzzNotification> getNotifications(FragmentActivity activity) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(activity);
            return db.getNotifications();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean RemoveNotification(FragmentActivity mContext, int mBook) {
        return null;
    }

    public static Boolean MarkNotificationAsRead(FragmentActivity mContext, int mBook, NotificationTypes mType) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(mContext);
            return db.markAsRead(mBook, mType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static GoodreadsBook getBook(Context mContext, int mBook) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(mContext);
            return db.getBook(mBook);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<GoodreadsBook> getWatchedBooks(FragmentActivity activity) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(activity);
            return db.getWatchedBooks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean checkIfWatched(Context activity, int mBookId) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(activity);
            return db.checkIfWatched(mBookId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean RemoveFromWatched(Context mContext, int mBookId) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(mContext);
            return db.RemoveBookFromWatchList(mBookId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String[] GetISBNFromWatch(Context mContext) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(mContext);
            return db.GetWatchedISBNs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<PriceChecker> CheckAgainstDb(Context mContext, ArrayList<PriceChecker> priceCheckValues, String isbn) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(mContext);
            return db.CheckDBForPreviousPrices(priceCheckValues, isbn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer[] CreatePriceCheckerNotifications(Context ctx, ConcurrentHashMap<String, ArrayList<PriceChecker>> isbn) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            return db.addPriceCheckerNotification(isbn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}