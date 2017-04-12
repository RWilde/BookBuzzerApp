package com.fyp.n3015509.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.fyp.n3015509.db.dao.Buzzlist;
import com.fyp.n3015509.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.goodreadsDAO.GoodreadsShelf;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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
    private static final String RELEASE_DATE = "release_date";
    private static final String FORMAT = "format";
    private static final String EDITION = "edition";

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
            + EDITION + " varchar(255));";

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
        }
        catch (Exception e)
        {
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
            values.put(RATINGS_COUNT, book.getRatingsCount());
            values.put(DESCRIPTION, book.getDescription());

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
            String countQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE " + COLUMN_ID + "=" + id + " LIMIT 1;";
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
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String columnIdQuery = "SELECT * FROM book WHERE id = " + bookId;
            Cursor cursor = db.rawQuery(columnIdQuery, null);
            int bookColumnId = cursor.getInt(0);

            String deleteQuery = "DELETE FROM " + BUZZLIST_INTERIM + " WHERE " + BOOK_ID + " = " + bookColumnId + ";";
            Cursor c1 = db.rawQuery(deleteQuery, null);

            String checkQuery = "SELECT * FROM " + TABLE_BOOKS + " INNER JOIN " + BUZZLIST_INTERIM + " ON " + TABLE_BOOKS
                    + "." + COLUMN_ID + " = " + BUZZLIST_INTERIM + "." + BOOK_ID + " WHERE " + BUZZLIST_INTERIM + "." + BUZZLIST_ID + "=" + list + " AND " + TABLE_BOOKS + "." + GOODREADS_ID + " = " + bookId + ";";
            Cursor c2 = db.rawQuery(checkQuery, null);


            if (c2 == null) {
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
