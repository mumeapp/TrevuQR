package com.remu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.remu.POJO.TextProcess;
import com.saber.chentianslideback.SlideBackActivity;

public class AddDictionary extends SlideBackActivity {

    public static final int ORIGIN_REQUEST_CODE = 0;
    public static final int DESTINATION_REQUEST_CODE = 1;

    private DatabaseReference database;

    private EditText etTextAwal;
    private EditText etTextTranslete;
    private ProgressDialog loading;

    private static final String LOG_TAG = "Record_Log";
    private boolean permissionToRecordAccepted = false;
    private String awal, akhir;
    private String textAwal;
    private String textAkhir;

    // select language
    private LinearLayout selectorOrigin, selectorDestination;
    private ImageView imageOrigin, imageDestination;
    private TextView languageOrigin, languageDestination;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dictionary);

        initializeUI();
        initializeClickListener();

        Animatoo.animateSlideDown(this);

        makeNewTranslation();

        setSlideBackDirection(SlideBackActivity.LEFT);

    }

    @Override
    protected void slideBackSuccess() {
        link();
    }

    private void submitUser(TextProcess requests, String child){
        database.child("Dictionary").child(child).push().setValue(requests).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loading.dismiss();

                etTextAwal.setText("");
                etTextTranslete.setText("");

                Toast.makeText(AddDictionary.this, "Input Successful", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void link(){
        Intent intent = new Intent(AddDictionary.this, DictionaryActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        link();
    }


    @Override
    public void finish() {
        super.finish();
        Animatoo.animateSlideUp(this);
    }

    private void initializeUI() {
        selectorOrigin = findViewById(R.id.selector_origin);
        selectorDestination = findViewById(R.id.selector_destination);
        imageOrigin = findViewById(R.id.img_origin);
        imageDestination = findViewById(R.id.img_destination);
        languageOrigin = findViewById(R.id.language_origin);
        languageDestination = findViewById(R.id.language_destination);
    }

    private void makeNewTranslation(){
        database = FirebaseDatabase.getInstance().getReference();

        awal = languageOrigin.getText().toString();
        akhir = languageDestination.getText().toString();

        final Button btn = findViewById(R.id.submitButton);
        etTextAwal = findViewById(R.id.textAwal);
        etTextTranslete = findViewById(R.id.textTranslete);

        btn.setOnClickListener(view -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String StextAwal = etTextAwal.getText().toString();
            String StextTranslete = etTextTranslete.getText().toString();
            String id = currentUser.getUid();

            if (StextAwal.equals("")){
                etTextAwal.setError("Field Tidak Boleh Kosong");
                etTextAwal.requestFocus();
            }
            else if (StextTranslete.equals("")){
                etTextTranslete.setError("Field Tidak Boleh Kosong");
                etTextTranslete.requestFocus();
            }
            else {
                loading = ProgressDialog.show(AddDictionary.this,
                        null,
                        "please wait...",
                        true,
                        false);

                String child = null;
                if ((awal.equalsIgnoreCase("japanese") && akhir.equalsIgnoreCase("Indonesian")) || (awal.equalsIgnoreCase("indonesian") && akhir.equalsIgnoreCase("japanese"))) {
                    child = "indonesia-jepang";
                    textAwal = "Jepang";
                    textAkhir = "Indonesia";
                    submitUser(new TextProcess(
                            StextAwal.toLowerCase(),
                            StextTranslete.toLowerCase(),textAwal,textAkhir,id),child);
                    link();
                } else if ((awal.equalsIgnoreCase("japanese") && akhir.equalsIgnoreCase("english")) || (awal.equalsIgnoreCase("english") && akhir.equalsIgnoreCase("japanese"))) {
                    child = "jepang-inggris";
                    textAwal = "Jepang";
                    textAkhir = "Inggris";
                    submitUser(new TextProcess(
                            StextAwal.toLowerCase(),
                            StextTranslete.toLowerCase(),textAwal,textAkhir,id),child);
                    link();
                } else if ((awal.equalsIgnoreCase("english")&&akhir.equalsIgnoreCase("Indonesian"))||(awal.equalsIgnoreCase("indonesian")&&akhir.equalsIgnoreCase("english"))) {
                    child = "inggris-indonesia";
                    textAwal = "Inggris";
                    textAkhir = "Indonesia";
                    submitUser(new TextProcess(
                            StextAwal.toLowerCase(),
                            StextTranslete.toLowerCase(),textAwal,textAkhir,id),child);
                    link();
                }else{
                    Toast.makeText(getApplicationContext(), "Tidak bisa menggunakan bahasa yang sama", Toast.LENGTH_LONG).show();
                    loading.dismiss();
                }

            }
        });
    }

    private void initializeClickListener() {
        selectorOrigin.setOnClickListener(v -> {
            String currentLanguage = languageOrigin.getText().toString();

            switch (currentLanguage) {
                case "Indonesian":
                    selectLanguageOrigin(ChooseLanguageActivity.INDONESIAN);
                    break;
                case "English":
                    selectLanguageOrigin(ChooseLanguageActivity.ENGLISH);
                    break;
                case "Japanese":
                    selectLanguageOrigin(ChooseLanguageActivity.JAPANESE);
                    break;
            }
        });
        selectorDestination.setOnClickListener(v -> {
            String currentLanguage = languageDestination.getText().toString();

            switch (currentLanguage) {
                case "Indonesian":
                    selectLanguageDestination(ChooseLanguageActivity.INDONESIAN);
                    break;
                case "English":
                    selectLanguageDestination(ChooseLanguageActivity.ENGLISH);
                    break;
                case "Japanese":
                    selectLanguageDestination(ChooseLanguageActivity.JAPANESE);
                    break;
            }
        });
    }

    private void selectLanguageOrigin(int currentLanguage) {
        Intent intent = new Intent(AddDictionary.this, ChooseLanguageActivity.class);
        intent.putExtra("language", currentLanguage);
        startActivityForResult(intent, ORIGIN_REQUEST_CODE);
    }

    private void selectLanguageDestination(int currentLanguage) {
        Intent intent = new Intent(AddDictionary.this, ChooseLanguageActivity.class);
        intent.putExtra("language", currentLanguage);
        startActivityForResult(intent, DESTINATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String languageSelected = data.getStringExtra("selected");
            switch (requestCode) {
                case ORIGIN_REQUEST_CODE:
                    languageOrigin.setText(languageSelected);

                    switch (languageSelected) {
                        case "Indonesian":
                            imageOrigin.setImageDrawable(getDrawable(R.drawable.indo_flag));
                            break;
                        case "English":
                            imageOrigin.setImageDrawable(getDrawable(R.drawable.usa_flag));
                            break;
                        case "Japanese":
                            imageOrigin.setImageDrawable(getDrawable(R.drawable.japan_flag));
                            break;
                    }

                    break;
                case DESTINATION_REQUEST_CODE:
                    languageDestination.setText(languageSelected);

                    switch (languageSelected) {
                        case "Indonesian":
                            imageDestination.setImageDrawable(getDrawable(R.drawable.indo_flag));
                            break;
                        case "English":
                            imageDestination.setImageDrawable(getDrawable(R.drawable.usa_flag));
                            break;
                        case "Japanese":
                            imageDestination.setImageDrawable(getDrawable(R.drawable.japan_flag));
                            break;
                    }

                    break;
            }
            makeNewTranslation();
        }
    }

}

