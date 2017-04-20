package com.fyp.n3015509.dao;

import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;

import java.util.ArrayList;

/**
 * Created by tomha on 20-Apr-17.
 */

public class Buzzlist {
    String listName;

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public ArrayList<GoodreadsBook> getBookList() {
        return bookList;
    }

    public void setBookList(ArrayList<GoodreadsBook> bookList) {
        this.bookList = bookList;
    }

    ArrayList<GoodreadsBook> bookList;
}
