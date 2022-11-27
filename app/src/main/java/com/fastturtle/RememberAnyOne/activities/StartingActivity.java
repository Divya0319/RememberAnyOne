package com.fastturtle.RememberAnyOne.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.fastturtle.RememberAnyOne.R;

public class StartingActivity extends AppCompatActivity {
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Thread t1 = new Thread() {
            public void run() {
                try {
                    sleep(3 * 1000);
                    Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
                    startActivity(i);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();
    }

}
