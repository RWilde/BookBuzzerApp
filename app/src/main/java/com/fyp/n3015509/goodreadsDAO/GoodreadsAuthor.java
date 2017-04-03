package com.fyp.n3015509.goodreadsDAO;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tomha on 24-Mar-17.
 */

public class GoodreadsAuthor {
    int id;
    String name;
    Bitmap image;
    Bitmap smallImage;
    String link;
    double average_rating;
    int ratingsCount;
    int textReviewsCount;

    public GoodreadsAuthor createGoodreadsAuthor(int id, String name, String imageURL, String smallImageURL, String link, double avg, int ratings, int reviews) {
        setImage(imageURL);
        setSmallImage(smallImageURL);

        this.id = id;
        this.name = name;
        this.image = getImage();
        this.smallImage = getSmallImage();
        this.link = link;
        this.average_rating = avg;
        this.ratingsCount = ratings;
        this.textReviewsCount = reviews;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(String image_url) {
        if (image_url != null) {
            try {
                URL url = new URL(image_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream is = connection.getInputStream();
                this.image = BitmapFactory.decodeStream(is);
                ;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String small_image_url) {
        if (small_image_url != null) {
            try {
                URL url = new URL(small_image_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream is = connection.getInputStream();
                this.smallImage = BitmapFactory.decodeStream(is);
                ;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(double average_rating) {
        this.average_rating = average_rating;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public int getTextReviewsCount() {
        return textReviewsCount;
    }

    public void setTextReviewsCount(int textReviewsCount) {
        this.textReviewsCount = textReviewsCount;
    }


}
