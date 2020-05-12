package com.remu;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.saber.chentianslideback.SlideBackActivity;

public class ChooseLanguageActivity extends SlideBackActivity {

    public static final int INDONESIAN = 0;
    public static final int ENGLISH = 1;
    public static final int JAPANESE = 2;

    public static String test = "test";
    private CardView cardIndo, cardEnglish, cardJapanese;
    private ImageView checkIndo, checkEnglish, checkJapanese;
    private String highlighted;
    private Button submitLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);

        initializeUI();
        Animatoo.animateSlideDown(this);

        Intent intent = getIntent();
        highlightSelected(intent.getIntExtra("language", 0));

        initializeClickListener();

        setSlideBackDirection(SlideBackActivity.LEFT);
    }

    @Override
    protected void slideBackSuccess() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        Animatoo.animateSlideUp(this);
    }

    private void initializeUI() {
        cardIndo = findViewById(R.id.card_indo);
        cardEnglish = findViewById(R.id.card_usa);
        cardJapanese = findViewById(R.id.card_japan);
        checkIndo = findViewById(R.id.check_indo);
        checkEnglish = findViewById(R.id.check_usa);
        checkJapanese= findViewById(R.id.check_japan);
        submitLanguage = findViewById(R.id.submit_language);
    }

    private void initializeClickListener() {
        cardIndo.setOnClickListener(v -> {
            highlightSelected(INDONESIAN);
        });
        cardEnglish.setOnClickListener(v -> {
            highlightSelected(ENGLISH);
        });
        cardJapanese.setOnClickListener(v -> {
            highlightSelected(JAPANESE);
        });
        submitLanguage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("selected", highlighted);

            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void highlightSelected(int language) {
        switch (language) {
            case INDONESIAN:
                cardIndo.setCardElevation(getPixelFromDp(10, this));
                cardEnglish.setCardElevation(getPixelFromDp(5, this));
                cardJapanese.setCardElevation(getPixelFromDp(5, this));
                checkIndo.setVisibility(View.VISIBLE);
                checkEnglish.setVisibility(View.INVISIBLE);
                checkJapanese.setVisibility(View.INVISIBLE);
                highlighted = "Indonesian";
                break;
            case ENGLISH:
                cardIndo.setCardElevation(getPixelFromDp(5, this));
                cardEnglish.setCardElevation(getPixelFromDp(10, this));
                cardJapanese.setCardElevation(getPixelFromDp(5, this));
                checkIndo.setVisibility(View.INVISIBLE);
                checkEnglish.setVisibility(View.VISIBLE);
                checkJapanese.setVisibility(View.INVISIBLE);
                highlighted = "English";
                break;
            case JAPANESE:
                cardIndo.setCardElevation(getPixelFromDp(5, this));
                cardEnglish.setCardElevation(getPixelFromDp(5, this));
                cardJapanese.setCardElevation(getPixelFromDp(10, this));
                checkIndo.setVisibility(View.INVISIBLE);
                checkEnglish.setVisibility(View.INVISIBLE);
                checkJapanese.setVisibility(View.VISIBLE);
                highlighted = "Japanese";
                break;
        }
    }

    public float getPixelFromDp(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }
}
