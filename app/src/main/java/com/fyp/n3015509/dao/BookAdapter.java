package com.fyp.n3015509.dao;

import android.graphics.Bitmap;

/**
 * Created by tomha on 19-Apr-17.
 */

public class BookAdapter {
    String buzzlistNames;

    public Boolean getAdded() {
        return added;
    }

    public void setAdded(Boolean added) {
        this.added = added;
    }

    Boolean added;

    public String getBuzzlistNames() {
        return buzzlistNames;
    }

    public void setBuzzlistNames(String buzzlistNames) {
        this.buzzlistNames = buzzlistNames;
    }

    public Bitmap getBuzzlistImages() {
        return buzzlistImages;
    }

    public void setBuzzlistImages(Bitmap buzzlistImages) {
        this.buzzlistImages = buzzlistImages;
    }

    public String getBuzzlistAuthors() {
        return buzzlistAuthors;
    }

    public void setBuzzlistAuthors(String buzzlistAuthors) {
        this.buzzlistAuthors = buzzlistAuthors;
    }

    public Integer getBuzzlistIds() {
        return buzzlistIds;
    }

    public void setBuzzlistIds(Integer buzzlistIds) {
        this.buzzlistIds = buzzlistIds;
    }

    public String getBuzzlistIsbns() {
        return buzzlistIsbns;
    }

    public void setBuzzlistIsbns(String buzzlistIsbns) {
        this.buzzlistIsbns = buzzlistIsbns;
    }

    Bitmap buzzlistImages;
    String buzzlistAuthors;
    Integer buzzlistIds;
    String buzzlistIsbns;
}
