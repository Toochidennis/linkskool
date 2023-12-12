package com.digitaldream.winskool.models;

public class CourseResultModel {
    private String mCourseName;
    private String mCourseId;
    private String mClassId;

    public CourseResultModel() {
    }

    public CourseResultModel(String courseName, String courseId, String classId) {
        this.mCourseName = courseName;
        this.mCourseId = courseId;
        this.mClassId = classId;
    }

    public String getCourseName() {
        return mCourseName;
    }

    public String getCourseId() {
        return mCourseId;
    }

    public String getClassId() {
        return mClassId;
    }
}
