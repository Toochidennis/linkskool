package com.digitaldream.winskool.models;

import com.j256.ormlite.field.DatabaseField;

public class ExamType {

    @DatabaseField(generatedId = true)
    public int Id;
    @DatabaseField
    private int examTypeId;
    @DatabaseField
    public String examName;
    @DatabaseField
    private String examLogo;
    @DatabaseField
    private String status;
    @DatabaseField
    private String category;

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
    public String getExamName(){
        return examName;
    }
    public void setExamName(String name)
    {
        this.examName = name;
    }
    public String getExamLogo(){
        return examLogo;
    }
    public void setExamLogo(String name)
    {
        this.examLogo = name;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String name)
    {
        this.status = name;
    }
    public String getCategory(){
        return category;
    }
    public void setCategory(String name)
    {
        this.category = name;
    }
}
