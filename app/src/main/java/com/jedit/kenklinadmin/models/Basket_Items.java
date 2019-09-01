package com.jedit.kenklinadmin.models;

public class Basket_Items {

    public static final String ID = Services_offered.ID;
    public static final String NAME = "item_name";
    public static final String QTY = "quantity";
    public static final String PRICE = "price";

    private String item_name;
    private String serv_name;
    private int quantity;
    private int price;

    public Basket_Items() {
    }

    public Basket_Items(String item_name, String serv_name, int quantity, int price) {
        this.item_name = item_name;
        this.serv_name = serv_name;
        this.quantity = quantity;
        this.price = price;
    }

    public Basket_Items(String item_name, String serv_name, int price) {
        this.item_name = item_name;
        this.serv_name = serv_name;
        this.price = price;
    }

    public Basket_Items(String item_name, int price) {
        this.item_name = item_name;
        this.price = price;
    }

    public Basket_Items(String item_name, String serv_name) {
        this.item_name = item_name;
        this.serv_name = serv_name;
    }

    public Basket_Items(String item_name, int quantity, int price) {
        this.item_name = item_name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
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

    public String getServ_name() {
        return serv_name;
    }

    public void setServ_name(String serv_name) {
        this.serv_name = serv_name;
    }
}
