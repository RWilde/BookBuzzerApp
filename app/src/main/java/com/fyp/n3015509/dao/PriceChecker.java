package com.fyp.n3015509.dao;

/**
 * Created by tomha on 18-Apr-17.
 */

public class PriceChecker {
    public EditionTypes getType() {
        return type;
    }

    public void setType(EditionTypes type) {
        this.type = type;
    }

    EditionTypes type;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    Double price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
}

