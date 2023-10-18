package com.digitaldream.linkskool.models;

import java.io.Serializable;

public class PrevYrModel implements Serializable {
    private String term;
    private String year;
    private String name;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
