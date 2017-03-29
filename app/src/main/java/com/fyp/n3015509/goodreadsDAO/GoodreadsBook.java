package com.fyp.n3015509.goodreadsDAO;

import com.fyp.n3015509.Util.GoodreadsUtil;

import java.util.ArrayList;

/**
 * Created by tomha on 24-Mar-17.
 */

public class GoodreadsBook {
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
    int publication_day;
    int publication_year;
    int publication_month;
    double average_rating;
    int ratings_count;
    String description;
    ArrayList<GoodreadsAuthor> author;
    int yearPublished;

    GoodreadsUtil util = new GoodreadsUtil();

    public GoodreadsBook createGoodreadsBook(int id,
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
        this.id = id;
        this.isbn = isbn;
        this.isbn13 = isbn13;
        this.text_reviews_count = text_reviews_count;
        this.title = title;
        this.title_without_series = title_without_series;
        this.image_url = image_url;
        this.small_image_url = small_image_url;
        this.large_image_url = large_image_url;
        this.link = link;
        this.num_pages = num_pages;
        this.format = format;
        this.edition_information = edition_information;
        this.publisher = publisher;
        this.publication_day = publication_day;
        this.publication_year = publication_year;
        this.publication_month = publication_month;
        this.average_rating = average_rating;
        this.ratings_count = ratings_count;
        this.description = description;
        this.yearPublished = yearPublished;

        this.author = author;

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

    public int getPublication_day() {
        return publication_day;
    }

    public void setPublication_day(int publication_day) {
        this.publication_day = publication_day;
    }

    public int getPublication_year() {
        return publication_year;
    }

    public void setPublication_year(int publication_year) {
        this.publication_year = publication_year;
    }

    public int getPublication_month() {
        return publication_month;
    }

    public void setPublication_month(int publication_month) {
        this.publication_month = publication_month;
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

    public ArrayList<GoodreadsAuthor> getAuthors() {
        return author;
    }

    public void setAuthors(ArrayList<GoodreadsAuthor> author) {
        this.author = author;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }




}
