package com.fyp.n3015509.db.dao;

/**
 * Created by n3015509 on 24/03/2017.
 */

public class Author {
    //look into getting their descriptions

    int id;
    String name;
    byte[] image;
    byte[] small_image;
    String link;
    double average_rating;
    int ratings_count;
    int text_reviews_count;

    boolean notification;

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getSmall_image() {
        return small_image;
    }

    public void setSmall_image(byte[] small_image) {
        this.small_image = small_image;
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
