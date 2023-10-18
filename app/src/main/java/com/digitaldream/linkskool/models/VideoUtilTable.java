package com.digitaldream.linkskool.models;

import com.j256.ormlite.field.DatabaseField;

public class VideoUtilTable {
    @DatabaseField
    private String videoCatId;
    @DatabaseField
    private String videoCatTitle;

    public String getVideoCatId() {
        return videoCatId;
    }

    public void setVideoCatId(String videoCatId) {
        this.videoCatId = videoCatId;
    }

    public String getVideoCatTitle() {
        return videoCatTitle;
    }

    public void setVideoCatTitle(String videoCatTitle) {
        this.videoCatTitle = videoCatTitle;
    }

}
