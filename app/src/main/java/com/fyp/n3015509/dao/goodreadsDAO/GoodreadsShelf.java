package com.fyp.n3015509.dao.goodreadsDAO;

import java.util.ArrayList;

/**
 * Created by tomha on 24-Mar-17.
 */

public class GoodreadsShelf {
    private String shelfName;
    private int bookNum;
    private int shelfId;

    public ArrayList<GoodreadsBook> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<GoodreadsBook> books) {
        this.books = books;
    }

    private ArrayList<GoodreadsBook> books;

    public GoodreadsShelf createGoodreadsShelf(String name, int num, int id)
    {
        this.shelfName = name;
        this.bookNum = num;
        this.shelfId = id;
        return this;
    }

    public String getShelfName() {
        return shelfName;
    }

    public void setShelfName(String shelfName) {
        this.shelfName = shelfName;
    }

    public int getBookNum() {
        return bookNum;
    }

    public void setBookNum(int bookNum) {
        this.bookNum = bookNum;
    }

    public int getShelfId() {
        return shelfId;
    }

    public void setShelfId(int shelfId) {
        this.shelfId = shelfId;
    }



}
