package com.example.chatme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // Start home activity
                    startActivity(new Intent(SplashActivity.this, UserActivity.class));
                } else {
                    // No user is signed in
                    // start login activity
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }

                // close splash activity
                finish();
            }

        }, 5000);

    }
}