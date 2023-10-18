package com.digitaldream.linkskool.models;

import com.j256.ormlite.field.DatabaseField;

public class CourseOutlineTable {
    @DatabaseField
    private String week;
    @DatabaseField
    private String title;
    @DatabaseField
    private String objective;
    @DatabaseField
    private String levelId;
    @DatabaseField
    private String courseId;
    @DatabaseField
    private String noteMaterialPath;
    @DatabaseField
    private String otherMatherialPath;
    @DatabaseField
    private String id;
    @DatabaseField
    private String levelName;
    @DatabaseField
    private String courseName;
    @DatabaseField
    private String startDate;
    @DatabaseField
    private String endDate;
    @DatabaseField
    private String duration;
    @DatabaseField
    private String type;
    @DatabaseField
    private String assessmentUrl;
    @DatabaseField
    private String json;
    @DatabaseField
    private String author;
    @DatabaseField
    private String date;
    @DatabaseField
    private String body;

    private boolean isSelected=false;

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getNoteMaterialPath() {
        return noteMaterialPath;
    }

    public void setNoteMaterialPath(String noteMaterialPath) {
        this.noteMaterialPath = noteMaterialPath;
    }

    public String getOtherMatherialPath() {
        return otherMatherialPath;
    }

    public void setOtherMatherialPath(String otherMatherialPath) {
        this.otherMatherialPath = otherMatherialPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAssessmentUrl() {
        return assessmentUrl;
    }

    public void setAssessmentUrl(String assessmentUrl) {
        this.assessmentUrl = assessmentUrl;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
