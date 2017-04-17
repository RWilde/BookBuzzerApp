package com.fyp.n3015509.APIs;

import android.content.Context;

import com.fyp.n3015509.db.DBUtil;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsShelf;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tomha on 24-Mar-17.
 */

public class GoodreadsAPI {
    GoodreadsShelves shelves = new GoodreadsShelves();
    //GoodreadsShelf shelf = new GoodreadsShelf();
    GoodreadsAuthor author = new GoodreadsAuthor();

    public static GoodreadsShelf createGoodreadsShelf(int id, String name, int count) {
        GoodreadsShelf shelf = new GoodreadsShelf();
        return shelf.createGoodreadsShelf(name, count, id);
    }

    public GoodreadsAuthor createGoodreadsAuthor(int authorId, String authorName, String authorImageURL, String authorSmallImageURL, String authorLink, double authorAverageRating, int authorRatingsCount, int authorTextReviewsCount) {
        return author.createGoodreadsAuthor(authorId, authorName, authorImageURL, authorSmallImageURL, authorLink, authorAverageRating, authorRatingsCount, authorTextReviewsCount);
    }

    public ArrayList<GoodreadsShelf> getShelves(Context ctx) {
        return shelves.getShelves(ctx);
    }

    public ArrayList<JSONObject> RetrieveSelectedShelves(Context ctx, ArrayList<GoodreadsShelf> options) {
        BookBuzzerAPI util = new BookBuzzerAPI();
        ArrayList<GoodreadsShelf> shelfList= new ArrayList<>();
        ArrayList<JSONObject> json = new ArrayList<>();
        for (GoodreadsShelf shelf : options) {
            try {
                JSONObject shelvesJSON = new JSONObject();
                ArrayList<GoodreadsBook> booklist = shelves.getBookShelf(ctx, shelf);
                shelf.setBooks(booklist);
                shelfList.add(shelf);
                JSONObject apiParam = util.convertToApi(booklist);
                shelvesJSON.put(shelf.getShelfName(), apiParam);
                json.add(shelvesJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        DBUtil.SaveShelf(shelfList, ctx);
        return json;
    }

    public ArrayList<GoodreadsBook> getBooks(Context ctx) {
        //return shelves.getBookShelf(ctx);
        return null;
    }

    public static GoodreadsBook CreateGoodreadsBook(int id,
                                                    String isbn,
                                                    String isbn13,
                                                    int text_reviews_count,
                                                    String title,
                                                    String title_without_series,
                                                    String image_url,
                                                    String small_image_url,
                                                    String large_image_url,
                                                    String link,
                                                    int num_pages,
                                                    String format,
                                                    String edition_information,
                                                    String publisher,
                                                    int publication_day,
                                                    int publication_year,
                                                    int publication_month,
                                                    double average_rating,
                                                    int ratings_count,
                                                    String description,
                                                    int yearPublished,

                                                    ArrayList<GoodreadsAuthor> author) {
        GoodreadsBook book = new GoodreadsBook();

        return book.createGoodreadsBook(id, isbn, isbn13, text_reviews_count, title, title_without_series, image_url, small_image_url, large_image_url, link, num_pages,
                format, edition_information, publisher, publication_day, publication_year, publication_month, average_rating, ratings_count, description, yearPublished, author);
    }
}
