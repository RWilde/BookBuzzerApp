package com.fyp.n3015509.goodreads;

/**
 * Created by tomha on 24-Mar-17.
 */

public class GoodreadsAuthor {
    int id;
    String name;
    String image_url;
    String small_image_url;
    String link;
    double average_rating;
    int ratings_count;
    int text_reviews_count;

    public GoodreadsAuthor createGoodreadsAuthor(int id, String name, String image, String smallImage, String link, double avg, int ratings, int reviews) {
        this.id = id;
        this.name = name;
        this.image_url = image;
        this.small_image_url = smallImage;
        this.link = link;
        this.average_rating = avg;
        this.ratings_count = ratings;
        this.text_reviews_count = reviews;
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSmall_image_url() {
        return small_image_url;
    }

    public void setSmall_image_url(String small_image_url) {
        this.small_image_url = small_image_url;
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

    public int getRatings_count() {
        return ratings_count;
    }

    public void setRatings_count(int ratings_count) {
        this.ratings_count = ratings_count;
    }

    public int getText_reviews_count() {
        return text_reviews_count;
    }

    public void setText_reviews_count(int text_reviews_count) {
        this.text_reviews_count = text_reviews_count;
    }


}
