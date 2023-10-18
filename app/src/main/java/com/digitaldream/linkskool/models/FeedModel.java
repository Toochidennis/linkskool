package com.digitaldream.linkskool.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class FeedModel implements Serializable {
    @DatabaseField
    private String title;
    @DatabaseField
    private String authorName;
    @DatabaseField
    private String date;
    @DatabaseField
    private String feedId;

    private String feedType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }
}
