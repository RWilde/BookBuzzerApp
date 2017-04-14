package com.fyp.n3015509.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.fyp.n3015509.apppreferences.SaveSharedPreference;
import com.fyp.n3015509.dao.BuzzNotification;
import com.fyp.n3015509.dao.NotificationTypes;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.fyp.n3015509.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.goodreadsDAO.GoodreadsShelf;
import com.google.api.client.util.DateTime;

import org.apache.commons.lang.time.DateUtils;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    private static final String NOTIFIED = "notified";
    private static final String PREORDER = "preorder";

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
    public static final String PREORDER_STRING = "preorder";
    public static final String AVALIABLE_STRING = "available";

    private static final String DATABASE_NAME = "bookbuzzer.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String CREATE_AUTHOR_TABLE = "create table "
            + TABLE_AUTHORS + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + AUTHOR_ID + " integer not null,"
            + AUTHOR_NAME + " varchar(255),"
            + AUTHOR_IMAGE + " blob,"
            + AUTHOR_SMALL_IMAGE + " blob,"
            + AUTHOR_LINK + " varchar(255),"
            + AUTHOR_AVG_RATING + " float,"
            + AUTHOR_RATINGS_COUNT + " integer,"
            + AUTHOR_TEXT_REVIEWS_COUNT + " integer);";

    private static final String CREATE_BOOK_TABLE = "create table "
            + TABLE_BOOKS + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + GOODREADS_ID + " integer not null,"
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
            + OWN + " integer)"
            + ";";

    private static final String CREATE_BUZZLIST_TABLE = "create table "
            + TABLE_BUZZLISTS + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + BUZZLIST_NAME + " varchar(255) not null,"
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
            + NOTIFICATION_TYPE + " varchar(255) not null, "
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
            database.insertOrThrow(NOTIFICATIONS_TABLE, null, insertValues);
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

    public long insertAuthor(GoodreadsAuthor author, Context context) {
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

            // insert row
            long todo_id = db.insert(TABLE_AUTHORS, null, values);
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
            long todo_id = db.insert(TABLE_BOOKS, null, values);
            db.close();
            return todo_id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long insertBouzzlist(GoodreadsShelf buzz) {
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

            // insert row
            long todo_id = db.insert(TABLE_BUZZLISTS, null, values);

            return todo_id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long insertBouzzlistInterim(long buzzlistID, long bookId) {
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

            cursor.close();
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
                        ArrayList<GoodreadsBook> booklist = new ArrayList<>();
                        while (cursor.isAfterLast() == false) {
                            GoodreadsBook b = createGoodreadsBook(cursor);
                            String authorQuery = "SELECT * FROM " + TABLE_AUTHORS + " INNER JOIN " + BOOK_INTERIM + " ON " + TABLE_AUTHORS
                                    + "." + COLUMN_ID + " = " + BOOK_INTERIM + "." + AUTHOR_ID + " WHERE " + BOOK_INTERIM + "." + BOOK_ID + "=" + b.getColumnId();
                            Cursor c = db.rawQuery(authorQuery, null);
                            ArrayList<GoodreadsAuthor> authorList = new ArrayList<>();

                            if (c.moveToFirst()) {
                                while (c.isAfterLast() == false) {
                                    authorList.add(createAuthor(c));
                                    c.moveToNext();
                                }
                            }
                            b.setAuthors(authorList);
                            book.add(b);
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

    public GoodreadsBook getBook(int id) {
        try {
            String countQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + GOODREADS_ID + "=" + id + " LIMIT 1;";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            GoodreadsBook book = new GoodreadsBook();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        book = createGoodreadsBook(cursor);
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

    public Integer[] addToNotifications() {
        Integer[] count = new Integer[2];
        ArrayList<Integer> counts = new ArrayList<>();
        java.sql.Date currentDate = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
        Date preorderDate = DateUtils.addDays(new Date(), 7);
        String columnIdQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + RELEASE_DATE + " = " + currentDate + ";";
        String preorderQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + RELEASE_DATE + " = " + preorderDate + ";";

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor avaialbleCursor = db.rawQuery(columnIdQuery, null);
            Cursor preorderCursor = db.rawQuery(columnIdQuery, null);

            if (avaialbleCursor != null) {
                try {
                    counts.add(avaialbleCursor.getCount());
                    if (avaialbleCursor.moveToFirst()) {
                        while (avaialbleCursor.isAfterLast() == false) {
                            //add to notifications table
                            ContentValues avaliable = new ContentValues();
                            avaliable.put(BOOK_ID, avaialbleCursor.getInt(0));
                            avaliable.put(NOTIFICATION_TYPE, NotificationTypes.AVALIABLE.toString());
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
                    counts.add(preorderCursor.getCount());
                    if (preorderCursor.moveToFirst()) {
                        while (preorderCursor.isAfterLast() == false) {

                            //add to notifications table
                            ContentValues avaliable = new ContentValues();
                            avaliable.put(BOOK_ID, preorderCursor.getInt(0));
                            avaliable.put(NOTIFICATION_TYPE, NotificationTypes.PREORDER.toString());
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

        return counts.toArray(count);
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
                            NotificationTypes notification = NotificationTypes.valueOf(type);
                            String bookQuery = "SELECT " + TITLE + " FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + " = " + bookId + " LIMIT 1";
                            String notQuery = "";
                            Cursor c1 = db.rawQuery(bookQuery, null);

                            switch (notification) {
                                case AVALIABLE:
                                    notQuery = "SELECT " + NOTIFIED + " FROM " + BOOK_WATCH + " WHERE " + BOOK_ID + " = " + bookId + " LIMIT 1";
                                    break;
                                case PREORDER:
                                    notQuery = "SELECT " + PREORDER + " FROM " + BOOK_WATCH + " WHERE " + BOOK_ID + " = " + bookId + " LIMIT 1";
                                    break;
                            }
                            Cursor c2 = db.rawQuery(notQuery, null);

                            String bookName = getSingleString(c1);
                            int notified = getSingleInt(c2);

                            BuzzNotification buzz = createBuzzNotification(bookId, notification, bookName, notified);
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

    private BuzzNotification createBuzzNotification(int bookId, NotificationTypes notification, String bookName, int notified) {
        BuzzNotification not = new BuzzNotification();
        not.setBookId(bookId);
        not.setType(notification);
        not.setBookName(bookName);
        if (notified == 0)
            not.setNotified(false);
        else
            not.setNotified(true);

        return not;
    }

    private String getSingleString(Cursor cursor)
    {
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

    private int getSingleInt(Cursor cursor)
    {
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

}

//                        if (cursor.moveToFirst()) {
//        String releaseDate = cursor.getString(0);
//
//        DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
//        Date date = sourceFormat.parse(releaseDate);
//
//        Date currentDate = new Date(System.currentTimeMillis());
//
//        if (lastActive != null) {
//            if (date == currentDate || date.after(lastActive) && date.before(currentDate)) {
//                //add to notifications db
//                ContentValues notification = new ContentValues();
//
//                db.insert(BOOK_WATCH, null, notification);
//            }
//        } else {
//            if (date == currentDate) {
//                //add to notifications db
//            }
//        }
//    }
