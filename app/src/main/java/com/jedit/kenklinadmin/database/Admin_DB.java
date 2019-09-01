package com.jedit.kenklinadmin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jedit.kenklinadmin.activities.DashboardActivity;
import com.jedit.kenklinadmin.models.Basket_Items;
import com.jedit.kenklinadmin.models.Request_Class;
import com.jedit.kenklinadmin.models.Services_offered;
import com.jedit.kenklinadmin.models.User_Class;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Admin_DB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "KenklinAdmin.db";
    private WeakReference<Context> weakcontext;

    public Admin_DB(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        weakcontext = new WeakReference<>(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String userQuery = "CREATE TABLE IF NOT EXISTS " + User_Class.TABLE + " ( "
                + User_Class.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + User_Class.FIREID + " TEXT UNIQUE NOT NULL, "
                + User_Class.FIRSTNAME + " TEXT, "
                + User_Class.LASTNAME + " TEXT, "
                + User_Class.MOBILE_NUMBER + " TEXT );";
        db.execSQL(userQuery);

        String serviceQuery = "CREATE TABLE IF NOT EXISTS " + Services_offered.TABLE + " ( " +
                Services_offered.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Services_offered.ITEM_SIZE + "INTEGER DEFAULT 0, " +
                Services_offered.NAME + " TEXT UNIQUE )";
        db.execSQL(serviceQuery);

        String reqQuery = "CREATE TABLE IF NOT EXISTS " + Request_Class.TABLE + " ( "
                + Request_Class.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Request_Class.REQ_TIME_STAMP + " TEXT UNIQUE, "
                + Request_Class.REQ_UID + " TEXT, "
                + Request_Class.REQDATE + " TEXT, "
                + Request_Class.COMPDATE + " TEXT, "
                + Request_Class.COMPSTATUS + " TEXT )";
        db.execSQL(reqQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Services_offered.TABLE);
        onCreate(db);
    }

    //==============================================================================================

    //--------------------------------------USER----------------------------------------------------
    public void addUser(User_Class user){

        ContentValues userValues = new ContentValues();

        userValues.put(User_Class.FIREID,user.getFirebaseID());
        userValues.put(User_Class.FIRSTNAME,user.getFn());
        userValues.put(User_Class.LASTNAME,user.getLn());
        userValues.put(User_Class.MOBILE_NUMBER,user.getNumber());

        SQLiteDatabase db = getWritableDatabase();

        try {
            db.insertOrThrow(User_Class.TABLE,null,userValues);
        }catch (SQLiteConstraintException ignored){
            updateUser(user);
        }

    }

    private void updateUser(User_Class userClass){

        ContentValues userValues = new ContentValues();

        userValues.put(User_Class.FIREID,userClass.getFirebaseID());
        userValues.put(User_Class.FIRSTNAME,userClass.getFn());
        userValues.put(User_Class.LASTNAME,userClass.getLn());
        userValues.put(User_Class.MOBILE_NUMBER,userClass.getNumber());

        SQLiteDatabase db = getWritableDatabase();

        db.update(User_Class.TABLE,userValues,User_Class.FIREID + " = ? ",
                new String[]{userClass.getFirebaseID()});
    }

    public User_Class fetchUser(String fireID){
        User_Class user;

        SQLiteDatabase db = getReadableDatabase();
        String userQery = "SELECT * FROM " + User_Class.TABLE + " WHERE "
                + User_Class.FIREID + " = \"" + fireID + "\"";

        Cursor c = db.rawQuery(userQery,null);
        c.moveToFirst();

        user = new User_Class(
                c.getString(c.getColumnIndexOrThrow(User_Class.FIRSTNAME)),
                c.getString(c.getColumnIndexOrThrow(User_Class.LASTNAME)),
                c.getString(c.getColumnIndexOrThrow(User_Class.MOBILE_NUMBER)));

        c.close();

        return user;
    }
    //--------------------------------------USER----------------------------------------------------

    private Cursor cursor_ServTable(){

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + Services_offered.TABLE + " ORDER BY " + Services_offered.NAME;

        return db.rawQuery(query,null);
    }

    private Cursor cursor_ReqTable(){

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + Request_Class.TABLE + " ORDER BY " + Request_Class.REQDATE + " DESC;" ;

        return db.rawQuery(query,null);
    }

    private Cursor cursor_itemTable(String servicename){

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + "\"SETUP-" + servicename.toUpperCase() + "\" ORDER BY " + Basket_Items.NAME;

        return db.rawQuery(query,null);
    }

    public ArrayList<String> service_names(){

        ArrayList<String> names = new ArrayList<>();

        Cursor c = cursor_ServTable();

        while (c.moveToNext()){
            names.add(c.getString(c.getColumnIndexOrThrow(Services_offered.NAME)));
        }
        c.close();

        return names;
    }

    public ArrayList<Basket_Items> items_list(String servicename){

        ArrayList<Basket_Items> items = new ArrayList<>();

        Cursor c = cursor_itemTable(servicename);

        while (c.moveToNext()){
            items.add(new Basket_Items(
                    c.getString(c.getColumnIndexOrThrow(Basket_Items.NAME)),
                    servicename,
                    c.getInt(c.getColumnIndexOrThrow(Basket_Items.PRICE))));
        }
        c.close();

        return items;

    }

    //add service from online DB
    public void AddonlineServices(Services_offered servicesOffered){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues servValues = new ContentValues();

        servValues.put(Services_offered.NAME,servicesOffered.getName().toUpperCase());

        try {
            db.insertOrThrow(Services_offered.TABLE,null,servValues);
            itemsTable_create(servicesOffered);

            if (servicesOffered.getService_items() != null && servicesOffered.getService_items().size() > 0){
                servValues.put(Services_offered.ITEM_SIZE,servicesOffered.getService_items().size());

                additems(servicesOffered);
            }
        }catch (Exception ignored){
        }

    }

    //add service and item count
    public void addService(Services_offered servicesOffered){

       SQLiteDatabase db = getWritableDatabase();

       ContentValues servValues = new ContentValues();

       servValues.put(Services_offered.NAME,servicesOffered.getName().toUpperCase());

       try {
           db.insertOrThrow(Services_offered.TABLE,null,servValues);
           itemsTable_create(servicesOffered);

           if (servicesOffered.getService_items() != null && servicesOffered.getService_items().size() > 0){
               servValues.put(Services_offered.ITEM_SIZE,servicesOffered.getService_items().size());

               additems(servicesOffered);
           }
           service_toFireDB(servicesOffered);
       }catch (Exception ignored){
       }

    }

    private void service_toFireDB(Services_offered service){
        DatabaseReference serviceref = FirebaseDatabase.getInstance().getReference("Services");
        serviceref.child(service.getName()).setValue(service);
    }

    private void serviceDeleteFireDB(String servicename){
        DatabaseReference serviceref = FirebaseDatabase.getInstance().getReference("Services");
        serviceref.child(servicename).removeValue();
    }

    public void delete_Service(String serviceName){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.execSQL("DELETE FROM " + Services_offered.TABLE + " WHERE "
                + Services_offered.NAME + " = \"" + serviceName.toUpperCase() + "\"");

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "\"SETUP-" + serviceName.toUpperCase() + "\"" );

        serviceDeleteFireDB(serviceName);
    }

    //==============================================================================================

    //create items table by service name, add items if not empty
    private void itemsTable_create(Services_offered service){

        SQLiteDatabase db = getWritableDatabase();

        String serviceQuery = "CREATE TABLE IF NOT EXISTS " + "\"SETUP-" + service.getName().toUpperCase() + "\" ( " +
                Basket_Items.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Basket_Items.PRICE + " INTEGER, " +
                Basket_Items.NAME + " TEXT UNIQUE)";
        db.execSQL(serviceQuery);

        if (service.getService_items() != null && service.getService_items().size() > 0){

            additems(service);
        }

    }

    public void addSingle_item(String service, Basket_Items item){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues itemValues = new ContentValues();

        String tablename = "\"SETUP-" + service.toUpperCase() + "\"";

            itemValues.put(Basket_Items.NAME,item.getItem_name().toUpperCase());
            itemValues.put(Basket_Items.PRICE,item.getPrice());

            try {
                db.insertOrThrow(tablename,null,itemValues);
                service_toFireDB(new Services_offered(service, items_list(service)));
            }catch (Exception ignored){}


    }

    private void additems(Services_offered servicesOffered){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues itemValues = new ContentValues();

        String tablename = "\"SETUP-" + servicesOffered.getName().toUpperCase() + "\"";

        for (Basket_Items item: servicesOffered.getService_items()) {

            itemValues.put(Basket_Items.NAME,item.getItem_name().toUpperCase());
            itemValues.put(Basket_Items.PRICE,item.getPrice());

            try {
                db.insertOrThrow(tablename,null,itemValues);
            }catch (Exception ignored){}
        }

    }

    public void delete_ServiceItem(String servicename, String itemname){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.execSQL("DELETE FROM " + "\"SETUP-" + servicename.toUpperCase() + "\"" + " WHERE "
                + Basket_Items.NAME + " = \"" + itemname.toUpperCase() + "\"");

        service_toFireDB(new Services_offered(servicename,items_list(servicename)));
    }

    public void addRequest(Request_Class request){

        ContentValues reqValues = new ContentValues();
        reqValues.put(Request_Class.REQ_UID,request.getRequesterID());
        reqValues.put(Request_Class.REQ_TIME_STAMP,request.getReqTime_stamp());
        reqValues.put(Request_Class.REQDATE,request.getReqDate());
        reqValues.put(Request_Class.COMPDATE,request.getCompleteDate());
        reqValues.put(Request_Class.COMPSTATUS,String.valueOf(request.getStatus()));

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.insertOrThrow(Request_Class.TABLE,null,reqValues);

            if (request.getLaundrylist().size() > 0){
                orderItems_Table(request);
            }

            if (weakcontext.get() instanceof DashboardActivity){
                ((DashboardActivity) weakcontext.get()).load_localRequests();
            }

        }catch (Exception ignored){
            updateRequest(request);
        }
    }

    private void updateRequest(Request_Class request){

        ContentValues reqValues = new ContentValues();
        reqValues.put(Request_Class.REQ_UID,request.getRequesterID());
        reqValues.put(Request_Class.REQ_TIME_STAMP,request.getReqTime_stamp());
        reqValues.put(Request_Class.REQDATE,request.getReqDate());
        reqValues.put(Request_Class.COMPDATE,request.getCompleteDate());
        reqValues.put(Request_Class.COMPSTATUS,String.valueOf(request.getStatus()));

        SQLiteDatabase db = getWritableDatabase();
        db.update(Request_Class.TABLE,reqValues,Request_Class.REQ_TIME_STAMP + "=? AND " + Request_Class.REQ_UID + " =?",
                new String[]{request.getReqTime_stamp(),request.getRequesterID()});

        if (weakcontext.get() instanceof DashboardActivity){
            ((DashboardActivity) weakcontext.get()).load_localRequests();
        }
    }

    public ArrayList<Request_Class> all_requests(){

        ArrayList<Request_Class> reqs = new ArrayList<>();

        Cursor c = cursor_ReqTable();

        while (c.moveToNext()){

            Request_Class req = new Request_Class(
                    c.getString(c.getColumnIndexOrThrow(Request_Class.REQ_TIME_STAMP)),
                    c.getString(c.getColumnIndexOrThrow(Request_Class.REQDATE)),
                    c.getString(c.getColumnIndexOrThrow(Request_Class.COMPDATE)),
                    c.getString(c.getColumnIndexOrThrow(Request_Class.COMPSTATUS))
            );

            req.setRequesterID(c.getString(c.getColumnIndexOrThrow(Request_Class.REQ_UID)));

            reqs.add(req);
        }

        return reqs;
    }

    //==============================================================================================

    private void orderItems_Table(Request_Class order){

        SQLiteDatabase sqDB = getWritableDatabase();
        String tableName = order.getRequesterID() + "_" + order.getReqTime_stamp();

        String reqQuery = "CREATE TABLE IF NOT EXISTS \"" + tableName + "\" ( "
                + Basket_Items.ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Basket_Items.NAME + " TEXT, "
                + Services_offered.NAME + " TEXT, "
                + Basket_Items.PRICE + " TEXT, "
                + Basket_Items.QTY + " INTEGER);";

        sqDB.execSQL(reqQuery);
        add_order_items(order);
    }

    private void add_order_items(Request_Class order){

        String tableName = order.getRequesterID() + "_" + order.getReqTime_stamp();
        ContentValues orderValues = new ContentValues();
        SQLiteDatabase sqDB = getWritableDatabase();

        for (Basket_Items item : order.getLaundrylist()){
            orderValues.put(Basket_Items.NAME,item.getItem_name());
            orderValues.put(Basket_Items.QTY,item.getQuantity());
            orderValues.put(Services_offered.NAME,item.getServ_name());
            orderValues.put(Basket_Items.PRICE, item.getPrice());

            sqDB.insert(tableName, null, orderValues);
        }

    }

    private Cursor cursor_orderItems(String tableName){

        SQLiteDatabase sqDB = getReadableDatabase();

        return sqDB.rawQuery("SELECT * FROM " + tableName,null);
    }

    public ArrayList<Basket_Items> fetch_items(Request_Class order){

        ArrayList<Basket_Items> items_list = new ArrayList<>();
        String tableName = order.getRequesterID() + "_" + order.getReqTime_stamp();

        Cursor c = cursor_orderItems(tableName);

        while (c.moveToNext()){
            Basket_Items item = new Basket_Items(
                    c.getString(c.getColumnIndexOrThrow(Basket_Items.NAME)),
                    c.getString(c.getColumnIndexOrThrow(Services_offered.NAME)),
                    c.getInt(c.getColumnIndexOrThrow(Basket_Items.QTY)),
                    c.getInt(c.getColumnIndexOrThrow(Basket_Items.PRICE))
            );

            items_list.add(item);
        }

        return items_list;
    }

}
