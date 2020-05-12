package com.remu.POJO;

import java.util.ArrayList;

public class Weighting {

    public ArrayList<Double> doWeighting(double latitude, double longitude, ArrayList<PlaceModel> item) {

        double weightDistance = -0.3, weightGoogleRate = 0.167, weightTrevuRate = 0.25, weightIntensity = 0.25;
        double pembagi = 0;
        int count = 0;
        ArrayList<Double> s = new ArrayList<>();
        ArrayList<Double> v = new ArrayList<>();

        for (PlaceModel place : item) {
            double lat = place.getPlaceLocation().latitude;
            double lng = place.getPlaceLocation().longitude;
            System.out.println("intensity" + place.getPlaceIntensity());
            if (place.getPlaceIntensity() != 0 && place.getTrevuRating() != 0) {
                s.add(Math.pow(Distance.distance(lat, latitude, lng, longitude), weightDistance) *
                        Math.pow(place.getPlaceRating(), weightGoogleRate) *
                        Math.pow(place.getTrevuRating(), weightTrevuRate) *
                        Math.pow(place.getPlaceIntensity(), weightIntensity));

            }
            else if (place.getPlaceIntensity()!=0){
                s.add(Math.pow(Distance.distance(lat, latitude, lng, longitude), weightDistance) *
                        Math.pow(place.getPlaceRating(), weightGoogleRate) *
                        Math.pow(1, weightTrevuRate) *
                        Math.pow(place.getPlaceIntensity(), weightIntensity));
            }
            else if (place.getTrevuRating()!=0){
                s.add(Math.pow(Distance.distance(lat, latitude, lng, longitude), weightDistance) *
                        Math.pow(place.getPlaceRating(), weightGoogleRate) *
                        Math.pow(place.getTrevuRating(), weightTrevuRate) *
                        Math.pow(1, weightIntensity));
            }
            else {
                s.add(Math.pow(Distance.distance(lat, latitude, lng, longitude), weightDistance) *
                        Math.pow(place.getPlaceRating(), weightGoogleRate) *
                        Math.pow(1, weightTrevuRate) *
                        Math.pow(1, weightIntensity));
            }
            pembagi += s.get(count);
            count++;
        }

        for (Double vectorS : s) {
            v.add(vectorS / pembagi);
        }

        return v;
    }
}
