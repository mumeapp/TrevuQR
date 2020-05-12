package com.remu.POJO;

import android.graphics.drawable.Drawable;

public class Article {

    private String image;
    private String title;
    private String highlight;
    private String article;
    private String source;

    public Article(String image, String title, String article) {
        this.image = image;
        this.title = title;
        this.article = article;
        this.highlight = article.substring(0, 80) + "...";
    }
    public Article(){

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getHighlight() {
        return highlight;
    }

    public String getArticle() {
        return article;
    }
}
