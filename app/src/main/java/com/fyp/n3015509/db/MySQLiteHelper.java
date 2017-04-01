package com.fyp.n3015509.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fyp.n3015509.db.dao.Author;
import com.fyp.n3015509.db.dao.Book;
import com.fyp.n3015509.db.dao.Buzzlist;
import com.fyp.n3015509.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.goodreadsDAO.GoodreadsShelf;

import java.util.ArrayList;

import static android.R.attr.author;

/**
 * Created by n3015509 on 24/03/2017.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_BOOKS = "book";
    public static final String BOOK_INTERIM = "book_interim";
    public static final String TABLE_AUTHORS = "author";
    public static final String BUZZLIST_INTERIM = "buzzlist_interim";
    public static final String TABLE_BUZZLISTS = "buzzlist";

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

    //public static final String AUTHOR_ID = "id";
    public static final String AUTHOR_NAME = "name";
    public static final String AUTHOR_IMAGE = "image_url";
    public static final String AUTHOR_SMALL_IMAGE = "small_image_url";
    public static final String AUTHOR_LINK = "link";
    public static final String AUTHOR_AVG_RATING = "average_rating";
    public static final String AUTHOR_RATINGS_COUNT = "ratings_count";
    public static final String AUTHOR_TEXT_REVIEWS_COUNT = "text_reviews_count";

    private static final String DATABASE_NAME = "bookbuzzer.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String CREATE_AUTHOR_TABLE = "create table "
            + TABLE_BOOKS + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + AUTHOR_ID + " integer not null,"
            + AUTHOR_NAME + " varchar(255),"
            + AUTHOR_IMAGE + " varchar(255),"
            + AUTHOR_SMALL_IMAGE + " varchar(255),"
            + AUTHOR_LINK + " varchar(255),"
            + AUTHOR_AVG_RATING + " float,"
            + AUTHOR_RATINGS_COUNT + " integer,"
            + AUTHOR_TEXT_REVIEWS_COUNT + " integer);";

    private static final String CREATE_BOOK_TABLE = "create table "
            + TABLE_AUTHORS + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + GOODREADS_ID + " integer not null,"
            + ISBN + " varchar(255),"
            + ISBN13 + " varchar(255),"
            + REVIEW_COUNT + " integer not null,"
            + TITLE + " varchar(255),"
            + TITLE_WITHOUT_SERIES + " varchar(255),"
            + IMAGE + " varchar(255),"
            + SMALL_IMAGE + " varchar(255),"
            + LARGE_IMAGE + " varchar(255),"
            + GOODREADS_LINK + " varchar(255),"
            + PAGE_NUM + " integer,"
            + YEAR + " float,"
            + AVG_RATING + " integer,"
            + PUBLISHSER + " varchar(255),"
            + RATINGS_COUNT + " integer,"
            + DESCRIPTION + " varchar(255));";

    private static final String CREATE_BUZZLIST_TABLE = "create table "
            + TABLE_BUZZLISTS + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + BUZZLIST_NAME + " varchar(255) not null,"
            + BUZZLIST_BOOK_NUM + "integer);";

    private static final String CREATE_BUZZLIST_INTERIM_TABLE = "create table "
            + BUZZLIST_INTERIM + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + BOOK_ID + " integer not null,"
            + BUZZLIST_ID + " integer not null);";

    private static final String CREATE_BOOK_INTERIM_TABLE = "create table "
            + BOOK_INTERIM + "( " + COLUMN_ID
            + " integer primary key autoincrement, " + BOOK_ID
            + " integer not null, "+ AUTHOR_ID
            + " integer not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_BOOK_TABLE);
        database.execSQL(CREATE_AUTHOR_TABLE);
        database.execSQL(CREATE_BUZZLIST_TABLE);
        database.execSQL(CREATE_BOOK_INTERIM_TABLE);
        database.execSQL(CREATE_BUZZLIST_INTERIM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUZZLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + BOOK_INTERIM);
        db.execSQL("DROP TABLE IF EXISTS " + BUZZLIST_INTERIM);
        onCreate(db);
    }

    public long insertAuthor(GoodreadsAuthor author) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AUTHOR_ID, author.getId());
        values.put(AUTHOR_AVG_RATING, author.getAverage_rating());
        values.put(AUTHOR_IMAGE, author.getImage_url());
        values.put(AUTHOR_LINK, author.getLink());
        values.put(AUTHOR_RATINGS_COUNT, author.getRatings_count());
        values.put(AUTHOR_SMALL_IMAGE, author.getSmall_image_url());
        values.put(AUTHOR_TEXT_REVIEWS_COUNT,author.getText_reviews_count());
        values.put(AUTHOR_NAME, author.getName());

        // insert row
        long todo_id = db.insert(TABLE_AUTHORS, null, values);
        db.close();
        return todo_id;
    }

    public long insertBook(GoodreadsBook book) {
        try{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GOODREADS_ID, book.getId());
        values.put(ISBN, book.getIsbn());
        values.put(ISBN13, book.getIsbn13());
        values.put(REVIEW_COUNT, book.getText_reviews_count());
        values.put(TITLE, book.getTitle());
        values.put(TITLE_WITHOUT_SERIES, book.getTitle_without_series());
        values.put(IMAGE,book.getImage_url());
        values.put(SMALL_IMAGE, book.getSmall_image_url());
        values.put(LARGE_IMAGE,book.getLarge_image_url());
        values.put(GOODREADS_LINK, book.getLink());
        values.put(PAGE_NUM,book.getNum_pages());
        values.put(YEAR, book.getYearPublished());
        values.put(AVG_RATING,book.getAverage_rating());
        values.put(PUBLISHSER, book.getPublisher());
        values.put(RATINGS_COUNT,book.getRatings_count());
        values.put(DESCRIPTION, book.getDescription());

        // insert row
        long todo_id = db.insert(TABLE_BOOKS, null, values);
            db.close();
            return todo_id;}
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public long insertBouzzlist(GoodreadsShelf buzz) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BUZZLIST_NAME, buzz.getShelfName());
        values.put(BUZZLIST_BOOK_NUM, buzz.getBookNum());

        // insert row
        long todo_id = db.insert(TABLE_BUZZLISTS, null, values);

        return todo_id;
    }

    public long insertBouzzlistInterim(long buzzlistID, long bookId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BUZZLIST_ID, buzzlistID);
        values.put(BOOK_ID, bookId);

        // insert row
        long todo_id = db.insert(BUZZLIST_INTERIM, null, values);
        db.close();
        return todo_id;
    }

    public long insertBookInterim(long bookID, long authorId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BOOK_ID, bookID);
        values.put(AUTHOR_ID, authorId);

        // insert row
        long todo_id = db.insert(BOOK_INTERIM, null, values);

        return todo_id;
    }

    public int getBooksCount() {
        String countQuery = "SELECT * FROM " + TABLE_BOOKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }


}
