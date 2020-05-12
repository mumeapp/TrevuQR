package com.remu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.remu.POJO.TextProcess;
import com.saber.chentianslideback.SlideBackActivity;

public class DictionaryActivity extends SlideBackActivity {

    public static final int ORIGIN_REQUEST_CODE = 0;
    public static final int DESTINATION_REQUEST_CODE = 1;

    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<TextProcess, TextProcessViewHolder> firebaseRecyclerAdapter;
    private LinearLayout selectorOrigin, selectorDestination;
    private ImageView imageOrigin, imageDestination;
    private TextView languageOrigin, languageDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        initializeUI();
        initializeClickListener();

        Animatoo.animateSlideLeft(this);

        showDictionary();
        Button btn1 = findViewById(R.id.ButtonAdd);
        btn1.setOnClickListener(view -> buttonAdd());

        setSlideBackDirection(SlideBackActivity.LEFT);
    }

    @Override
    protected void slideBackSuccess() {
        finish();
    }

    private void initializeUI() {
        selectorOrigin = findViewById(R.id.selector_origin);
        selectorDestination = findViewById(R.id.selector_destination);
        imageOrigin = findViewById(R.id.img_origin);
        imageDestination = findViewById(R.id.img_destination);
        languageOrigin = findViewById(R.id.language_origin);
        languageDestination = findViewById(R.id.language_destination);
    }

    private void showDictionary() {
        RecyclerView rvDictionary = findViewById(R.id.rv_listDictionary);
        rvDictionary.setLayoutManager(new LinearLayoutManager(DictionaryActivity.this, LinearLayoutManager.VERTICAL, false));

        if ((languageOrigin.getText().toString().equals("Indonesian") && languageDestination.getText().toString().equals("Japanese")) || (languageDestination.getText().toString().equals("Indonesian") && languageOrigin.getText().toString().equals("Japanese"))) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Dictionary").child("indonesia-jepang");
        } else if ((languageOrigin.getText().toString().equals("Indonesian") && languageDestination.getText().toString().equals("English")) || (languageDestination.getText().toString().equals("Indonesian") && languageOrigin.getText().toString().equals("English"))) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Dictionary").child("inggris-indonesia");
        } else if ((languageOrigin.getText().toString().equals("English") && languageDestination.getText().toString().equals("Japanese")) || (languageDestination.getText().toString().equals("English") && languageOrigin.getText().toString().equals("Japanese"))) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Dictionary").child("jepang-inggris");
        } else {
            Toast.makeText(getApplicationContext(), "Cant pick same language", Toast.LENGTH_LONG).show();
        }
        Query query = databaseReference.orderByKey();

        FirebaseRecyclerOptions<TextProcess> options = new FirebaseRecyclerOptions.Builder<TextProcess>()
                .setQuery(query, TextProcess.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TextProcess, TextProcessViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TextProcessViewHolder textProcessViewHolder, int i, @NonNull TextProcess textProcess) {
                textProcessViewHolder.setText1(textProcess.getTextAwal());
                textProcessViewHolder.setText2(textProcess.getTextTranslete());

            }

            @NonNull
            @Override
            public TextProcessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dictionary_result, parent, false);

                return new TextProcessViewHolder(view);
            }
        };

        rvDictionary.setAdapter(firebaseRecyclerAdapter);
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
        Intent intent = new Intent(DictionaryActivity.this, ChooseLanguageActivity.class);
        intent.putExtra("language", currentLanguage);
        startActivityForResult(intent, ORIGIN_REQUEST_CODE);
    }

    private void selectLanguageDestination(int currentLanguage) {
        Intent intent = new Intent(DictionaryActivity.this, ChooseLanguageActivity.class);
        intent.putExtra("language", currentLanguage);
        startActivityForResult(intent, DESTINATION_REQUEST_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        firebaseRecyclerAdapter.stopListening();
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
            showDictionary();
        }
    }

    class TextProcessViewHolder extends RecyclerView.ViewHolder {

        TextView text1;
        LinearLayout seeTranslation;
        TextView text2;

        TextProcessViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.awal);
            text2 = itemView.findViewById(R.id.translete);
            seeTranslation = itemView.findViewById(R.id.see_translation);

            seeTranslation.setOnClickListener(view -> {
                ImageView arrow = seeTranslation.findViewById(R.id.arrow_see_translation);
                arrow.setRotation(arrow.getRotation() + 180);

                if (text2.getVisibility() == View.GONE) {
                    text2.setVisibility(View.VISIBLE);
                } else if (text2.getVisibility() == View.VISIBLE) {
                    text2.setVisibility(View.GONE);
                }
            });
        }

        void setText1(String text) {
            text1.setText(text);
        }

        void setText2(String text) {
            text2.setText(text);
        }

    }

    @Override
    public void finish() {
        super.finish();
        Animatoo.animateSlideRight(this);
    }

    private void buttonAdd() {
        Intent intent = new Intent(DictionaryActivity.this, AddDictionary.class);
        startActivity(intent);
        finish();
    }
}
