package com.digitaldream.winskool.models;

public class ViewPagerModel {
    private String number;
    private String title;
    private String btn_text;
    private String schoolName;

    public ViewPagerModel(String number, String title, String btn_text, String schoolName) {
        this.number = number;
        this.title = title;
        this.btn_text = btn_text;
        this.schoolName = schoolName;
    }

    public String getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getBtn_text() {
        return btn_text;
    }

    public String getSchoolName() {
        return schoolName;
    }
}
