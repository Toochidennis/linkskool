package com.digitaldream.linkskool.models;

import com.j256.ormlite.field.DatabaseField;

public class StaffTableUtil {
    @DatabaseField
    private String staffId;
    @DatabaseField
    private String courseId;
    @DatabaseField
    private String classId;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}
