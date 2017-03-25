package com.fyp.n3015509.db.dao;

/**
 * Created by n3015509 on 24/03/2017.
 */

public class Book {
    int id;
    String isbn;
    String isbn13;
    int text_reviews_count;
    String title;
    String title_without_series;
    byte[] image_url;
    byte[] small_image_url;
    byte[] large_image_url;
    String link;
    int num_pages;
    String publisher;
    String year;
    double average_rating;
    int ratings_count;
    String description;
    int author_id;

    boolean notification;
    boolean priceDropNotification;

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isPriceDropNotification() {
        return priceDropNotification;
    }

    public void setPriceDropNotification(boolean priceDropNotification) {
        this.priceDropNotification = priceDropNotification;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public int getText_reviews_count() {
        return text_reviews_count;
    }

    public void setText_reviews_count(int text_reviews_count) {
        this.text_reviews_count = text_reviews_count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_without_series() {
        return title_without_series;
    }

    public void setTitle_without_series(String title_without_series) {
        this.title_without_series = title_without_series;
    }

    public byte[] getImage_url() {
        return image_url;
    }

    public void setImage_url(byte[] image_url) {
        this.image_url = image_url;
    }

    public byte[] getSmall_image_url() {
        return small_image_url;
    }

    public void setSmall_image_url(byte[] small_image_url) {
        this.small_image_url = small_image_url;
    }

    public byte[] getLarge_image_url() {
        return large_image_url;
    }

    public void setLarge_image_url(byte[] large_image_url) {
        this.large_image_url = large_image_url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getNum_pages() {
        return num_pages;
    }

    public void setNum_pages(int num_pages) {
        this.num_pages = num_pages;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }


}
