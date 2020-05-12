package com.remu.POJO;

public class User {
    private String name, gender,  birthdate, about, image, LatLong, id;

    public User(){

    }

    public User(String id, String foto, String birthdate, String gender, String name){
        this.id = id;
        this.image = foto;
        this.birthdate = birthdate;
        this.gender = gender;
        this.name = name;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getTanggal() {
        return birthdate;
    }

    public String getDeskripsi() {
        return about;
    }

    public String getImage() {
        return image;
    }

    public String getLatLong() {
        return LatLong;
    }




}
