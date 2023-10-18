package com.digitaldream.linkskool.models;

import com.j256.ormlite.field.DatabaseField;

public class Exam {

    @DatabaseField(generatedId = true)
    public int Id;
    @DatabaseField
    private int examTypeId;
    @DatabaseField
    public int examId;
    @DatabaseField
    private int yearId;
    @DatabaseField
    private String course;
    @DatabaseField
    private String year;

    public int getId(){
        return Id;
    }
    public void setId(int id)
    {
        this.Id = id;
    }
    public int getExamTypeId(){
        return examTypeId;
    }
    public void setExamTypeId(int id)
    {
        this.examTypeId = id;
    }
    public int getExamId(){
        return examId;
    }
    public void setExamId(int name)
    {
        this.examId = name;
    }
    public int getYearId(){
        return yearId;
    }
    public void setYearId(int name)
    {
        this.yearId = name;
    }
    public String getCourse(){
        return course;
    }
    public void setCourse(String name)
    {
        this.course = name;
    }
    public String getYear(){
        return year;
    }
    public void setYear(String name)
    {
        this.year = name;
    }
}
