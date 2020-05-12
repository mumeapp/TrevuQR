package com.remu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.remu.Service.UpdateLocation;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    Button loginButton;
    private FirebaseAuth mAuth;
    private TextInputEditText loginEmail, loginPassword;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent service = new Intent(LoginActivity.this, UpdateLocation.class);
        stopService(service);
        startService(service);
        initializeUI();
        Animatoo.animateSlideLeft(this);

        mAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> signIn());
    }

    public void registerClicked(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            finish();
        }
    }

    public void signIn() {
        String email, password;
        TextInputLayout emailLayout = findViewById(R.id.input_layout_login_email);
        emailLayout.setError(null);
        TextInputLayout passwordLayout = findViewById(R.id.input_layout_login_password);
        passwordLayout.setError(null);
        boolean isError = false;
        email = Objects.requireNonNull(loginEmail.getText()).toString();
        password = Objects.requireNonNull(loginPassword.getText()).toString();

        if (loginEmail.getText().length() == 0) {
            emailLayout.setError("Email is required");
            isError = true;
        }
        if (loginPassword.getText().length() == 0) {
            passwordLayout.setError("Password is required");
            isError = true;
        }
        if (isError) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        databaseReference.child(mAuth.getUid()).child("status").setValue(true);
                        try {
                            SharedPreferences privacyPreference =
                                    getSharedPreferences("privacy", MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = privacyPreference.edit();
                            prefsEditor.putBoolean("searchable", true);
                            prefsEditor.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent service = new Intent(LoginActivity.this, UpdateLocation.class);
                        stopService(service);
                        startService(service);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                        loginPassword.setText("");
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        Animatoo.animateSlideRight(this);
    }

    private void initializeUI() {
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User Location");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
