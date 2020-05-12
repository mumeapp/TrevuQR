package com.remu.POJO;

import com.google.android.gms.maps.model.LatLng;

public class PlaceModel {

    private String placeId;
    private String placeName;
    private String placeAddress;
    private double placeRating;
    private double trevuRating;
    private LatLng placeLocation;
    private double placeWeight;
    private String placePhotoUri;
    private int placeIntensity;

    public PlaceModel(String placeId, String placeName, String placeAddress, double placeRating, LatLng placeLocation) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeRating = placeRating;
        this.placeLocation = placeLocation;
    }


    public PlaceModel(String placeId, String placeName, String placeAddress, double placeRating, LatLng placeLocation, String placePhotoUri) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeRating = placeRating;
        this.placeLocation = placeLocation;
        this.placePhotoUri = placePhotoUri;

    }

    public double getTrevuRating() {
        return trevuRating;
    }

    public void setTrevuRating(double trevuRating) {
        this.trevuRating = trevuRating;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public double getPlaceRating() {
        return placeRating;
    }

    public LatLng getPlaceLocation() {
        return placeLocation;
    }

    public String getPlacePhotoUri() {
        return placePhotoUri;
    }

    public void setPlaceWeight(double weight) {
        this.placeWeight = weight;
    }

    public double getPlaceWeight() {
        return placeWeight;
    }

    public int getPlaceIntensity() {
        return placeIntensity;
    }

    public void setPlaceIntensity(int placeIntensity) {
        this.placeIntensity = placeIntensity;
    }
}

