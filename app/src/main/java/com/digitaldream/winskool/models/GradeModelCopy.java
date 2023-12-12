package com.digitaldream.winskool.models;

public class GradeModelCopy {

    private String gradeId;
    private String gradeName;
    private String gradeMinimuim;
    private String gradeRemark;

    public GradeModelCopy(String gradeId, String gradeName, String gradeMinimuim, String gradeRemark) {
        this.gradeId = gradeId;
        this.gradeName = gradeName;
        this.gradeMinimuim = gradeMinimuim;
        this.gradeRemark = gradeRemark;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public void setGradeMinimuim(String gradeMinimuim) {
        this.gradeMinimuim = gradeMinimuim;
    }

    public void setGradeRemark(String gradeRemark) {
        this.gradeRemark = gradeRemark;
    }

    public String getGradeId() {
        return gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public String getGradeMinimuim() {
        return gradeMinimuim;
    }

    public String getGradeRemark() {
        return gradeRemark;
    }
}
