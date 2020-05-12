package com.remu.POJO;


import com.google.android.libraries.places.api.model.PhotoMetadata;

public class SavedFoodBeveragesTour {
      private String id, latlong, title;
      private Double rating;


    public String getId() {
        return id;
    }

    public String getLatlong() {
        return latlong;
    }

    public Double getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }
}
