package com.digitaldream.linkskool.models;

import com.j256.ormlite.field.DatabaseField;

public class TeacherCourseModelCopy {
    @DatabaseField
    private String id;
    @DatabaseField
    private String className;
    @DatabaseField
    private String courseName;
    @DatabaseField
    private String courseId;
    @DatabaseField
    private String classId;
    @DatabaseField
    private String staffId;

    public TeacherCourseModelCopy(String id, String className, String courseName, String courseId, String classId) {
        this.id = id;
        this.className = className;
        this.courseName = courseName;
        this.courseId = courseId;
        this.classId = classId;
        this.staffId = staffId;
    }

    public TeacherCourseModelCopy() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
