package com.jedit.kenklinadmin.models;

import android.support.annotation.Nullable;

import java.util.ArrayList;

public class Services_offered {

    public static final String TABLE = "SERVICES";
    public static final String ID = "_id";
    public static final String ITEM_SIZE = "list_size";
    public static final String NAME = "service_name";

    private String name;
    private ArrayList<Basket_Items> service_items;

    public Services_offered() {
    }

    public Services_offered(String name, @Nullable ArrayList<Basket_Items> service_items) {
        this.name = name;
        this.service_items = service_items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Basket_Items> getService_items() {
        return service_items;
    }

    public void setService_items(ArrayList<Basket_Items> service_items) {
        this.service_items = service_items;
    }
}
