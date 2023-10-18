package com.digitaldream.linkskool.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class TeachersTable implements Serializable {
    @DatabaseField
    private int id;
    @DatabaseField
    private String staffId;
    @DatabaseField
    private String staffFirstname;
    @DatabaseField
    private String staffSurname;
    @DatabaseField
    private String staffMiddlename;

    private String staffFullName;
    @DatabaseField
    private String staffGender;
    @DatabaseField
    private String staffAddress;
    @DatabaseField
    private String staffNationality;
    @DatabaseField
    private String staffPhone;
    @DatabaseField
    private String staffEmail;
    @DatabaseField
    private String staffNo;
    @DatabaseField
    private String staffLGA;
    @DatabaseField
    private String staffStateOrigin;
    @DatabaseField
    private String staffAccessLevel;
    @DatabaseField
    private String staffBirthday;
    @DatabaseField
    private String password;

    private boolean isSelected = false;

    public String getStaffBirthday() {
        return staffBirthday;
    }

    public void setStaffBirthday(String staffBirthday) {
        this.staffBirthday = staffBirthday;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffFirstname() {
        return staffFirstname;
    }

    public void setStaffFirstname(String staffFirstname) {
        this.staffFirstname = staffFirstname;
    }

    public String getStaffSurname() {
        return staffSurname;
    }

    public void setStaffSurname(String staffSurname) {
        this.staffSurname = staffSurname;
    }

    public String getStaffMiddlename() {
        return staffMiddlename;
    }

    public void setStaffMiddlename(String staffMiddlename) {
        this.staffMiddlename = staffMiddlename;
    }

    public String getStaffFullName() {
        return staffFullName;
    }

    public void setStaffFullName(String sStaffFullName) {
        staffFullName = sStaffFullName;
    }

    public String getStaffGender() {
        return staffGender;
    }

    public void setStaffGender(String staffGender) {
        this.staffGender = staffGender;
    }

    public String getStaffAddress() {
        return staffAddress;
    }

    public void setStaffAddress(String staffAddress) {
        this.staffAddress = staffAddress;
    }

    public String getStaffNationality() {
        return staffNationality;
    }

    public void setStaffNationality(String staffNationality) {
        this.staffNationality = staffNationality;
    }

    public String getStaffPhone() {
        return staffPhone;
    }

    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }

    public String getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public String getStaffLGA() {
        return staffLGA;
    }

    public void setStaffLGA(String staffLGA) {
        this.staffLGA = staffLGA;
    }

    public String getStaffStateOrigin() {
        return staffStateOrigin;
    }

    public void setStaffStateOrigin(String staffStateOrigin) {
        this.staffStateOrigin = staffStateOrigin;
    }

    public String getStaffAccessLevel() {
        return staffAccessLevel;
    }

    public void setStaffAccessLevel(String staffAccessLevel) {
        this.staffAccessLevel = staffAccessLevel;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
