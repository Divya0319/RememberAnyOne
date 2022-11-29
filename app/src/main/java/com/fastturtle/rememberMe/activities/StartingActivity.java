package com.fastturtle.rememberMe.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fastturtle.rememberme.R;

public class StartingActivity extends AppCompatActivity {

    Animation zoomIn, slideIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

        getWindow().setStatusBarColor(Color.TRANSPARENT);

        zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        slideIn = AnimationUtils.loadAnimation(this, R.anim.slidein_bottom);

        ImageView appIcon = findViewById(R.id.imageViewIcon);
        TextView tvAppName = findViewById(R.id.tv_app_name);

        new Handler().postDelayed(() -> {

            appIcon.setVisibility(View.VISIBLE);
            tvAppName.setVisibility(View.VISIBLE);
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) appIcon.getDrawable();
            drawable.start();
            tvAppName.startAnimation(zoomIn);

        }, 500);

        new Handler().postDelayed(() -> {

            Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
            startActivity(i);
            finish();
        }, 4000);


    }

}
