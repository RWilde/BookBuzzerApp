package com.fyp.n3015509.dao.APIdao;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tomha on 29-Mar-17.
 */

public class APIBookList {
    public String getBookList() {
        return bookList;
    }

    public void setBookList(HashMap<String, String> bookList) {
        this.bookList =  new Gson().toJson(bookList);
    }

    String bookList;

}
