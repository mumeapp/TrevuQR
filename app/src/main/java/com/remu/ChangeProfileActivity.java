package com.remu;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.saber.chentianslideback.SlideBackActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

public class ChangeProfileActivity extends SlideBackActivity {

    private final int PICK_IMAGE_REQUEST = 71;
    private TextView saveButton;
    private TextInputEditText name, dateOfBirth, about;
    private AutoCompleteTextView genderSpinner;
    private ImageView profilePicture;
    private Uri filePath;

    private boolean isSaveButtonDisabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        initializeUI();
        Animatoo.animateSlideLeft(this);

        Glide.with(ChangeProfileActivity.this)
                .load(Uri.parse(getIntent().getStringExtra("image")))
                .centerCrop()
                .placeholder(R.drawable.ic_default_avatar)
                .into(profilePicture);

        name.setText(getIntent().getStringExtra("name"));
        dateOfBirth.setText(getIntent().getStringExtra("birthdate"));
        genderSpinner.setText(getIntent().getStringExtra("gender"));
        about.setText(getIntent().getStringExtra("about"));

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setTextColor(getResources().getColor(R.color.trevuMidPink));
                isSaveButtonDisabled = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        genderSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setTextColor(getResources().getColor(R.color.trevuMidPink));
                isSaveButtonDisabled = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dateOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setTextColor(getResources().getColor(R.color.trevuMidPink));
                isSaveButtonDisabled = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        about.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setTextColor(getResources().getColor(R.color.trevuMidPink));
                isSaveButtonDisabled = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dateOfBirth.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        switch (monthOfYear) {
                            case 0:
                                dateOfBirth.setText(dayOfMonth + " " + "January" + " " + year1);
                                break;
                            case 1:
                                dateOfBirth.setText(dayOfMonth + " " + "February" + " " + year1);
                                break;
                            case 2:
                                dateOfBirth.setText(dayOfMonth + " " + "March" + " " + year1);
                                break;
                            case 3:
                                dateOfBirth.setText(dayOfMonth + " " + "April" + " " + year1);
                                break;
                            case 4:
                                dateOfBirth.setText(dayOfMonth + " " + "May" + " " + year1);
                                break;
                            case 5:
                                dateOfBirth.setText(dayOfMonth + " " + "June" + " " + year1);
                                break;
                            case 6:
                                dateOfBirth.setText(dayOfMonth + " " + "July" + " " + year1);
                                break;
                            case 7:
                                dateOfBirth.setText(dayOfMonth + " " + "August" + " " + year1);
                                break;
                            case 8:
                                dateOfBirth.setText(dayOfMonth + " " + "September" + " " + year1);
                                break;
                            case 9:
                                dateOfBirth.setText(dayOfMonth + " " + "October" + " " + year1);
                                break;
                            case 10:
                                dateOfBirth.setText(dayOfMonth + " " + "November" + " " + year1);
                                break;
                            case 11:
                                dateOfBirth.setText(dayOfMonth + " " + "December" + " " + year1);
                                break;
                        }
                    }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(new GregorianCalendar(year - 10, month, day).getTimeInMillis());
            datePickerDialog.show();
        });

        String[] arrayOfGender = new String[]{"Male", "Female"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.adapter_gender_edit_profile, arrayOfGender);
        genderSpinner.setAdapter(arrayAdapter);

        profilePicture.setOnClickListener(view -> {
            chooseImage();
            saveButton.setTextColor(getResources().getColor(R.color.trevuMidPink));
            isSaveButtonDisabled = false;
        });

        saveButton.setOnClickListener(v -> {
            if (!isSaveButtonDisabled) {
                uploadImage();
            }
        });

        setSlideBackDirection(SlideBackActivity.LEFT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            Glide.with(ChangeProfileActivity.this)
                    .load(filePath)
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(profilePicture);
        }
    }

    @Override
    protected void slideBackSuccess() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        Animatoo.animateSlideRight(this);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Profile").child(FirebaseAuth.getInstance().getUid());
        databaseReference.child("name").setValue(Objects.requireNonNull(name.getText()).toString()).addOnSuccessListener(aVoid -> {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();
            user.updateProfile(profileUpdates);
            databaseReference.child("id").setValue(FirebaseAuth.getInstance().getUid()).addOnSuccessListener(aVoid1 ->
                    databaseReference.child("gender").setValue(genderSpinner.getText().toString()).addOnSuccessListener(aVoid11 ->
                            databaseReference.child("birthdate").setValue(Objects.requireNonNull(dateOfBirth.getText()).toString()).addOnSuccessListener(aVoid111 ->
                                    databaseReference.child("about").setValue(Objects.requireNonNull(about.getText()).toString()).addOnSuccessListener(aVoid1111 -> {
                                        if (filePath == null) {
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    }))));
        });
        if (filePath != null) {
            assert user != null;
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("user").child(user.getUid());
            ref.putFile(filePath).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return ref.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    databaseReference.child("image").setValue(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).toString()));
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(Objects.requireNonNull(task.getResult())).build();
                    user.updateProfile(profileUpdates);
                    progressDialog.dismiss();
                    finish();
                }
            });
        }
    }

    private void initializeUI() {
        saveButton = findViewById(R.id.ep_save_info);
        name = findViewById(R.id.ep_name);
        dateOfBirth = findViewById(R.id.ep_dateofbirth);
        genderSpinner = findViewById(R.id.ep_gender);
        about = findViewById(R.id.ep_about);
        profilePicture = findViewById(R.id.profile_picture);
        isSaveButtonDisabled = true;
    }

}
