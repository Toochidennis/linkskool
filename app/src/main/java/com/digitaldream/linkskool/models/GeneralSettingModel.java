package com.digitaldream.linkskool.models;

import com.j256.ormlite.field.DatabaseField;

public class GeneralSettingModel {
    @DatabaseField
    private String schoolName;
    @DatabaseField
    private String schoolYear;
    @DatabaseField
    private String schoolTerm;
    @DatabaseField
    private String website;
    @DatabaseField
    private String shcoolShortName;
    @DatabaseField
    private String schoolAddress;
    @DatabaseField
    private String city;
    @DatabaseField
    private String state;
    @DatabaseField
    private String country;
    @DatabaseField
    private String schoolEmail;
    @DatabaseField
    private String schoolPhone;
    @DatabaseField
    private String studentPrefix;
    @DatabaseField
    private String staffPrefix;
    @DatabaseField
    private String alumniPrefix;

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public String getSchoolTerm() {
        return schoolTerm;
    }

    public void setSchoolTerm(String schoolTerm) {
        this.schoolTerm = schoolTerm;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getShcoolShortName() {
        return shcoolShortName;
    }

    public void setShcoolShortName(String shcoolShortName) {
        this.shcoolShortName = shcoolShortName;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSchoolEmail() {
        return schoolEmail;
    }

    public void setSchoolEmail(String schoolEmail) {
        this.schoolEmail = schoolEmail;
    }

    public String getSchoolPhone() {
        return schoolPhone;
    }

    public void setSchoolPhone(String schoolPhone) {
        this.schoolPhone = schoolPhone;
    }

    public String getStudentPrefix() {
        return studentPrefix;
    }

    public void setStudentPrefix(String studentPrefix) {
        this.studentPrefix = studentPrefix;
    }

    public String getStaffPrefix() {
        return staffPrefix;
    }

    public void setStaffPrefix(String staffPrefix) {
        this.staffPrefix = staffPrefix;
    }

    public String getAlumniPrefix() {
        return alumniPrefix;
    }

    public void setAlumniPrefix(String alumniPrefix) {
        this.alumniPrefix = alumniPrefix;
    }
}
