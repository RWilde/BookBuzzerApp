package com.fyp.n3015509.goodreadsDAO;

/**
 * Created by tomha on 24-Mar-17.
 */

public class GoodreadsShelf {
    private String shelfName;
    private int bookNum;
    private int shelfId;

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
