package com.fyp.n3015509.dao;

import android.graphics.Bitmap;

/**
 * Created by tomha on 14-Apr-17.
 */

public class BuzzNotification {
    public int getGoodreadsId() {
        return goodreadsId;
    }

    public void setGoodreadsId(int goodreadsId) {
        this.goodreadsId = goodreadsId;
    }

    int goodreadsId;
    int bookId;
    Boolean notified;
    NotificationTypes type;
    String bookName;
    Bitmap image;
    String message;
    Boolean read;


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public NotificationTypes getType() {
        return type;
    }

    public void setType(NotificationTypes type) {
        this.type = type;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }


}
