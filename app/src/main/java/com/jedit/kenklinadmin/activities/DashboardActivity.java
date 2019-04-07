package com.jedit.kenklinadmin.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.jedit.kenklinadmin.R;

public class DashboardActivity extends AppCompatActivity {

    //===========================================ON CREATE==========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (!isUserLogged_in()){
            //go to login screen
            tologin();
        }

    }

    //===========================================ON CREATE==========================================

    //------------------------------------------DEFINED METHODS-------------------------------------
    //test if user is logged in
    boolean isUserLogged_in(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void setup_laundry(View view) {
        toSettings();
    }

    public void admin_logout(View view) {
        FirebaseAuth.getInstance().signOut();
        tologin();
    }

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
