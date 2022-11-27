package com.fastturtle.RememberAnyOne.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.splashscreen.SplashScreen;

import com.fastturtle.RememberAnyOne.R;


public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatButton btAdd, btView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        btAdd = findViewById(R.id.buttonAddUser);
        btView = findViewById(R.id.buttonViewUser);
        btAdd.setOnClickListener(this);
        btView.setOnClickListener(this);

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
        }
    }
}
