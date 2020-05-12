package com.remu.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.remu.BuildConfig;
import com.remu.ChangeProfileActivity;
import com.remu.HelpCenterActivity;
import com.remu.LoginActivity;
import com.remu.PrivacyPolicyActivity;
import com.remu.R;
import com.remu.Service.UpdateLocation;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private ImageView profilePicture;
    private TextView profileName, profileId, versionId;
    private LinearLayout changeProfile, privacyPolicy, helpCenter;
    private Switch searchable;
    private Button signOutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeUI(root);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = FirebaseAuth.getInstance().getUid();
        final String[] gender = new String[1];
        final String[] birthdate = new String[1];
        final String[] about = new String[1];
        if (currentUser != null) {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Profile").child(FirebaseAuth.getInstance().getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child("image").exists()) {
                        new Handler().postDelayed(() -> Glide.with(ProfileFragment.this)
                                .load(dataSnapshot.child("image").getValue().toString())
                                .placeholder(R.drawable.ic_default_avatar)
                                .into(profilePicture), 200);
                    }
                    if (dataSnapshot.child("gender").exists()) {
                        gender[0] = dataSnapshot.child("gender").getValue().toString();
                    } else {
                        gender[0] = "";
                    }

                    if (dataSnapshot.child("birthdate").exists()) {
                        birthdate[0] = dataSnapshot.child("birthdate").getValue().toString();
                    } else {
                        birthdate[0] = "";
                    }

                    if (dataSnapshot.child("about").exists()) {
                        about[0] = dataSnapshot.child("about").getValue().toString();
                    } else {
                        about[0] = "";
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            String name = currentUser.getDisplayName();
            profileName.setText(name);

            String id = "ID: " + FirebaseAuth.getInstance().getUid();
            profileId.setText(id);
        }

        changeProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileFragment.super.getContext(), ChangeProfileActivity.class);
            if (currentUser != null) {
                intent.putExtra("name", currentUser.getDisplayName());

                if (currentUser.getPhotoUrl() != null) {
                    intent.putExtra("image", currentUser.getPhotoUrl().toString());
                } else {
                    intent.putExtra("image", "");
                }

                intent.putExtra("birthdate", birthdate[0]);
                intent.putExtra("gender", gender[0]);
                intent.putExtra("about", about[0]);
            }
            startActivity(intent);
        });

        if (getActivity().getSharedPreferences("privacy", MODE_PRIVATE).contains("searchable")) {
            searchable.setChecked(getActivity().getSharedPreferences("privacy", MODE_PRIVATE).getBoolean("searchable", true));
        } else {
            searchable.setChecked(true);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User Location").child(userId);

        databaseReference.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue().equals(true)) {
                        searchable.setChecked(true);
                        try {
                            SharedPreferences privacyPreference = getActivity()
                                    .getSharedPreferences("privacy", MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = privacyPreference.edit();
                            prefsEditor.putBoolean("searchable", true);
                            prefsEditor.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        searchable.setChecked(false);
                        try {
                            SharedPreferences privacyPreference = getActivity()
                                    .getSharedPreferences("privacy", MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = privacyPreference.edit();
                            prefsEditor.putBoolean("searchable", false);
                            prefsEditor.apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (NullPointerException np) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        searchable.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            try {
                SharedPreferences privacyPreference = getActivity()
                        .getSharedPreferences("privacy", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = privacyPreference.edit();
                prefsEditor.putBoolean("searchable", isChecked);
                prefsEditor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (getActivity().getSharedPreferences("privacy", MODE_PRIVATE).getBoolean("searchable", true)) {
                databaseReference.child("status").setValue(true);
            } else {
                databaseReference.child("status").setValue(false);
            }
        }));


        privacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileFragment.super.getContext(), PrivacyPolicyActivity.class);
            startActivity(intent);
        });

        helpCenter.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileFragment.super.getContext(), HelpCenterActivity.class);
            startActivity(intent);
        });

        String versionName = "Version " + BuildConfig.VERSION_NAME + " (Beta)";
        versionId.setText(versionName);

        signOutButton.setOnClickListener(v -> {
            Intent stopService = new Intent(ProfileFragment.super.getContext(), UpdateLocation.class);
            getActivity().stopService(stopService);
            FirebaseAuth.getInstance().signOut();
            Intent login = new Intent(ProfileFragment.super.getContext(), LoginActivity.class);
            startActivity(login);
            getActivity().finish();
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        profileName.setText(name);
    }

    private void initializeUI(View root) {
        profilePicture = root.findViewById(R.id.profile_image);
        profileName = root.findViewById(R.id.profile_name);
        profileId = root.findViewById(R.id.profile_id);
        changeProfile = root.findViewById(R.id.change_profile);
        searchable = root.findViewById(R.id.searchable);
        privacyPolicy = root.findViewById(R.id.privacy_policy);
        helpCenter = root.findViewById(R.id.help_center);
        versionId = root.findViewById(R.id.version_id);
        signOutButton = root.findViewById(R.id.sign_out_button);
    }

}
