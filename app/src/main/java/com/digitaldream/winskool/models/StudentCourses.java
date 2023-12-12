package com.digitaldream.winskool.models;

import com.j256.ormlite.field.DatabaseField;

public class StudentCourses {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String courseId;
    @DatabaseField
    private String classId;
    @DatabaseField
    private String courseName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
