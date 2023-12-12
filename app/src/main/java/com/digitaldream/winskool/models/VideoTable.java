package com.digitaldream.winskool.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class VideoTable implements Serializable {

    @DatabaseField
    private String videoId;
    @DatabaseField
    private String videoTitle;
    @DatabaseField
    private String videoSubject;
    @DatabaseField
    private String thumbnail;
    @DatabaseField
    private String videoUrl;
    @DatabaseField
    private String levelId;
    @DatabaseField
    private String levelName;
    @DatabaseField
    private String videoCategory;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public void setVideoSubject(String videoSubject) {
        this.videoSubject = videoSubject;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoSubject() {
        return videoSubject;
    }

    public String getThumbnailUrl() {
        return thumbnail;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(String videoCategory) {
        this.videoCategory = videoCategory;
    }
}
