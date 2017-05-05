package com.fyp.n3015509.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.dao.BuzzNotification;
import com.fyp.n3015509.dao.SearchResult;
import com.fyp.n3015509.dao.enums.EditionTypes;
import com.fyp.n3015509.dao.enums.NotificationTypes;
import com.fyp.n3015509.dao.PriceChecker;
import com.fyp.n3015509.dao.enums.SearchResultType;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsShelf;

import org.apache.commons.lang.time.DateUtils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by n3015509 on 24/03/2017.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_BOOKS = "book";
    public static final String BOOK_INTERIM = "book_interim";
    public static final String TABLE_AUTHORS = "author";
    public static final String BUZZLIST_INTERIM = "buzzlist_interim";
    public static final String TABLE_BUZZLISTS = "buzzlist";
    public static final String BOOK_WATCH = "book_watched";

    public static final String COLUMN_ID = "_id";
    public static final String BOOK_ID = "book_id";
    public static final String AUTHOR_ID = "author_id";

    public static final String BUZZLIST_ID = "list_id";
    public static final String BUZZLIST_NAME = "list_name";
    public static final String BUZZLIST_BOOK_NUM = "book_num";

    public static final String GOODREADS_ID = "id";
    public static final String ISBN = "isbn";
    public static final String ISBN13 = "isbn13";
    public static final String REVIEW_COUNT = "text_reviews_count";
    public static final String TITLE = "title";
    public static final String TITLE_WITHOUT_SERIES = "title_without_series";
    public static final String IMAGE = "image";
    public static final String SMALL_IMAGE = "small_image";
    public static final String LARGE_IMAGE = "large_image";
    public static final String GOODREADS_LINK = "link";
    public static final String PAGE_NUM = "num_pages";
    public static final String PUBLISHSER = "publisher";
    public static final String YEAR = "year";
    public static final String AVG_RATING = "average_rating";
    public static final String RATINGS_COUNT = "ratings_count";
    public static final String DESCRIPTION = "description";
    private static final String RELEASE_DATE = "release_date";
    private static final String FORMAT = "format";
    private static final String EDITION = "edition";
    private static final String OWN = "owned";
    private static final String KINDLE = "kindle";
    private static final String PAPERBACK = "paperback";
    private static final String HARDCOVER = "hardcover";

    private static final String NOTIFIED = "notified";
    private static final String PREORDER = "preorder";
    private static final String READ = "read";

    //public static final String AUTHOR_ID = "id";
    public static final String AUTHOR_NAME = "name";
    public static final String AUTHOR_IMAGE = "image_url";
    public static final String AUTHOR_SMALL_IMAGE = "small_image_url";
    public static final String AUTHOR_LINK = "link";
    public static final String AUTHOR_AVG_RATING = "average_rating";
    public static final String AUTHOR_RATINGS_COUNT = "ratings_count";
    public static final String AUTHOR_TEXT_REVIEWS_COUNT = "text_reviews_count";

    private static final String NOTIFICATIONS_TABLE = "notifications";
    private static final String NOTIFICATION_TYPE = "type";
    private static final String EDITION_TYPE = "edition";
    private static final String MESSAGE = "message";
    public static final String PREORDER_STRING = "preorder";
    public static final String AVALIABLE_STRING = "available";

    private static final String DATABASE_NAME = "bookbuzzer.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String CREATE_AUTHOR_TABLE = "create table "
            + TABLE_AUTHORS + " ( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + AUTHOR_ID + " integer not null UNIQUE,"
            + AUTHOR_NAME + " varchar(255),"
            + AUTHOR_IMAGE + " blob,"
            + AUTHOR_SMALL_IMAGE + " blob,"
            + AUTHOR_LINK + " varchar(255),"
            + AUTHOR_AVG_RATING + " float,"
            + AUTHOR_RATINGS_COUNT + " integer,"
            + AUTHOR_TEXT_REVIEWS_COUNT + " integer);";

    private static final String CREATE_BOOK_TABLE = "create table "
            + TABLE_BOOKS + " ( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + GOODREADS_ID + " integer not null UNIQUE,"
            + ISBN + " varchar(255),"
            + ISBN13 + " varchar(255),"
            + REVIEW_COUNT + " integer not null,"
            + TITLE + " varchar(255),"
            + TITLE_WITHOUT_SERIES + " varchar(255),"
            + IMAGE + " blob,"
            + SMALL_IMAGE + " blob,"
            + LARGE_IMAGE + " blob,"
            + GOODREADS_LINK + " varchar(255),"
            + PAGE_NUM + " integer,"
            + YEAR + " float,"
            + AVG_RATING + " integer,"
            + PUBLISHSER + " varchar(255),"
            + RELEASE_DATE + " varchar(255),"
            + RATINGS_COUNT + " integer,"
            + DESCRIPTION + " varchar(255),"
            + FORMAT + " varchar(255),"
            + EDITION + " varchar(255),"
            + OWN + " integer,"
            + KINDLE + " float, "
            + PAPERBACK + " float, "
            + HARDCOVER + " float)"
            + ";";

    private static final String CREATE_BUZZLIST_TABLE = "create table "
            + TABLE_BUZZLISTS + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + BUZZLIST_NAME + " varchar(255) not null UNIQUE,"
            + BUZZLIST_BOOK_NUM + " integer);";

    private static final String CREATE_BUZZLIST_INTERIM_TABLE = "create table "
            + BUZZLIST_INTERIM + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + BOOK_ID + " integer not null,"
            + BUZZLIST_ID + " integer not null, "
            + "FOREIGN KEY (" + BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + ") ON DELETE CASCADE, "
            + "FOREIGN KEY (" + BUZZLIST_ID + ") REFERENCES " + TABLE_BUZZLISTS + "(" + COLUMN_ID + ") ON DELETE CASCADE"
            + ");";

    private static final String CREATE_BOOK_INTERIM_TABLE = "create table "
            + BOOK_INTERIM + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + BOOK_ID + " integer not null, "
            + AUTHOR_ID + " integer not null, "
            + "FOREIGN KEY (" + BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + ") ON DELETE CASCADE, "
            + "FOREIGN KEY (" + AUTHOR_ID + ") REFERENCES " + TABLE_AUTHORS + "(" + COLUMN_ID + ") ON DELETE CASCADE"
            + ");";

    private static final String CREATE_WATCH_TABLE = "create table "
            + BOOK_WATCH + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + BOOK_ID + " integer not null, "
            + NOTIFIED + " integer not null, "
            + PREORDER + " integer not null, "
            + "FOREIGN KEY (" + BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + ") ON DELETE CASCADE"
            + ");";

    private static final String CREATE_NOTIFICATIONS_TABLE = "create table "
            + NOTIFICATIONS_TABLE + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + BOOK_ID + " integer not null, "
            + NOTIFICATION_TYPE + " varchar(255), "
            + EDITION_TYPE + " varchar(255), "
            + MESSAGE + " varchar(255), "
            + READ + " integer not null,"
            + "FOREIGN KEY (" + BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + ") ON DELETE CASCADE"
            + ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            database.execSQL(CREATE_BOOK_TABLE);
            database.execSQL(CREATE_AUTHOR_TABLE);
            database.execSQL(CREATE_BUZZLIST_TABLE);
            database.execSQL(CREATE_BOOK_INTERIM_TABLE);
            database.execSQL(CREATE_BUZZLIST_INTERIM_TABLE);
            database.execSQL(CREATE_WATCH_TABLE);
            database.execSQL(CREATE_NOTIFICATIONS_TABLE);
            ContentValues insertValues = new ContentValues();
            insertValues.put(BOOK_ID, 1);
            insertValues.put(NOTIFICATION_TYPE, NotificationTypes.AVALIABLE.toString());
            insertValues.put(MESSAGE, "This book is available next week, preorder it now");
            insertValues.put(READ, 1);

            database.insertOrThrow(NOTIFICATIONS_TABLE, null, insertValues);
            ContentValues insert = new ContentValues();
            insert.put(BOOK_ID, 2);
            insert.put(NOTIFICATION_TYPE, NotificationTypes.AVALIABLE.toString());
            insert.put(MESSAGE, "This book is available");
            insert.put(READ, 0);
            database.insertOrThrow(NOTIFICATIONS_TABLE, null, insert);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.w(MySQLiteHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHORS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUZZLISTS);
            db.execSQL("DROP TABLE IF EXISTS " + BOOK_INTERIM);
            db.execSQL("DROP TABLE IF EXISTS " + BUZZLIST_INTERIM);
            db.execSQL("DROP TABLE IF EXISTS " + BOOK_WATCH);
            db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATIONS_TABLE);

            onCreate(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long insertAuthor(GoodreadsAuthor author) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String selectString = "SELECT " + COLUMN_ID + " FROM " + TABLE_AUTHORS + " WHERE " + AUTHOR_ID + " = " + author.getId() + ";";
            final Cursor cursor = db.rawQuery(selectString, null);
            long columnId = 0;
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        columnId = cursor.getInt(0);
                    }
                } finally {
                    cursor.close();
                }
            }

            if (columnId != 0) {
                return columnId;
            }

            ContentValues values = new ContentValues();
            values.put(AUTHOR_ID, author.getId());
            values.put(AUTHOR_AVG_RATING, author.getAverage_rating());
            values.put(AUTHOR_IMAGE, toByteArray(author.getImage()));
            values.put(AUTHOR_LINK, author.getLink());
            values.put(AUTHOR_RATINGS_COUNT, author.getRatingsCount());
            values.put(AUTHOR_SMALL_IMAGE, toByteArray(author.getSmallImage()));
            values.put(AUTHOR_TEXT_REVIEWS_COUNT, author.getTextReviewsCount());
            values.put(AUTHOR_NAME, author.getName());

            String where = AUTHOR_ID + "=" + author.getId() + "";
            // insert row
            long todo_id = db.update(TABLE_AUTHORS, values, where, null);
            if (todo_id == 0) {
                todo_id = db.insertWithOnConflict(TABLE_AUTHORS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            // insert row
            //long todo_id = db.insert(TABLE_AUTHORS, null, values);
            db.close();
            return todo_id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private byte[] toByteArray(Bitmap image) {
        if (image != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    public long insertBook(GoodreadsBook book) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String selectString = "SELECT " + COLUMN_ID + " FROM " + TABLE_BOOKS + " WHERE " + GOODREADS_ID + " = " + book.getId() + ";";
            final Cursor cursor = db.rawQuery(selectString, null);
            long columnId = 0;
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        columnId = cursor.getInt(0);
                    }
                } finally {
                    cursor.close();
                }
            }

            if (columnId != 0) {
                return columnId;
            }

            ContentValues values = new ContentValues();
            values.put(GOODREADS_ID, book.getId());
            values.put(ISBN, book.getIsbn());
            values.put(ISBN13, book.getIsbn13());
            values.put(REVIEW_COUNT, book.getTextReviewsCount());
            values.put(TITLE, book.getTitle());
            values.put(TITLE_WITHOUT_SERIES, book.getTitleWithoutSeries());
            values.put(IMAGE, toByteArray(book.getImage()));
            values.put(SMALL_IMAGE, toByteArray(book.getSmallImage()));
            values.put(LARGE_IMAGE, toByteArray(book.getLargeImage()));
            values.put(GOODREADS_LINK, book.getLink());
            values.put(PAGE_NUM, book.getNumPages());
            values.put(YEAR, book.getYearPublished());
            values.put(AVG_RATING, book.getAverage_rating());
            values.put(PUBLISHSER, book.getPublisher());
            values.put(RELEASE_DATE, book.getReleaseDate());
            values.put(RATINGS_COUNT, book.getRatingsCount());
            values.put(DESCRIPTION, book.getDescription());
            values.put(OWN, 0);

            // insert row

            String where = GOODREADS_ID + "=" + book.getId() + "";
            // insert row
            long todo_id = db.update(TABLE_BOOKS, values, where, null);
            if (todo_id == 0) {
                todo_id = db.insertWithOnConflict(TABLE_BOOKS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }

            // long todo_id = db.insert(TABLE_BOOKS, null, values);
            db.close();
            return todo_id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long insertBuzzlist(GoodreadsShelf buzz) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String selectString = "SELECT " + COLUMN_ID + " FROM " + TABLE_BUZZLISTS + " WHERE " + BUZZLIST_NAME + " = '" + buzz.getShelfName() + "';";
            final Cursor cursor = db.rawQuery(selectString, null);
            long columnId = 0;
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        columnId = cursor.getInt(0);
                    }
                } finally {
                    cursor.close();
                }
            }

            if (columnId != 0) {
                return columnId;
            }

            ContentValues values = new ContentValues();
            values.put(BUZZLIST_NAME, buzz.getShelfName());
            values.put(BUZZLIST_BOOK_NUM, buzz.getBookNum());
            String where = BUZZLIST_NAME + "='" + buzz.getShelfName() + "'";
            // insert row
            long todo_id = db.update(TABLE_BUZZLISTS, values, where, null);
            if (todo_id == 0) {
                todo_id = db.insertWithOnConflict(TABLE_BUZZLISTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }

            //long todo_id = db.up(TABLE_BUZZLISTS, null, values);

            return todo_id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long insertBuzzlistInterim(long buzzlistID, long bookId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String selectString = "SELECT " + COLUMN_ID + " FROM " + BUZZLIST_INTERIM + " WHERE " + BUZZLIST_ID + " = '" + buzzlistID + "' AND " + BOOK_ID + " = '" + bookId + "';";
            final Cursor cursor = db.rawQuery(selectString, null);
            long columnId = 0;
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        columnId = cursor.getInt(0);
                    }
                } finally {
                    cursor.close();
                }
            }

            if (columnId != 0) {
                return columnId;
            }

            //check that the interim hasnt already been inserted into
            ContentValues values = new ContentValues();
            values.put(BUZZLIST_ID, buzzlistID);
            values.put(BOOK_ID, bookId);

            // insert row
            long todo_id = db.insert(BUZZLIST_INTERIM, null, values);
            db.close();
            return todo_id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long insertBookInterim(long bookID, long authorId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(BOOK_ID, bookID);
            values.put(AUTHOR_ID, authorId);

            // insert row
            long todo_id = db.insert(BOOK_INTERIM, null, values);

            return todo_id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getBooksCount() {
        String countQuery = "SELECT * FROM " + TABLE_BOOKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    public ArrayList<Buzzlist> getBuzzlists() {
        try {
            String countQuery = "SELECT * FROM " + TABLE_BUZZLISTS;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);

            ArrayList<Buzzlist> list = new ArrayList<>();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {

                            Buzzlist newList = new Buzzlist();
                            newList.setId(cursor.getInt(0));
                            newList.setName(cursor.getString(1));
                            list.add(newList);
                            cursor.moveToNext();
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<GoodreadsBook> getBooksFromBuzzlist(int buzzlistId) {
        try {
            String countQuery = "SELECT * FROM " + TABLE_BOOKS + " INNER JOIN " + BUZZLIST_INTERIM + " ON " + TABLE_BOOKS
                    + "." + COLUMN_ID + " = " + BUZZLIST_INTERIM + "." + BOOK_ID + " WHERE " + BUZZLIST_INTERIM + "." + BUZZLIST_ID + "=" + buzzlistId;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            ArrayList<GoodreadsBook> book = new ArrayList<>();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        book = CreateGoodreadsBookArrayList(cursor, db);
                    }
                } finally {
                    cursor.close();
                }
            }

            return book;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public GoodreadsBook getBook(int id) {
        try {
            String countQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + GOODREADS_ID + "=" + id + " LIMIT 1;";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            GoodreadsBook b = new GoodreadsBook();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        b = CreateGoodreadsBookObject(cursor, db);
                    }
                } finally {
                    cursor.close();
                }
            }

            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public GoodreadsBook getBookByColumnId(int id) {
        try {
            String countQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + "=" + id + " LIMIT 1;";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            GoodreadsBook b = new GoodreadsBook();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        b = CreateGoodreadsBookObject(cursor, db);
                    }
                } finally {
                    cursor.close();
                }
            }

            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean removeBookFromBuzzlist(int bookId, int list) {
        int bookColumnId = 0;
        String columnIdQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + GOODREADS_ID + "=" + bookId + " LIMIT 1;";
        String deleteQuery = null;
        String checkQuery = null;
        boolean success = false;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(columnIdQuery, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        bookColumnId = cursor.getInt(0);
                        if (bookColumnId != 0) {
                            String whereClause = "_id=" + bookColumnId;
                            int delete = db.delete(BUZZLIST_INTERIM, whereClause, null);
                            if (delete == 1)
                                success = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public String getBuzzlistName(int listId) {
        String columnIdQuery = "SELECT " + BUZZLIST_NAME + " FROM " + TABLE_BUZZLISTS + " WHERE " + COLUMN_ID + "=" + listId + " LIMIT 1;";
        String name = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(columnIdQuery, null);
            try {
                if (cursor.moveToFirst()) {
                    name = cursor.getString(0);
                }
            } finally {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }

    public Boolean addBookToWatchList(int mBook) {
        boolean success = false;
        int bookColumnId = 0;
        String columnIdQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + GOODREADS_ID + "=" + mBook + " LIMIT 1;";

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(columnIdQuery, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        bookColumnId = cursor.getInt(0);

                        if (bookColumnId != 0) {
                            ContentValues insertValues = new ContentValues();
                            insertValues.put(BOOK_ID, bookColumnId);
                            insertValues.put(NOTIFIED, 0);
                            insertValues.put(PREORDER, 0);
                            long result = db.insert(BOOK_WATCH, null, insertValues);
                            if (result != 0)
                                success = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean RemoveBookFromWatchList(int mBookId) {
        boolean success = false;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String bookString = "SELECT " + COLUMN_ID + " FROM " + TABLE_BOOKS + " WHERE " + GOODREADS_ID + " = " + mBookId + " LIMIT 1";
            Cursor cursor = db.rawQuery(bookString, null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int id = cursor.getInt(0);
                        String whereClause = BOOK_ID + "=" + id;
                        int delete = db.delete(BOOK_WATCH, whereClause, null);
                        if (delete == 1) {
                            success = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
                return success;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public ArrayList<BuzzNotification> AddToWatchNotifications() {
        ArrayList<BuzzNotification> notification = new ArrayList<>();
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
            String currentDate = df.format(c.getTime());
            c.add(Calendar.DATE, 7);
            String preorderDate = df.format(c.getTime());

            String columnIdQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + RELEASE_DATE + " = '" + currentDate + "';";
            String preorderQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + RELEASE_DATE + " = '" + preorderDate + "';";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor avaialbleCursor = db.rawQuery(columnIdQuery, null);
            Cursor preorderCursor = db.rawQuery(preorderQuery, null);

            if (avaialbleCursor != null) {
                try {
                    //counts.add(avaialbleCursor.getCount());
                    if (avaialbleCursor.moveToFirst()) {
                        while (avaialbleCursor.isAfterLast() == false) {

                            BuzzNotification buzz = new BuzzNotification();
                            String message = avaialbleCursor.getString(5) + " is now avaliable to buy";
                            buzz.setBookId(avaialbleCursor.getInt(1));
                            buzz.setType(NotificationTypes.AVALIABLE);
                            buzz.setMessage(message);
                            buzz.setRead(false);
                            notification.add(buzz);

                            //add to notifications table
                            ContentValues avaliable = new ContentValues();
                            avaliable.put(BOOK_ID, avaialbleCursor.getInt(0));
                            avaliable.put(NOTIFICATION_TYPE, NotificationTypes.AVALIABLE.toString());
                            avaliable.put(MESSAGE, message);
                            avaliable.put(READ, 0);

                            db.insert(NOTIFICATIONS_TABLE, null, avaliable);
                            avaialbleCursor.moveToNext();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    avaialbleCursor.close();
                }
            }

            if (preorderCursor != null) {
                try {
                    // counts.add(preorderCursor.getCount());
                    if (preorderCursor.moveToFirst()) {
                        while (preorderCursor.isAfterLast() == false) {

                            BuzzNotification buzz = new BuzzNotification();
                            String message = avaialbleCursor.getString(5) + " is now avaliable to buy";
                            buzz.setBookId(avaialbleCursor.getInt(1));
                            buzz.setType(NotificationTypes.PREORDER);
                            buzz.setMessage(message);
                            buzz.setRead(false);
                            notification.add(buzz);

                            //add to notifications table
                            ContentValues avaliable = new ContentValues();
                            avaliable.put(BOOK_ID, preorderCursor.getInt(0));
                            avaliable.put(NOTIFICATION_TYPE, NotificationTypes.PREORDER.toString());
                            avaliable.put(MESSAGE, message);
                            avaliable.put(READ, 0);

                            db.insert(NOTIFICATIONS_TABLE, null, avaliable);
                            preorderCursor.moveToNext();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    preorderCursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notification;
    }

    public ArrayList<BuzzNotification> getNotifications() {
        String notificationQuery = "SELECT * FROM " + NOTIFICATIONS_TABLE + " LIMIT 50";
        ArrayList<BuzzNotification> notifications = new ArrayList<BuzzNotification>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(notificationQuery, null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {
                            int bookId = cursor.getInt(1);
                            String type = cursor.getString(2);
                            String message = cursor.getString(4);
                            int read = cursor.getInt(5);

                            NotificationTypes notification = NotificationTypes.valueOf(type);
                            String bookQuery = "SELECT " + GOODREADS_ID + ", " + TITLE + ", " + SMALL_IMAGE + " FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = " + bookId + " LIMIT 1";
                            String notQuery = "";
                            Cursor c1 = db.rawQuery(bookQuery, null);

                            Bitmap image = null;
                            String bookName = "";
                            int goodreadsId = 0;
                            if (c1 != null) {
                                try {
                                    if (c1.moveToFirst()) {
                                        goodreadsId = c1.getInt(0);
                                        bookName = c1.getString(1);
                                        byte[] by = c1.getBlob(2);
                                        image = BitmapFactory.decodeByteArray(by, 0, by.length);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    c1.close();
                                }
                            }

                            BuzzNotification buzz = createBuzzNotification(bookId, notification, bookName, 0, image, read, message, goodreadsId);
                            notifications.add(buzz);
                            cursor.moveToNext();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public Boolean MarkAsRead(int mBook, NotificationTypes mType) {
        Boolean success = false;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues bookT = new ContentValues();
            ContentValues cv = new ContentValues();
            cv.put(READ, 1);

            switch (mType) {
                case AVALIABLE:
                    bookT.put(NOTIFIED, 1);
                    break;
                case PREORDER:
                    bookT.put(PREORDER, 1);
                    break;
            }
            db.update(BOOK_WATCH, bookT, BOOK_ID + "=" + mBook, null);
            String where = BOOK_ID + "=" + mBook + " and " + NOTIFICATION_TYPE + "= '" + mType.toString() + "'";
            db.update(NOTIFICATIONS_TABLE, cv, where, null);
            success = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public ArrayList<GoodreadsBook> getWatchedBooks() {
        try {
            String watchQuery = "SELECT " + BOOK_ID + " FROM " + BOOK_WATCH;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(watchQuery, null);
            ArrayList<GoodreadsBook> book = new ArrayList<>();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        ArrayList<GoodreadsBook> booklist = new ArrayList<>();
                        while (cursor.isAfterLast() == false) {
                            String bookQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + "=" + cursor.getInt(0);
                            Cursor c1 = db.rawQuery(bookQuery, null);
                            if (c1 != null) {
                                try {
                                    if (c1.moveToFirst()) {
                                        book.add(CreateGoodreadsBookObject(c1, db));
                                    }
                                } finally {
                                    c1.close();
                                }
                            }
                            cursor.moveToNext();
                        }
                    }
                } finally {
                    cursor.close();
                }
            }

            return book;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean checkIfWatched(int mBookId) {
        Boolean watched = false;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String bookString = "SELECT " + COLUMN_ID + " FROM " + TABLE_BOOKS + " WHERE " + GOODREADS_ID + " = " + mBookId + " LIMIT 1";
            Cursor cursor = db.rawQuery(bookString, null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int id = cursor.getInt(0);
                        String watchString = "SELECT " + COLUMN_ID + " FROM " + BOOK_WATCH + " WHERE " + BOOK_ID + " = " + id + " LIMIT 1";
                        Cursor c1 = db.rawQuery(watchString, null);

                        if (c1 != null) {
                            try {
                                if (c1.moveToFirst()) {
                                    int count = c1.getCount();
                                    if (count != 0) {
                                        watched = true;
                                    }
                                }
                            } finally {
                                c1.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return watched;

    }


    public String[] GetWatchedISBNs() {
        ArrayList<String> isbns = new ArrayList<>();
        String columnIdQuery = "SELECT " + BOOK_ID + " FROM " + BOOK_WATCH;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(columnIdQuery, null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {
                            int book_id = cursor.getInt(0);
                            String isbnQuery = "SELECT " + ISBN + " FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = " + book_id;
                            Cursor c1 = db.rawQuery(isbnQuery, null);
                            if (c1 != null) {
                                try {
                                    if (c1.moveToFirst()) {
                                        String isbn = c1.getString(0);
                                        isbns.add(isbn);
                                    }
                                } finally {
                                    c1.close();
                                }
                            }
                            cursor.moveToNext();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] isbnArray = new String[isbns.size()];
        return isbns.toArray(isbnArray);
    }

    public ArrayList<PriceChecker> CheckDBForPreviousPrices(ArrayList<PriceChecker> priceCheckValues, String isbn) {
        GoodreadsBook b = getBookByIsbn(isbn);
        ArrayList<PriceChecker> cheaperOptions = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String watchQuery = "SELECT " + KINDLE + "," + PAPERBACK + "," + HARDCOVER + " FROM " + TABLE_BOOKS + " WHERE " + ISBN + " = '" + isbn + "';";
            Cursor c1 = db.rawQuery(watchQuery, null);

            if (c1 != null) {
                try {
                    if (c1.moveToFirst()) {
                        double kindlePrice = c1.getDouble(0);
                        double paperbackPrice = c1.getDouble(1);
                        double hardbackPrice = c1.getDouble(2);

                        for (PriceChecker priceChecker : priceCheckValues) {
                            EditionTypes type = priceChecker.getType();
                            Double price = priceChecker.getPrice();
                            double tablePrice = 0;
                            ContentValues val = new ContentValues();

                            switch (type) {
                                case KINDLE_EDITION:
                                    tablePrice = kindlePrice;
                                    val.put(KINDLE, price);
                                    break;
                                case PAPERBACK:
                                    val.put(PAPERBACK, price);
                                    tablePrice = paperbackPrice;
                                    break;
                                case HARDBACK:
                                    val.put(HARDCOVER, price);
                                    tablePrice = hardbackPrice;
                                    break;
                            }
                            String whereClause = COLUMN_ID + "=" + b.getColumnId();
                            db.update(TABLE_BOOKS, val, whereClause, null);

                            if (price < tablePrice || (tablePrice == 0.0 && price >= 0)) {
                                priceChecker.setName(b.getTitle());
                                cheaperOptions.add(priceChecker);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    c1.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cheaperOptions;
    }

    public ArrayList<BuzzNotification> AddPriceCheckerNotification(ConcurrentHashMap<String, ArrayList<PriceChecker>> isbn) {
        ArrayList<BuzzNotification> buzzNotifications = new ArrayList();
        for (Map.Entry<String, ArrayList<PriceChecker>> entry : isbn.entrySet()) {
            String key = entry.getKey();
            ArrayList<PriceChecker> value = entry.getValue();
            GoodreadsBook b = getBookByIsbn(key);
            try {
                StringBuilder formats = new StringBuilder();
                double lowestPrice = 0;

                for (PriceChecker p : value) {
                    if (formats.length() != 0) {
                        formats.append(",");
                    }
                    formats.append(p.getType().toString().toLowerCase());

                    if (p.getPrice() == 0 && lowestPrice == 0.0)
                        lowestPrice = p.getPrice();
                    else if (p.getPrice() < lowestPrice && lowestPrice != 0.0)
                        lowestPrice = p.getPrice();
                }
                SQLiteDatabase db = this.getReadableDatabase();
                String message = b.getTitle() + " is cheaper today from £" + lowestPrice + " in the following formats: " + formats;

                ContentValues avaliable = new ContentValues();
                BuzzNotification buzz = new BuzzNotification();
                buzz.setType(NotificationTypes.CHEAPER);
                buzz.setBookId(b.getId());
                buzz.setMessage(message);

                avaliable.put(BOOK_ID, b.getColumnId());
                avaliable.put(NOTIFICATION_TYPE, NotificationTypes.CHEAPER.toString());
                avaliable.put(MESSAGE, message);
                avaliable.put(READ, 0);
                long id = db.insert(NOTIFICATIONS_TABLE, null, avaliable);
                buzzNotifications.add(buzz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buzzNotifications;
    }


    private GoodreadsBook createGoodreadsBook(Cursor cursor) {
        GoodreadsBook b = new GoodreadsBook();
        b.setColumnId(cursor.getInt(0));
        b.setId(cursor.getInt(1));
        b.setIsbn(cursor.getString(2));
        b.setIsbn13(cursor.getString(3));
        b.setTextReviewsCount(cursor.getInt(4));
        b.setTitle(cursor.getString(5));
        b.setTitleWithoutSeries(cursor.getString(6));
        b.setImage(cursor.getBlob(7));
        b.setSmallImage(cursor.getBlob(8));
        b.setLargeImage(cursor.getBlob(9));
        b.setLink(cursor.getString(10));
        b.setNumPages(cursor.getInt(11));
        b.setYearPublished(cursor.getInt(12));
        b.setAverage_rating(cursor.getDouble(13));
        b.setPublisher(cursor.getString(14));
        b.setReleaseDate(cursor.getString(15));
        b.setRatingsCount(cursor.getInt(16));
        b.setDescription(cursor.getString(17));
        b.setFormat(cursor.getString(18));
        b.setEditionInformation(cursor.getString(19));
        b.setKindlePrice(cursor.getDouble(21));
        b.setPaperbackPrice(cursor.getDouble(22));
        b.setHardcoverPrice(cursor.getDouble(23));

        return b;
    }

    private GoodreadsAuthor createAuthor(Cursor c) {
        GoodreadsAuthor b = new GoodreadsAuthor();
        b.setColumnId(c.getInt(0));
        b.setId(c.getInt(1));
        b.setName(c.getString(2));
        b.setImageDB(c.getBlob(3));
        b.setSmallImageDB(c.getBlob(4));
        b.setLink(c.getString(5));
        b.setAverage_rating(c.getDouble(6));
        b.setRatingsCount(c.getInt(7));
        b.setTextReviewsCount(c.getInt(8));
        return b;
    }

    private BuzzNotification createBuzzNotification(int bookId, NotificationTypes
            notification, String bookName, int notified, Bitmap image, int read, String message, int goodreadsId) {
        BuzzNotification not = new BuzzNotification();
        not.setBookId(bookId);
        not.setType(notification);
        not.setImage(image);
        not.setBookName(bookName);
        not.setGoodreadsId(goodreadsId);
        if (notified == 0)
            not.setNotified(false);
        else
            not.setNotified(true);

        if (read == 0)
            not.setRead(false);
        else
            not.setRead(true);

        not.setMessage(message);

        return not;
    }

    private ArrayList<GoodreadsBook> CreateGoodreadsBookArrayList(Cursor cursor, SQLiteDatabase db) {
        ArrayList<GoodreadsBook> book = new ArrayList<>();
        while (cursor.isAfterLast() == false) {
            book.add(CreateGoodreadsBookObject(cursor, db));
            cursor.moveToNext();
        }
        cursor.close();
        return book;
    }

    private GoodreadsBook CreateGoodreadsBookObject(Cursor c1, SQLiteDatabase db) {
        GoodreadsBook b = createGoodreadsBook(c1);
        String authorQuery = "SELECT * FROM " + TABLE_AUTHORS + " INNER JOIN " + BOOK_INTERIM + " ON " + TABLE_AUTHORS
                + "." + COLUMN_ID + " = " + BOOK_INTERIM + "." + AUTHOR_ID + " WHERE " + BOOK_INTERIM + "." + BOOK_ID + "=" + b.getColumnId();
        Cursor cursor = db.rawQuery(authorQuery, null);
        ArrayList<GoodreadsAuthor> authorList = new ArrayList<>();
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    while (cursor.isAfterLast() == false) {
                        authorList.add(createAuthor(cursor));
                        cursor.moveToNext();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        b.setAuthors(authorList);
        return b;
    }

    private String getSingleString(Cursor cursor) {
        String var = "";
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    var = cursor.getString(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return var;
    }

    private int getSingleInt(Cursor cursor) {
        int var = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    var = cursor.getInt(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return 0;
    }

    private GoodreadsBook getBookByIsbn(String key) {
        try {
            String countQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + ISBN + "='" + key + "' LIMIT 1;";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            GoodreadsBook b = new GoodreadsBook();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        b = CreateGoodreadsBookObject(cursor, db);
                    }
                } finally {
                    cursor.close();
                }
            }

            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getIsbnFromId(int goodreadsId) {
        String isbn = null;
        try {
            String countQuery = "SELECT " + ISBN + " FROM " + TABLE_BOOKS + " WHERE " + GOODREADS_ID + "=" + goodreadsId + " LIMIT 1;";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            GoodreadsBook b = new GoodreadsBook();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        isbn = cursor.getString(0);
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isbn;
    }

    public Boolean createBuzzlistAndBook(GoodreadsBook mBook, String listName) {
        long bookId = insertBook(mBook);
        GoodreadsShelf shelf = new GoodreadsShelf();
        shelf.setShelfName(listName);
        long buzzlistId = insertBuzzlist(shelf);
        long success = insertBuzzlistInterim(buzzlistId, bookId);

        if (success != 0 || success != -1) return true;
        else return false;
    }

    public Boolean addBookToBuzzlist(GoodreadsBook mBook, String listName) {
        int buzzlistId = getBuzzlistIdByName(listName);
        if (buzzlistId != 0 || buzzlistId != -1) {
            long bookId = insertBook(mBook);
            long success = insertBookInterim(buzzlistId, bookId);

            if (success != 0 || success != -1) return true;
            else return false;
        }
        return false;
    }

    private int getBuzzlistIdByName(String listName) {
        String columnIdQuery = "SELECT " + COLUMN_ID + " FROM " + TABLE_BUZZLISTS + " WHERE " + BUZZLIST_NAME + "='" + listName + "' LIMIT 1;";
        int id = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(columnIdQuery, null);
            try {
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    public ArrayList<GoodreadsAuthor> getAuthors() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String authorQuery = "SELECT * FROM " + TABLE_AUTHORS;
            Cursor cursor = db.rawQuery(authorQuery, null);
            ArrayList<GoodreadsAuthor> authorList = new ArrayList<>();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {
                            authorList.add(createAuthor(cursor));
                            cursor.moveToNext();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
            return authorList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public GoodreadsAuthor getAuthorById(int authorId) {
        try {
            String countQuery = "SELECT * FROM " + TABLE_AUTHORS + " WHERE " + COLUMN_ID + "=" + authorId + ";";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            GoodreadsAuthor b = new GoodreadsAuthor();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        b = createAuthor(cursor);
                    }
                } finally {
                    cursor.close();
                }
            }

            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<GoodreadsBook> getBooksByAuthor(int authorId) {
        try {
            String countQuery = "SELECT * FROM " + TABLE_BOOKS + " INNER JOIN " + BOOK_INTERIM + " ON " + TABLE_BOOKS
                    + "." + COLUMN_ID + " = " + BOOK_INTERIM + "." + BOOK_ID + " WHERE " + BOOK_INTERIM + "." + AUTHOR_ID + "=" + authorId;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            ArrayList<GoodreadsBook> book = new ArrayList<>();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        book = CreateGoodreadsBookArrayList(cursor, db);
                    }
                } finally {
                    cursor.close();
                }
            }

            return book;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUnopenedNotifications() {
        String countQuery = "SELECT * FROM " + NOTIFICATIONS_TABLE + " WHERE " + READ + " = 0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }

    public Boolean CreateEmptyBuzz(String name) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(BUZZLIST_NAME, name);

            long todo_id = db.insert(TABLE_BUZZLISTS, null, values);
            if (todo_id != -1) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean CheckBuzzlist(String name) {
        try {
            String countQuery = "SELECT " + COLUMN_ID + " FROM " + TABLE_BUZZLISTS + " WHERE " + BUZZLIST_NAME + "='" + name + "';";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int columnId = 0;
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        columnId = cursor.getInt(0);
                    }
                } finally {
                    cursor.close();
                }
            }

            if (columnId != 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean ModifyBuzzlist(String name, String newName) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(BUZZLIST_NAME, newName);
            String where = BUZZLIST_NAME + "='" + name + "'";
            // insert row
            long todo_id = db.update(TABLE_BUZZLISTS, values, where, null);

            //long todo_id = db.up(TABLE_BUZZLISTS, null, values);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<SearchResult> SearchForBookOrAuthor(String query) {
        ArrayList<SearchResult> results = new ArrayList<>();

        String authorQuery = "SELECT * FROM "+ TABLE_AUTHORS + " WHERE " + AUTHOR_NAME + " LIKE  '" + query + "'";
        String bookQuery = "SELECT * FROM "+ TABLE_BOOKS + " WHERE " + TITLE_WITHOUT_SERIES + " LIKE  '" + query + "'";

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c1 = db.rawQuery(authorQuery, null);
            Cursor c2 = db.rawQuery(bookQuery, null);

            if (c1 != null) {
                try {
                    if (c1.moveToFirst()) {
                        while (c1.isAfterLast() == false) {
                            SearchResult res = new SearchResult();
                            res.setGoodreadsId(c1.getInt(1));
                            res.setType(SearchResultType.LOCAL);
                            res.setAuthorName(c1.getString(2));
                            results.add(res);
                            c1.moveToNext();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    c1.close();
                }
            }

            if (c2 != null) {
                try {
                    if (c2.moveToFirst()) {
                        while (c2.isAfterLast() == false) {
                            SearchResult res = new SearchResult();
                            res.setGoodreadsId(c2.getInt(1));
                            GoodreadsBook b = getBook(c2.getInt(1));
                            res.setBookName(b.getTitle());
                            StringBuilder s = new StringBuilder();
                            for(GoodreadsAuthor a : b.getAuthors())
                            {
                                if (s.length() != 0)
                                {
                                    s.append(", ");
                                }
                                s.append(a.getName());
                            }
                            res.setAuthorName(s.toString());
                            res.setType(SearchResultType.LOCAL);
                            results.add(res);
                            c2.moveToNext();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    c2.close();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    public boolean addBooksToWatchList(ArrayList<Integer> watch) {
        for (int bookId : watch) {
            boolean success = false;
            int bookColumnId = 0;
            String columnIdQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + GOODREADS_ID + "=" + bookId + " LIMIT 1;";

            try {
                SQLiteDatabase db = this.getReadableDatabase();
                Cursor cursor = db.rawQuery(columnIdQuery, null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            bookColumnId = cursor.getInt(0);

                            if (bookColumnId != 0) {
                                ContentValues insertValues = new ContentValues();
                                insertValues.put(BOOK_ID, bookColumnId);
                                insertValues.put(NOTIFIED, 0);
                                insertValues.put(PREORDER, 0);
                                long result = db.insert(BOOK_WATCH, null, insertValues);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        cursor.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean addNotifications(ArrayList<BuzzNotification> notificationList) {
        for (BuzzNotification b : notificationList) {
            String columnIdQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + GOODREADS_ID + " = '" + b.getGoodreadsId() + "';";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(columnIdQuery, null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        ContentValues notification = new ContentValues();
                        notification.put(BOOK_ID, cursor.getInt(0));
                        notification.put(NOTIFICATION_TYPE, b.getType().toString());
                        notification.put(MESSAGE, b.getMessage());
                        if (b.getRead() == true) {
                            notification.put(READ, 1);
                        } else {
                            notification.put(READ, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        }
        return true;
    }
}

