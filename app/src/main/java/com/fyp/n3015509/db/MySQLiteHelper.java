package com.fyp.n3015509.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fyp.n3015509.db.dao.Author;

/**
 * Created by n3015509 on 24/03/2017.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_BOOKS = "book";
    public static final String TABLE_AUTHORS = "author";

    public static final String COLUMN_ID = "_id";

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
    public static final String PUBLIsHSER = "publisher";
    public static final String YEAR = "year";
    public static final String AVG_RATING = "average_rating";
    public static final String RATINGS_COUNT = "ratings_count";
    public static final String DESCRIPTION = "description";

    public static final String AUTHOR_ID = "id";
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
    private static final String DATABASE_CREATE = "create table "
            + TABLE_BOOKS + "( " + COLUMN_ID
            + " integer primary key autoincrement, " + GOODREADS_ID
            + " integer not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHORS);
        onCreate(db);
    }

    public long createAuthor(Author author, long[] tag_ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AUTHOR_ID, author.getId());
        values.put(AUTHOR_AVG_RATING, author.getAverage_rating());
        values.put(AUTHOR_IMAGE, author.getImage());
        values.put(AUTHOR_LINK, author.getLink());
        values.put(AUTHOR_RATINGS_COUNT, author.getRatings_count());
        values.put(AUTHOR_SMALL_IMAGE, author.getSmall_image());
        values.put(AUTHOR_TEXT_REVIEWS_COUNT,author.getText_reviews_count());
        values.put(AUTHOR_NAME, author.getName());

        // insert row
        long todo_id = db.insert(TABLE_AUTHORS, null, values);

        return todo_id;
    }


}
