package com.jedit.kenklinadmin.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jedit.kenklinadmin.R;
import com.jedit.kenklinadmin.adapters.Req_recy_Adapter;
import com.jedit.kenklinadmin.database.Admin_DB;
import com.jedit.kenklinadmin.models.Basket_Items;
import com.jedit.kenklinadmin.models.Request_Class;
import com.jedit.kenklinadmin.models.Services_offered;
import com.jedit.kenklinadmin.models.User_Class;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    Admin_DB adminDb;
    WeakReference<DashboardActivity> weakdash;
    TextView tv_no_req;
    RecyclerView recy_orders;

    //===========================================ON CREATE==========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        weakdash = new WeakReference<>(this);
        adminDb = new Admin_DB(weakdash.get(),null);

        tv_no_req = findViewById(R.id.tv_no_req);
        recy_orders = findViewById(R.id.recy_orders);

        if (!isUserLogged_in()){
            //go to login screen
            tologin();
        }

    }

    //===========================================ON CREATE==========================================

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=OVERRIDE METHODS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    @Override
    protected void onResume() {
        super.onResume();

        try {
            fetchServices_Online();
            fetchOrders_Online();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Loading failed  with error: " + e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=OVERRIDE METHODS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    //------------------------------------------DEFINED METHODS-------------------------------------
    //test if user is logged in
    boolean isUserLogged_in(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    void fetchServices_Online(){

        DatabaseReference setupref = FirebaseDatabase.getInstance().getReference("Services");

        setupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //fetch all services
                for (DataSnapshot servicesnap : dataSnapshot.getChildren()){

                    Services_offered singleservice = servicesnap.getValue(Services_offered.class);

                    try {
                        adminDb.AddonlineServices(singleservice);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
                    }
                }

                setupref.removeEventListener(this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void fetchOrders_Online(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String date = simpleDateFormat.format(calendar.getTime());

        DatabaseReference orders_ref = FirebaseDatabase.getInstance().getReference("Orders");

        orders_ref.child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot timeStamp_order : dataSnapshot.getChildren()){
                        Request_Class request = timeStamp_order.getValue(Request_Class.class);

                        //Fetch orders online and use requester id to fetch user details, create table of order
                        // refresh list after inserting data

                        if (request != null){
                            request.setReqTime_stamp(timeStamp_order.getKey());
                            adminDb.addRequest(request);
                            fetch_user_profiles(request.getRequesterID());
                            //Toast.makeText(getApplicationContext(),timeStamp_order.getKey(),Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    load_localRequests();
                }

                orders_ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void fetch_user_profiles(String user_id){
        DatabaseReference users_ref = FirebaseDatabase.getInstance().getReference("AppUsers");

        users_ref.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User_Class req_user = dataSnapshot.getValue(User_Class.class);

                if (req_user != null){
                    adminDb.addUser(req_user);
                }

                users_ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void load_localRequests(){

        if (adminDb.all_requests().size() > 0){
            tv_no_req.setVisibility(View.GONE);
            Req_recy_Adapter req_recy_adapter = new Req_recy_Adapter(adminDb.all_requests());
            recy_orders.setLayoutManager(new LinearLayoutManager(weakdash.get()));
            recy_orders.setAdapter(req_recy_adapter);
        }

    }

    //------------------------------------------DEFINED METHODS-------------------------------------

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-BUTTON CLICK LISTENERS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
    public void setup_laundry(View view) {
        toSettings();
    }

    public void admin_logout(View view) {
        FirebaseAuth.getInstance().signOut();
        tologin();
    }
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-BUTTON CLICK LISTENERS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    //--------------------------------------------INTENTS-------------------------------------------
    void toSettings(){
        Intent settings_intent = new Intent(getApplicationContext(),SetupActivity.class);
        startActivity(settings_intent);
    }

    void tologin(){
        Intent login_intent = new Intent(getApplicationContext(), LoginActivity.class);
        login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login_intent);
        super.finish();
    }
    //--------------------------------------------INTENTS-------------------------------------------

}
