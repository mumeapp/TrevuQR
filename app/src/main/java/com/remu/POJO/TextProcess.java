package com.remu.POJO;

public class TextProcess {
    private String textAwal;
    private String textTranslete;
    private String bhsawal;
    private String bhsTranslete;
    private String id;
    private static String key;



    public TextProcess() {
    }

    public TextProcess(String textAwal, String textTranslete, String bhsawal, String bhsTranslete, String id) {
        this.textAwal = textAwal;
        this.textTranslete = textTranslete;
        this.bhsawal = bhsawal;
        this.bhsTranslete = bhsTranslete;
        this.id = id;
    }

    public String getBhsawal() {
        return bhsawal;
    }

    public void setBhsawal(String bhsawal) {
        this.bhsawal = bhsawal;
    }

    public String getBhsTranslete() {
        return bhsTranslete;
    }

    public void setBhsTranslete(String bhsTranslete) {
        this.bhsTranslete = bhsTranslete;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextAwal() {
        return textAwal;
    }

    public void setTextAwal(String textAwal) {
        this.textAwal = textAwal;
    }

    public String getTextTranslete() {
        return textTranslete;
    }

    public void setTextTranslete(String textTranslete) {
        this.textTranslete = textTranslete;
    }

    public static String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString(){
        return textAwal +"$"+ textTranslete+"$"+bhsawal+"$"+bhsTranslete;
    }
}
