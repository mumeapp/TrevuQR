package com.remu.POJO;

import android.graphics.drawable.Drawable;

public class Tips {

    private Drawable image;
    private String title;
    private String tips;

    public Tips(Drawable image, String title, String tips) {
        this.image = image;
        this.title = title;
        this.tips = tips;
    }

    public Drawable getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getTips() {
        return tips;
    }
}
