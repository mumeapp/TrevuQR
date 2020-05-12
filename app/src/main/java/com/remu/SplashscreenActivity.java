package com.remu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.remu.Service.UpdateLocation;

public class SplashscreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        Intent service = new Intent(SplashscreenActivity.this, UpdateLocation.class);
        startService(service);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            FirebaseUser user = mAuth.getCurrentUser();

            if (sharedPreferences.getBoolean("isFirstTime", true)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirstTime", false).apply();

                Intent intent = new Intent(SplashscreenActivity.this, OnboardingActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (user != null) {
                    Intent intent = new Intent(SplashscreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashscreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
