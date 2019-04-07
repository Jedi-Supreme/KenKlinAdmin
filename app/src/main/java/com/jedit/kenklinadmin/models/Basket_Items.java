package com.jedit.kenklinadmin.models;

public class Basket_Items {

    public static final String ID = Services_offered.ID;
    public static final String NAME = "item_name";
    public static final String QTY = "quantity";
    public static final String PRICE = "price";

    private String name;
    private int quantity;
    private int price;

    public Basket_Items() {
    }

    public Basket_Items(String name, int quantity, int price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
