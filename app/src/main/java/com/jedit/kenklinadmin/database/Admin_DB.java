package com.jedit.kenklinadmin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.jedit.kenklinadmin.models.Basket_Items;
import com.jedit.kenklinadmin.models.Services_offered;

public class Admin_DB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "KenklinAdmin.db";

    public Admin_DB(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String serviceQuery = "CREATE TABLE IF NOT EXISTS " + Services_offered.TABLE + " ( " +
                Services_offered.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Services_offered.ITEM_SIZE + "INTEGER DEFAULT 0, " +
                Services_offered.NAME + " TEXT )";
        db.execSQL(serviceQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Services_offered.TABLE);
        onCreate(db);
    }

    //create items table by service name, add itms if not empty
    void itemsServ_Table(Services_offered service){

        SQLiteDatabase db = getWritableDatabase();

        String serviceQuery = "CREATE TABLE IF NOT EXISTS " + "\"SETUP " + service.getName() + "\" ( " +
                Basket_Items.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Basket_Items.PRICE + " INTEGER, " +
                Basket_Items.NAME + " TEXT UNIQUE)";
        db.execSQL(serviceQuery);

        if (service.getService_items() != null && service.getService_items().size() > 0){
            //todo add items to table
        }

    }

    void additems(Services_offered servicesOffered){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues itemValues = new ContentValues();

        String tablename = "\"SETUP " + servicesOffered.getName() + "\"";

        for (Basket_Items item: servicesOffered.getService_items()) {

            itemValues.put(Basket_Items.NAME,item.getName().toUpperCase());
            itemValues.put(Basket_Items.PRICE,item.getPrice());

            try {
                db.insertOrThrow(tablename,null,itemValues);
            }catch (Exception ignored){}
        }

    }

    void updateitems(Services_offered servicesOffered){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues itemValues = new ContentValues();

        String tablename = "\"SETUP " + servicesOffered.getName() + "\"";

        for (Basket_Items item: servicesOffered.getService_items()) {

            itemValues.put(Basket_Items.NAME,item.getName().toUpperCase());
            itemValues.put(Basket_Items.PRICE,item.getPrice());

            try {
                db.update(tablename,itemValues,Basket_Items.NAME + " =? ", new String[] {item.getName().toUpperCase()});
            }catch (Exception ignored){}
        }

    }

}
