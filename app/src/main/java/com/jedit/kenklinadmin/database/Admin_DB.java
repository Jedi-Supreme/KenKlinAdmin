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
                Services_offered.NAME + " TEXT UNIQUE )";
        db.execSQL(serviceQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Services_offered.TABLE);
        onCreate(db);
    }

    //==============================================================================================

    //add service and item count
    void addService(Services_offered servicesOffered){

       SQLiteDatabase db = getWritableDatabase();

       ContentValues servValues = new ContentValues();

       servValues.put(Services_offered.NAME,servicesOffered.getName().toUpperCase());

       if (servicesOffered.getService_items() != null && servicesOffered.getService_items().size() > 0){
           servValues.put(Services_offered.ITEM_SIZE,servicesOffered.getService_items().size());

           itemsTable_create(servicesOffered);
       }

       try {
           db.insertOrThrow(Services_offered.TABLE,null,servValues);

       }catch (Exception ignored){
           updateService(servicesOffered);
       }

    }

    private void updateService(Services_offered servicesOffered){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues servValues = new ContentValues();

        servValues.put(Services_offered.NAME,servicesOffered.getName().toUpperCase());

        if (servicesOffered.getService_items() != null && servicesOffered.getService_items().size() > 0){
            servValues.put(Services_offered.ITEM_SIZE,servicesOffered.getService_items().size());
        }

        try {
            db.update(Services_offered.TABLE,servValues,
                    Services_offered.NAME + " = ?",
                    new String[]{servicesOffered.getName().toUpperCase()});

            if (servicesOffered.getService_items() != null && servicesOffered.getService_items().size() > 0){
                updateitems(servicesOffered);
            }
        }catch (Exception ignored){}

    }

    void delete_Service(String serviceName){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.execSQL("DELETE FROM " + Services_offered.TABLE + " WHERE "
                + Services_offered.NAME + " = \"" + serviceName.toUpperCase() + "\"");

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "\"SETUP " + serviceName.toUpperCase() + "\"" );

        //todo save_Online to update list(contactsList(contactPerson.getUser_fireID()),contactPerson.getUser_fireID());

    }

    //==============================================================================================

    //create items table by service name, add items if not empty
    private void itemsTable_create(Services_offered service){

        SQLiteDatabase db = getWritableDatabase();

        if (service.getService_items() != null && service.getService_items().size() > 0){

            String serviceQuery = "CREATE TABLE IF NOT EXISTS " + "\"SETUP " + service.getName().toUpperCase() + "\" ( " +
                    Basket_Items.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Basket_Items.PRICE + " INTEGER, " +
                    Basket_Items.NAME + " TEXT UNIQUE)";
            db.execSQL(serviceQuery);

            additems(service);

            //todo add items to table
        }

    }

    private void additems(Services_offered servicesOffered){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues itemValues = new ContentValues();

        String tablename = "\"SETUP " + servicesOffered.getName().toUpperCase() + "\"";

        for (Basket_Items item: servicesOffered.getService_items()) {

            itemValues.put(Basket_Items.NAME,item.getName().toUpperCase());
            itemValues.put(Basket_Items.PRICE,item.getPrice());

            try {
                db.insertOrThrow(tablename,null,itemValues);
            }catch (Exception ignored){}
        }

    }

    private void updateitems(Services_offered servicesOffered){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues itemValues = new ContentValues();

        String tablename = "\"SETUP " + servicesOffered.getName().toUpperCase() + "\"";

        for (Basket_Items item: servicesOffered.getService_items()) {

            itemValues.put(Basket_Items.NAME,item.getName().toUpperCase());
            itemValues.put(Basket_Items.PRICE,item.getPrice());

            try {
                db.update(tablename,itemValues,Basket_Items.NAME + " =? ", new String[] {item.getName().toUpperCase()});
            }catch (Exception ignored){}
        }

    }

    void delete_ServiceItem(String servicename, String itemname){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.execSQL("DELETE FROM " + "\"SETUP " + servicename.toUpperCase() + "\"" + " WHERE "
                + Basket_Items.NAME + " = \"" + itemname.toUpperCase() + "\"");

        //todo save_Online to update list(contactsList(contactPerson.getUser_fireID()),contactPerson.getUser_fireID());

    }


}
