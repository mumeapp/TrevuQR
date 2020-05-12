package com.remu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.saber.chentianslideback.SlideBackActivity;

public class PrivacyPolicyActivity extends SlideBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        Animatoo.animateSlideLeft(this);

        setSlideBackDirection(SlideBackActivity.LEFT);
    }

    @Override
    protected void slideBackSuccess() { finish(); }

    @Override
    public void finish() {
        super.finish();
        Animatoo.animateSlideRight(this);
    }

}