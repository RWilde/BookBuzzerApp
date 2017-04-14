package com.fyp.n3015509.dao;

/**
 * Created by tomha on 14-Apr-17.
 */

public class BuzzNotification {
    int bookId;

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

    Boolean notified;
    NotificationTypes type;
    String bookName;
}
