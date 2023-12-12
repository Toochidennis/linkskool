package com.digitaldream.winskool.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class NewsTable implements Serializable {
    @DatabaseField
    private int id;
    @DatabaseField
    private String newsId;
    @DatabaseField
    private String newsSubject;
    @DatabaseField
    private String newsContent;
    @DatabaseField
    private String datePosted;
    @DatabaseField
    private String newsImageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getNewsSubject() {
        return newsSubject;
    }

    public void setNewsSubject(String newsSubject) {
        this.newsSubject = newsSubject;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public String getNewsImageUrl() {
        return newsImageUrl;
    }

    public void setNewsImageUrl(String newsImageUrl) {
        this.newsImageUrl = newsImageUrl;
    }
}
