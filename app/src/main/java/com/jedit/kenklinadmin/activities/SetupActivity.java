package com.jedit.kenklinadmin.activities;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jedit.kenklinadmin.R;
import com.jedit.kenklinadmin.adapters.Items_recy_Adapter;
import com.jedit.kenklinadmin.adapters.Services_recy_Adapter;
import com.jedit.kenklinadmin.common;
import com.jedit.kenklinadmin.database.Admin_DB;
import com.jedit.kenklinadmin.models.Basket_Items;
import com.jedit.kenklinadmin.models.Services_offered;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SetupActivity extends AppCompatActivity {

    WeakReference<SetupActivity> weakSetup;

    ConstraintLayout const_setup_layout;
    RecyclerView recy_serv_items;
    Admin_DB adminDb;

    TextView tv_set_noshow;

    TextInputEditText et_setup_serv;

    //===========================================ON CREATE==========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        weakSetup = new WeakReference<>(this);

        adminDb = new Admin_DB(weakSetup.get(),null);

        const_setup_layout = findViewById(R.id.const_setup_layout);

        et_setup_serv = findViewById(R.id.et_setup_serv);

        recy_serv_items = findViewById(R.id.recy_service_items);

        tv_set_noshow = findViewById(R.id.tv_set_noshow);

    }
    //===========================================ON CREATE==========================================

    //----------------------------------------------ONCLICK LISTENERS-------------------------------
    public void add_service(View view) {
        add_service_dialog();
    }

    public void Services_Dialog(View view) {
        pick_service_dialog();
        recy_serv_items.setAdapter(null);
    }
    //----------------------------------------------ONCLICK LISTENERS-------------------------------

    //-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-DEFINED METHODS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
    //build dialog for user to enter service name
    void add_service_dialog() {
        AlertDialog name_dialog_builder = new AlertDialog.Builder(weakSetup.get()).create();

        View servname_view = LayoutInflater.from(weakSetup.get())
                .inflate(R.layout.diag_ent_name, const_setup_layout, false);

        name_dialog_builder.setCancelable(true);
        name_dialog_builder.setView(servname_view);

        Button bt_serv_popsave = servname_view.findViewById(R.id.bt_serv_popsave);
        Button bt_serv_popcncl = servname_view.findViewById(R.id.bt_serv_popcncl);
        TextView tv_name_title = servname_view.findViewById(R.id.tv_diag_title);
        TextInputEditText et_namediag = servname_view.findViewById(R.id.et_serv_popname);

        tv_name_title.setText(R.string.serv_name_lbl);

        bt_serv_popsave.setOnClickListener(v -> {
            if (!et_namediag.getText().toString().isEmpty() || !et_namediag.getText().toString().equals("")){
                adminDb.addService(new Services_offered(et_namediag.getText().toString(),null));
                Toast.makeText(getApplicationContext(),
                        et_namediag.getText().toString().toUpperCase() + " successfully added", Toast.LENGTH_SHORT).show();
                et_namediag.setText("");
            }else {

                common.Mysnackbar(const_setup_layout,"Enter Service Name", Snackbar.LENGTH_SHORT).show();
            }
        });

        bt_serv_popcncl.setOnClickListener(v -> name_dialog_builder.dismiss());

        name_dialog_builder.show();

    }

    void add_item_dialog() {
        AlertDialog item_dialog_builder = new AlertDialog.Builder(weakSetup.get()).create();

        View items_view = LayoutInflater.from(weakSetup.get())
                .inflate(R.layout.diag_name_price, const_setup_layout, false);

        item_dialog_builder.setCancelable(true);
        item_dialog_builder.setView(items_view);

        Button bt_serv_popsave = items_view.findViewById(R.id.bt_item_popsave);
        Button bt_serv_popcncl = items_view.findViewById(R.id.bt_item_popcncl);
        TextView tv_name_title = items_view.findViewById(R.id.tv_diag_title);
        TextInputEditText
                et_namediag = items_view.findViewById(R.id.et_item_popname),
                et_pricediag = items_view.findViewById(R.id.et_item_popprice);

        tv_name_title.setText(R.string.add_item);

        bt_serv_popsave.setOnClickListener(v -> {
            if (et_namediag.getText().toString().isEmpty() || et_namediag.getText().toString().equals("")){

                Toast.makeText(getApplicationContext(),"Enter Item Name", Toast.LENGTH_SHORT).show();

                //common.Mysnackbar(const_setup_layout,"Enter Item Name", Snackbar.LENGTH_SHORT).show();

            }else {
                int price = 0;

                if (!et_pricediag.getText().toString().isEmpty()){

                    try{
                       price = Integer.parseInt(et_pricediag.getText().toString());

                        adminDb.addSingle_item(et_setup_serv.getText().toString(),
                                new Basket_Items(et_namediag.getText().toString(),
                                        price));

                    }catch (Exception ignored){
                        Toast.makeText(getApplicationContext(),"Invalid Item Price", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    adminDb.addSingle_item(et_setup_serv.getText().toString(),
                            new Basket_Items(et_namediag.getText().toString(),
                                    price));
                }

                Toast.makeText(getApplicationContext(),
                        et_namediag.getText().toString().toUpperCase() + " successfully added", Toast.LENGTH_SHORT).show();
                load_item_list(et_setup_serv.getText().toString());
                et_namediag.setText("");
                et_pricediag.setText("");
            }
        });

        bt_serv_popcncl.setOnClickListener(v -> item_dialog_builder.dismiss());

        item_dialog_builder.show();

    }

    //build dialog for user to pick service
    void pick_service_dialog() {
        AlertDialog servlist_dialog = new AlertDialog.Builder(weakSetup.get()).create();

        View servlist_view = LayoutInflater.from(weakSetup.get())
                .inflate(R.layout.diag_service_picker, const_setup_layout, false);

        servlist_dialog.setCancelable(true);
        servlist_dialog.setView(servlist_view);

        RecyclerView recy_serv_list = servlist_view.findViewById(R.id.recy_diag_servlist);
        TextView tv_no_show = servlist_view.findViewById(R.id.tv_no_show);

        if (adminDb.service_names().size() > 0){
            recy_serv_list.setVisibility(View.VISIBLE);

            load_serv_diag_list(recy_serv_list, servlist_dialog);

        }else{
            tv_no_show.setVisibility(View.VISIBLE);
        }

        servlist_dialog.show();
    }

    void load_serv_diag_list(RecyclerView recyclerView, AlertDialog dialog){
        Services_recy_Adapter servicesRecyAdapter = new Services_recy_Adapter(adminDb.service_names(), dialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(weakSetup.get()));
        recyclerView.setAdapter(servicesRecyAdapter);
    }

    public void load_item_list(String servicename){

        ArrayList<Basket_Items> bskt_items = adminDb.items_list(servicename);

         if (bskt_items.size() > 0){
             tv_set_noshow.setVisibility(View.GONE);
             Items_recy_Adapter servicesRecyAdapter = new Items_recy_Adapter(bskt_items);
             recy_serv_items.setLayoutManager(new LinearLayoutManager(weakSetup.get()));
             recy_serv_items.setAdapter(servicesRecyAdapter);
         }else {
             tv_set_noshow.setVisibility(View.VISIBLE);
         }
    }

    public void set_serviceName(String service_name){
        et_setup_serv.setText(service_name);
    }

    public void add_item(View view) {
        if (!et_setup_serv.getText().toString().equals(getResources().getString(R.string.select_lbl))){
            add_item_dialog();
        }else {
            common.Mysnackbar(const_setup_layout,"Select Service",Snackbar.LENGTH_SHORT).show();
        }
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-DEFINED METHODS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

}
