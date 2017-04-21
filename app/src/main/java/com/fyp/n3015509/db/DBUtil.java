package com.fyp.n3015509.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;

import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.dao.BuzzNotification;
import com.fyp.n3015509.dao.NotificationTypes;
import com.fyp.n3015509.dao.PriceChecker;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsShelf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
                        long authorId = db.insertAuthor(author);
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


    public ArrayList<BuzzNotification> CreateWatchNotifications(Context ctx) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            return db.AddToWatchNotifications();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<BuzzNotification> GetWatchNotifications(FragmentActivity activity) {
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
            return db.MarkAsRead(mBook, mType);
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

    public ArrayList<BuzzNotification> CreatePriceCheckerNotifications(Context ctx, ConcurrentHashMap<String, ArrayList<PriceChecker>> isbn) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            return db.AddPriceCheckerNotification(isbn);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void InsertFromAPI(JSONObject object, Context cxt) {
        try {
            String listString = object.getString("list");
            String bookString = object.getString("books");
            String authorString = object.getString("authors");

            JSONArray lists = new JSONArray(listString);
            JSONArray books = new JSONArray(bookString);
            JSONArray authors = new JSONArray(authorString);

            HashMap<Integer, GoodreadsAuthor> authorList = new HashMap();
            HashMap<Integer, GoodreadsBook> bookList = new HashMap();
            ArrayList<GoodreadsShelf> buzzList = new ArrayList<>();
            int length = authors.length();
            for (int m = 0; m < authors.length(); m++) {
                GoodreadsAuthor author = CreateGoodreadsAuthor(authors.getJSONObject(m));
                authorList.put(author.getId(), author);
            }

            for (int n = 0; n < books.length(); n++) {
                GoodreadsBook book = CreateGoodreadsBook(books.getJSONObject(n));
                JSONArray authorIds = books.getJSONObject(n).getJSONArray("author");
                ArrayList<GoodreadsAuthor> bAuthors = new ArrayList();
                for (int m = 0; m < authorIds.length(); m++) {
                    bAuthors.add(authorList.get(authorIds.get(m)));
                }
                book.setAuthors(bAuthors);
                bookList.put(book.getId(), book);
            }

            for (int i = 0; i < lists.length(); i++) {
                GoodreadsShelf buzz = new GoodreadsShelf();
                buzz.setShelfName(lists.getJSONObject(i).getString("list_name"));
                JSONArray bookIds = lists.getJSONObject(i).getJSONArray("book_list");
                ArrayList<GoodreadsBook> gBooks = new ArrayList();
                for (int m = 0; m < bookIds.length(); m++) {
                    gBooks.add(bookList.get(bookIds.get(m)));
                }
                buzz.setBooks(gBooks);
                buzzList.add(buzz);
            }

            DBUtil.SaveShelf(buzzList, cxt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static GoodreadsAuthor CreateGoodreadsAuthor(JSONObject o) {
        GoodreadsAuthor b = new GoodreadsAuthor();
        try {
            b.setId(checkForNullInt(o, "goodreads_id"));
            b.setName(checkForNullString(o, "name"));
            b.setImage(checkForNullString(o, "img"));
            b.setLink(checkForNullString(o, "link"));
            b.setAverage_rating(checkForNullDouble(o, "avg_rating"));
            b.setRatingsCount(checkForNullInt(o, "ratings_count"));
            b.setTextReviewsCount(checkForNullInt(o, "reviews_count"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private static GoodreadsBook CreateGoodreadsBook(JSONObject o) {
        GoodreadsBook b = new GoodreadsBook();
        try {
            b.setId(checkForNullInt(o, "work_id"));
            b.setIsbn(checkForNullString(o,"isbn"));
            b.setIsbn13(checkForNullString(o,"isbn13"));
            b.setTextReviewsCount(checkForNullInt(o, "text_reviews_count"));
            b.setTitle(checkForNullString(o, "title"));
            b.setTitleWithoutSeries(checkForNullString(o, "title_without_name"));
            b.setImage_url(checkForNullString(o, "image_url"));
            b.setSmall_image_url(checkForNullString(o, "small_img"));
            b.setLink(checkForNullString(o, "link"));
            //b.setNumPages(o.getInt(""));
            b.setYearPublished(checkForNullInt(o, "yearPublished"));
            b.setAverage_rating(checkForNullDouble(o,"avg_rating"));
            b.setPublisher(checkForNullString(o,"publisher"));
            b.setReleaseDate(checkForNullString(o, "release_date"));
            //b.setRatingsCount(o.getInt(""));
            b.setDescription(checkForNullString(o, "blurb"));
            b.setFormat(checkForNullString(o, "format"));
            //b.setEditionInformation(o.getString(""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private static int checkForNullInt(JSONObject jsonObject, String work_id) {
        try {
            if (jsonObject.isNull(work_id)) {
                System.out.println("inside null");
                return 0;
            } else {
                System.out.println("inside else part");
                //jsonObject.put("parentId", jsonObject.getInt("parentId"));
                return jsonObject.getInt(work_id);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    private static String checkForNullString(JSONObject jsonObject, String work_id) {
        try {
            if (jsonObject.isNull(work_id)) {
                System.out.println("inside null");
                return "";
            } else {
                System.out.println("inside else part");
                //jsonObject.put("parentId", jsonObject.getInt("parentId"));
                return jsonObject.getString(work_id);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    private static Double checkForNullDouble(JSONObject jsonObject, String work_id) {
        try {
            if (jsonObject.isNull(work_id)) {
                System.out.println("inside null");
                return 0.0;
            } else {
                System.out.println("inside else part");
                //jsonObject.put("parentId", jsonObject.getInt("parentId"));
                return jsonObject.getDouble(work_id);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0.0;
    }

    private static byte[] getBitmapFromString(String jsonString) {

        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedString;
    }

}
