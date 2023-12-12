package com.digitaldream.winskool.models;

import com.j256.ormlite.field.DatabaseField;

public class StudentResultDownloadTable {
    @DatabaseField
    private String firstTerm;
    @DatabaseField
    private String secondTerm;
    @DatabaseField
    private String thirdTerm;
    @DatabaseField
    private String level;
    @DatabaseField
    private String studentId;
    @DatabaseField
    private String levelName;
    @DatabaseField
    private String schoolYear;
    @DatabaseField
    private String classId;


    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getFirstTerm() {
        return firstTerm;
    }

    public void setFirstTerm(String firstTerm) {
        this.firstTerm = firstTerm;
    }

    public String getSecondTerm() {
        return secondTerm;
    }

    public void setSecondTerm(String secondTerm) {
        this.secondTerm = secondTerm;
    }

    public String getThirdTerm() {
        return thirdTerm;
    }

    public void setThirdTerm(String thirdTerm) {
        this.thirdTerm = thirdTerm;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }
}
