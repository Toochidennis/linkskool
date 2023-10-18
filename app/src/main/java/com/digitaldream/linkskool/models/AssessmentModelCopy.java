package com.digitaldream.linkskool.models;

public class AssessmentModelCopy {
    private String assessmentId;
    private String assessmentName;
    private String maxScore;
    private String level;

    public AssessmentModelCopy(String assessmentId, String assessmentName, String maxScore, String level) {
        this.assessmentId = assessmentId;
        this.assessmentName = assessmentName;
        this.maxScore = maxScore;
        this.level = level;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public String getLevel() {
        return level;
    }
}
