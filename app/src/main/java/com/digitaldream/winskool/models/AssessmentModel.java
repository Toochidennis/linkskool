package com.digitaldream.winskool.models;

import com.j256.ormlite.field.DatabaseField;

public class AssessmentModel {
    @DatabaseField
    private String assessmentId;
    @DatabaseField
    private String assessmentName;
    @DatabaseField
    private String maxScore;
    @DatabaseField
    private String level;


    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
