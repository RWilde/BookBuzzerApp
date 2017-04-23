package com.fyp.n3015509.dao;

import com.fyp.n3015509.dao.enums.SearchResultType;

/**
 * Created by tomha on 22-Apr-17.
 */

public class SearchResult {
    int goodreadsId;
    String bookName;
    String authorName;
    String listName;
    SearchResultType type;

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public SearchResultType getType() {
        return type;
    }

    public void setType(SearchResultType type) {
        this.type = type;
    }

    public int getGoodreadsId() {
        return goodreadsId;
    }

    public void setGoodreadsId(int goodreadsId) {
        this.goodreadsId = goodreadsId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

}
