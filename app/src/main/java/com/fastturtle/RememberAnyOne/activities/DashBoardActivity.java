package com.fastturtle.RememberAnyOne.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;

import com.fastturtle.RememberAnyOne.R;


public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatButton btAdd, btView;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        btAdd.setOnClickListener(this);
        btView.setOnClickListener(this);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissons) {
        if (context != null && permissons != null) {
            for (String permission : permissons) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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
