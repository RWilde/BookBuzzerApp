package com.fyp.n3015509.db;

/**
 * Created by tomha on 04-Apr-17.
 */

public class DBQueries {
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
            + BUZZLIST_ID + " integer not null);";

    private static final String CREATE_BOOK_INTERIM_TABLE = "create table "
            + BOOK_INTERIM + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + BOOK_ID + " integer not null, "
            + AUTHOR_ID + " integer not null);";

}
