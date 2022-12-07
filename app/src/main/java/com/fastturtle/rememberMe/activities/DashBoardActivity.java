package com.fastturtle.rememberMe.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.fastturtle.rememberMe.R;
import com.google.firebase.auth.FirebaseAuth;


public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatButton btAdd, btView;

    FirebaseAuth firebaseAuth;
    AppCompatButton btSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getWindow().setStatusBarColor(Color.parseColor("#3DA2C4"));
        btAdd = findViewById(R.id.buttonAddUser);

        firebaseAuth = FirebaseAuth.getInstance();
        btView = findViewById(R.id.buttonViewUser);
        btSignOut = findViewById(R.id.buttonSignOut);
        btAdd.setOnClickListener(this);
        btView.setOnClickListener(this);
        btSignOut.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAddUser:
                Intent i = new Intent(getApplicationContext(), AddUserActivity.class);
                startActivity(i);
                break;
            case R.id.buttonViewUser:
                i = new Intent(getApplicationContext(), AllUsersListActivity.class);
                startActivity(i);
                break;
            case R.id.buttonSignOut:

                AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardActivity.this)
                        .setTitle("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialogInterface, i1) -> {
                            firebaseAuth.signOut();
                            Intent phoneIntent = new Intent(getApplicationContext(), PhoneActivity.class);
                            startActivity(phoneIntent);
                            finish();
                        }).setNegativeButton("No", (dialogInterface, i12) -> {
                            dialogInterface.dismiss();
                        });
                builder.show();

        }
    }
}
