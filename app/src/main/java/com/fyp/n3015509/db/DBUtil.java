package com.fyp.n3015509.db;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;

import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.dao.BuzzNotification;
import com.fyp.n3015509.dao.enums.NotificationTypes;
import com.fyp.n3015509.dao.PriceChecker;
import com.fyp.n3015509.dao.SearchResult;
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

    public static void SaveShelf(ArrayList<GoodreadsShelf> booklist, Context ctx) {
        ArrayList<GoodreadsBook> dbBooks = new ArrayList<>();
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            for (GoodreadsShelf shelf : booklist) {
                ArrayList<GoodreadsBook> books = shelf.getBooks();
                long shelfId = db.insertBuzzlist(shelf);
                for (GoodreadsBook book : books) {
                    long id = db.insertBook(book);
                    ArrayList<GoodreadsAuthor> authors = book.getAuthors();
                    for (GoodreadsAuthor author : authors) {
                        long authorId = db.insertAuthor(author);
                        if (authorId != 0) db.insertBookInterim(id, authorId);
                    }
                    db.insertBuzzlistInterim(shelfId, id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Buzzlist> GetBuzzlist(Context ctx) {
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
            String notificationString = object.getString("notifications");
            String watchString = object.getString("watched");

            JSONArray lists = new JSONArray(listString);
            JSONArray books = new JSONArray(bookString);
            JSONArray authors = new JSONArray(authorString);
            JSONArray notifications = new JSONArray(notificationString);
            JSONArray watched = new JSONArray(watchString);

            HashMap<Integer, GoodreadsAuthor> authorList = new HashMap();
            HashMap<Integer, GoodreadsBook> bookList = new HashMap();
            ArrayList<GoodreadsShelf> buzzList = new ArrayList<>();
            ArrayList<BuzzNotification> notificationList = new ArrayList<>();
            ArrayList<Integer> watch = new ArrayList<>();

            if (authors.length() > 0) {

                int length = authors.length();
                for (int m = 0; m < authors.length(); m++) {
                    GoodreadsAuthor author = CreateGoodreadsAuthor(authors.getJSONObject(m));
                    authorList.put(author.getId(), author);
                }
            }

            if (books.length() > 0) {

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
            }

            if (lists.length() > 0) {
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
            }

            if (notifications.length() > 0) {
                for (int o = 0; o < notifications.length(); o++) {
                    JSONObject j = notifications.getJSONObject(o);
                    String list = j.getString("book_list");
                    JSONArray notList = new JSONArray(list);
                    for (int q = 0; q < notList.length(); q++) {
                        BuzzNotification b = new BuzzNotification();
                        b.setGoodreadsId(notList.getJSONObject(q).getInt("book_id"));
                        b.setRead(notList.getJSONObject(q).getBoolean("read"));
                        b.setMessage(notList.getJSONObject(q).getString("message"));
                        b.setType(NotificationTypes.valueOf(notList.getJSONObject(q).getString("type")));
                        notificationList.add(b);
                    }
                }
            }

            if (watched.length() > 0) {
                for (int p = 0; p < watched.length(); p++) {
                    JSONObject j = watched.getJSONObject(p);
                    String list = j.getString("book_list");
                    JSONArray watchList = new JSONArray(list);
                    for (int q = 0; q < watchList.length(); q++) {
                        watch.add(Integer.parseInt(watchList.get(q).toString()));
                    }
                }
            }

            DBUtil.SaveShelf(buzzList, cxt);
            DBUtil.WatchBooks(cxt, watch);
            DBUtil.InsertNotifications(cxt, notificationList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean InsertNotifications(Context cxt, ArrayList<BuzzNotification> notificationList) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(cxt);
            return db.addNotifications(notificationList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean WatchBooks(Context cxt, ArrayList<Integer> watch) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(cxt);
            return db.addBooksToWatchList(watch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    public ArrayList<SearchResult> GetSearchResults(Context ctx, String query) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            return db.SearchForBookOrAuthor(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;    }

    public static String getIsbnByGoodreadsId(Context ctx, int goodreadsId) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(ctx);
            return db.getIsbnFromId(goodreadsId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static Boolean CreateBookAndList(Context mContext, GoodreadsBook mBook, String listName) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(mContext);
            return db.createBuzzlistAndBook(mBook, listName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;    }

    public static Boolean AddBookToList(Context mContext, GoodreadsBook mBook, String listName) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(mContext);
            return db.addBookToBuzzlist(mBook, listName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;    }

    public ArrayList<GoodreadsAuthor> GetAuthors(Context cxt) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(cxt);
            return db.getAuthors();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;    }

    public ArrayList<GoodreadsAuthor> GetBasicAuthors(Context cxt) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(cxt);
            return db.getBasicAuthors();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;    }

    public static GoodreadsAuthor GetAuthorById(Context context, int authorId) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(context);
            return db.getAuthorById(authorId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<GoodreadsBook> GetAllBookByAuthor(Context context, int authorId) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(context);
            return db.getBooksByAuthor(authorId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int GetUnopenedNotifications(Context context) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(context);
            return db.getUnopenedNotifications();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ArrayList<GoodreadsBook> GetBooks(FragmentActivity activity) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(activity);
            return db.getWatchedBooks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean SaveBuzzlist(Context mContext, String name) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(mContext);
            return db.CreateEmptyBuzz(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean CheckBuzzlistName(Context mContext, String name) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(mContext);
            return db.CheckBuzzlist(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean ModifyBuzzlist(Context mContext, String name, String newName) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(mContext);
            return db.ModifyBuzzlist(name, newName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String findListForBook(Context mContext, int mBook) {
        return null;
    }

    public static void deleteDatabase(Context applicationContext) {
        try
        {
            applicationContext.deleteDatabase("bookbuzzer.db");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Boolean RemoveBuzzlist(FragmentActivity mContext, String mName) {
        try {
            MySQLiteHelper db = new MySQLiteHelper(mContext);
            return db.DeleteBuzzlist(mName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
