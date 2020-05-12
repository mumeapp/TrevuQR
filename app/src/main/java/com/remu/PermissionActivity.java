package com.remu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class PermissionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Dexter.withActivity(PermissionActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            Intent intent2 = new Intent(context, MainActivity.class);
                            startActivity(intent2);
                            finish();
                        } else {
                            Intent intent2 = new Intent(context, LoginActivity.class);
                            startActivity(intent2);
                            finish();
                        }
                        finish();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
                            builder.setTitle("Permission denied!").setMessage("Location access is permanenetly denied, please set to ALLOW before using real-time feature.")
                                    .setNegativeButton("Cancel", null)
                                    .setPositiveButton("Okay!", (dialogInterface, i) -> {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                                    }).show();
                        } else {
                            Toast.makeText(PermissionActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();

    }
}
