package com.digitaldream.winskool.models;

import com.j256.ormlite.field.DatabaseField;

public class FormClassModel {
    @DatabaseField
    private String staffId;
    @DatabaseField
    private String className;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
