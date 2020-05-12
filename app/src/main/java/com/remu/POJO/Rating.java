package com.remu.POJO;

public class Rating {
    private String namaUser, review, rating, placeId, placeName, idUser;

    public Rating(){

    }
    public Rating(String idUser, String namaUser,  String review, String rating, String placeId, String placeName){
        this.namaUser = namaUser;
        this.idUser = idUser;
        this.review = review;
        this.rating = rating;
        this.placeId = placeId;
        this.placeName = placeName;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
