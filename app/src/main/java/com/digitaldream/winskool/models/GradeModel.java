package com.digitaldream.winskool.models;

import com.j256.ormlite.field.DatabaseField;

public class GradeModel {
    @DatabaseField
    private String gradeId;
    @DatabaseField
    private String gradeName;
    @DatabaseField
    private String gradeMinimuim;
    @DatabaseField
    private String gradeRemark;

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getGradeMinimuim() {
        return gradeMinimuim;
    }

    public void setGradeMinimuim(String gradeMinimuim) {
        this.gradeMinimuim = gradeMinimuim;
    }

    public String getGradeRemark() {
        return gradeRemark;
    }

    public void setGradeRemark(String gradeRemark) {
        this.gradeRemark = gradeRemark;
    }
}
