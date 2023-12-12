package com.digitaldream.winskool.models;

import com.j256.ormlite.field.DatabaseField;

public class ExamQuestions {

    @DatabaseField(generatedId = true)
    public int Id;
    @DatabaseField
    public int examId;
    @DatabaseField
    private String json;

    public int getId(){
        return Id;
    }
    public void setId(int id)
    {
        this.Id = id;
    }
    public int getExamId(){
        return examId;
    }
    public void setExamId(int name)
    {
        this.examId = name;
    }
    public String getJson(){
        return json;
    }
    public void setJson(String name)
    {
        this.json = name;
    }
}
