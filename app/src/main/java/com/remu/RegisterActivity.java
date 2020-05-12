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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.remu.Service.UpdateLocation;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private TextInputEditText registerUsername, registerEmail, registerPassword, registerConfirmPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference; //1
    Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent service = new Intent(RegisterActivity.this, UpdateLocation.class);
        stopService(service);
        startService(service);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        initializeUI();
        //updateUI();
        Animatoo.animateSlideLeft(this);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> registerNewUser());
    }

    private void registerNewUser() {
        String email, password, confirmPassword;
        TextInputLayout usernameLayout = findViewById(R.id.input_layout_register_username);
        usernameLayout.setError(null);
        TextInputLayout emailLayout = findViewById(R.id.input_layout_register_email);
        emailLayout.setError(null);
        TextInputLayout passwordLayout = findViewById(R.id.input_layout_register_password);
        passwordLayout.setError(null);
        TextInputLayout confirmPasswordLayout = findViewById(R.id.input_layout_register_confirm);
        confirmPasswordLayout.setError(null);

        boolean isError = false;
        email = Objects.requireNonNull(registerEmail.getText()).toString();
        password = Objects.requireNonNull(registerPassword.getText()).toString();
        confirmPassword = Objects.requireNonNull(registerConfirmPassword.getText()).toString();

        if (registerUsername.getText().length() == 0) {
            usernameLayout.setError("USername is required");
            isError = true;
        }
        if (registerEmail.getText().length() == 0) {
            emailLayout.setError("Email is required");
            isError = true;
        }
        if (registerPassword.getText().length() == 0) {
            passwordLayout.setError("Password is required");
            isError = true;
        }
        if (registerConfirmPassword.getText().length() == 0) {
            confirmPasswordLayout.setError("Confirm your password");
            isError = true;
        } else if (registerPassword.getText().length() != 0 && !registerPassword.getText().toString().equals(
            registerConfirmPassword.getText().toString()
        )) {
            passwordLayout.setError("Password must be same");
            confirmPasswordLayout.setError("Password must be same");
            registerPassword.setText("");
            registerConfirmPassword.setText("");
            isError = true;
        }
        if (isError) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateName(user);

                        String userID = user.getUid();

                        databaseReference= FirebaseDatabase.getInstance().getReference().child("User Location").child(userID).child("status");
                        databaseReference.setValue(true);
                        try {
                            SharedPreferences privacyPreference =
                                    getSharedPreferences("privacy", MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = privacyPreference.edit();
                            prefsEditor.putBoolean("searchable", true);
                            prefsEditor.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent service = new Intent(RegisterActivity.this, UpdateLocation.class);
                        stopService(service);
                        startService(service);
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_LONG).show();
                    }

                });
    }

    public void loginClicked(View view) {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        Animatoo.animateSlideRight(this);
    }

    private void initializeUI() {
        registerUsername = findViewById(R.id.register_username);
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        registerConfirmPassword = findViewById(R.id.register_confirm_password);
    }

    private void updateName(FirebaseUser user) {
        String email = registerEmail.getText().toString();
        int index = email.indexOf('@');
        String defaultName = email.substring(0, index);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(defaultName).build();
        user.updateProfile(profileUpdates);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(), "Please login!", Toast.LENGTH_LONG).show();
        }
    }


}
