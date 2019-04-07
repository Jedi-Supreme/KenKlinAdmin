package com.jedit.kenklinadmin.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.jedit.kenklinadmin.R;
import com.jedit.kenklinadmin.common_code;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText et_login_email, et_login_password;
    ProgressBar probar_login;

    //===========================================ON CREATE==========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_login_email = findViewById(R.id.et_login_email);
        et_login_password = findViewById(R.id.et_login_password);
        probar_login = findViewById(R.id.probar_login);

        TextInputLayout inpt_login_pass = findViewById(R.id.input_login_password);

        et_login_password.setOnFocusChangeListener((v, hasFocus) -> inpt_login_pass.setPasswordVisibilityToggleEnabled(hasFocus));

    }
    //===========================================ON CREATE==========================================

    void testInputs() {

        if (et_login_email.getText().toString().isEmpty() || et_login_email.getText().toString().equals("")) {
            common_code.Mysnackbar(findViewById(R.id.admin_login_layout), "Enter Valid Email", Snackbar.LENGTH_SHORT).show();;

        } else if (et_login_password.getText().toString().isEmpty() || et_login_password.getText().toString().equals("")) {
            common_code.Mysnackbar(findViewById(R.id.admin_login_layout), "Enter Password", Snackbar.LENGTH_SHORT).show();

        } else {
            login_with_credentials(et_login_email.getText().toString(), et_login_password.getText().toString());


        }


    }

    void login_with_credentials(final String email, String password) {

        probar_login.setVisibility(View.VISIBLE);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        toDashboard();
                        probar_login.setVisibility(View.INVISIBLE);
                        probar_login.clearAnimation();

                    } else {

                        // If sign in fails, display a message to the user.
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {

                                common_code.Mysnackbar(findViewById(R.id.admin_login_layout), "Invalid Email Address",
                                        Snackbar.LENGTH_SHORT).show();

                            probar_login.setVisibility(View.INVISIBLE);
                            probar_login.clearAnimation();

                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            common_code.Mysnackbar(findViewById(R.id.admin_login_layout), "Wrong Password",
                                    Snackbar.LENGTH_SHORT).show();

                            probar_login.setVisibility(View.INVISIBLE);
                            probar_login.clearAnimation();
                        }


                    }

                });

    }

    private void toDashboard() {
        Intent dash_intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(dash_intent);
        finish();
    }

    public void admin_login(View view) {
        testInputs();
    }
}
