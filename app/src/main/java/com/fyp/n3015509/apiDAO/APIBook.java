package com.fyp.n3015509.apiDAO;


import com.fyp.n3015509.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;
import com.google.gson.Gson;

/**
 * Created by tomha on 29-Mar-17.
 */

public class APIBook {
    int id;
    String isbn;
    String isbn13;
    int text_reviews_count;
    String title;
    String title_without_series;
    String image_url;
    String small_image_url;
    String large_image_url;
    String link;
    int num_pages;
    String format;
    String edition_information;
    String publisher;
    String date;

    double average_rating;
    int ratings_count;
    String description;
    int yearPublished;

    String author;

    public APIBook createApiBook(GoodreadsBook book) {
        this.id = book.getId();
        this.isbn = book.getIsbn();
        this.isbn13 = book.getIsbn13();
        this.text_reviews_count = book.getText_reviews_count();
        this.title = book.getTitle();
        this.title_without_series = book.getTitle_without_series();
        this.image_url = book.getImage_url();
        this.small_image_url = book.getSmall_image_url();
        this.large_image_url = book.getLarge_image_url();
        this.link = book.getLink();
        this.num_pages = book.getNum_pages();
        this.format = book.getFormat();
        this.edition_information = book.getEdition_information();
        this.publisher = book.getPublisher();
        this.date = book.getPublication_day() + "/" + book.getPublication_month() + "/" + book.getPublication_year();
        this.average_rating = book.getAverage_rating();
        this.ratings_count = book.getRatings_count();
        this.description = book.getDescription();
        this.yearPublished = book.getYearPublished();
        this.author = new Gson().toJson(book.getAuthors());

        return this;
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

    public String getLarge_image_url() {
        return large_image_url;
    }

    public void setLarge_image_url(String large_image_url) {
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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getEdition_information() {
        return edition_information;
    }

    public void setEdition_information(String edition_information) {
        this.edition_information = edition_information;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
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

    public int getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
