package com.digitaldream.linkskool.models;

import com.j256.ormlite.field.DatabaseField;

public class ClassNameTable {
    @DatabaseField
    private int id;
    @DatabaseField
    private String classId;
    @DatabaseField
    private String level;
    @DatabaseField
    private String className;
    @DatabaseField
    private String formTeacher;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFormTeacher() {
        return formTeacher;
    }

    public void setFormTeacher(String formTeacher) {
        this.formTeacher = formTeacher;
    }
}
