package com.fyp.n3015509.dao.goodreadsDAO;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fyp.n3015509.APIs.GoodreadsAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by tomha on 24-Mar-17.
 */

public class GoodreadsBook {
    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    String imgUrl;

    public String getSmallImgUrl() {
        return smallImgUrl;
    }

    public void setSmallImgUrl(String smallImgUrl) {
        this.smallImgUrl = smallImgUrl;
    }

    public String getLrgImgUrl() {
        return lrgImgUrl;
    }

    public void setLrgImgUrl(String lrgImgUrl) {
        this.lrgImgUrl = lrgImgUrl;
    }

    String smallImgUrl;
    String lrgImgUrl;

    int columnId;
    int id;
    String isbn;
    String isbn13;
    int textReviewsCount;
    String title;
    String titleWithoutSeries;
    Bitmap image;
    Bitmap smallImage;
    Bitmap largeImage;
    String link;
    int numPages;
    String format;
    String editionInformation;
    String publisher;
    int publicationDay;
    int publicationYear;
    int publicationMonth;
    double average_rating;
    int ratingsCount;
    String description;
    ArrayList<GoodreadsAuthor> author;
    int yearPublished;

    public double getKindlePrice() {
        return kindlePrice;
    }

    public void setKindlePrice(double kindlePrice) {
        this.kindlePrice = kindlePrice;
    }

    public double getPaperbackPrice() {
        return paperbackPrice;
    }

    public void setPaperbackPrice(double paperbackPrice) {
        this.paperbackPrice = paperbackPrice;
    }

    public double getHardcoverPrice() {
        return hardcoverPrice;
    }

    public void setHardcoverPrice(double hardcoverPrice) {
        this.hardcoverPrice = hardcoverPrice;
    }

    double kindlePrice;
    double paperbackPrice;
    double hardcoverPrice;

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    String releaseDate;

    GoodreadsAPI util = new GoodreadsAPI();

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
        setImage_url(image_url);
        setSmall_image_url(small_image_url);
        setLarge_image_url(large_image_url);

        this.imgUrl = image_url;
        this.smallImgUrl = small_image_url;
        this.lrgImgUrl = large_image_url;

        this.id = id;
        this.isbn = isbn;
        this.isbn13 = isbn13;
        this.textReviewsCount = text_reviews_count;
        this.title = title;
        this.titleWithoutSeries = title_without_series;
        this.image = getImage();
        this.smallImage = getSmallImage();
        this.largeImage = getLargeImage();
        this.link = link;
        this.numPages = num_pages;
        this.format = format;
        this.editionInformation = edition_information;
        this.publisher = publisher;
        this.publicationDay = publication_day;
        this.publicationYear = publication_year;
        this.publicationMonth = publication_month;
        this.average_rating = average_rating;
        this.ratingsCount = ratings_count;
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

    public int getTextReviewsCount() {
        return textReviewsCount;
    }

    public void setTextReviewsCount(int textReviewsCount) {
        this.textReviewsCount = textReviewsCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleWithoutSeries() {
        return titleWithoutSeries;
    }

    public void setTitleWithoutSeries(String titleWithoutSeries) {
        this.titleWithoutSeries = titleWithoutSeries;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage_url(String image) {
        if (image != null) {
            try {
                URL url = new URL(image);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

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

    public void setImage(byte[] image) {
        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            this.image = bitmap;
        }
    }

    public Bitmap getSmallImage() {
        return smallImage;
    }

    public void setSmall_image_url(String image) {
        if (image != null) {
            try {
                URL url = new URL(image);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

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

    public void setSmallImage(byte[] image) {
        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            this.smallImage = bitmap;
        }
    }

    public Bitmap getLargeImage() {
        return largeImage;
    }

    public void setLarge_image_url(String large_image_url) {
        if (large_image_url != null) {
            try {
                URL url = new URL(large_image_url);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                InputStream is = connection.getInputStream();
                this.largeImage = BitmapFactory.decodeStream(is);
                ;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLargeImage(byte[] image) {
        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            this.largeImage = bitmap;
        }
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getEditionInformation() {
        return editionInformation;
    }

    public void setEditionInformation(String editionInformation) {
        this.editionInformation = editionInformation;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationDay() {
        return publicationDay;
    }

    public void setPublicationDay(int publicationDay) {
        this.publicationDay = publicationDay;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getPublicationMonth() {
        return publicationMonth;
    }

    public void setPublicationMonth(int publicationMonth) {
        this.publicationMonth = publicationMonth;
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
