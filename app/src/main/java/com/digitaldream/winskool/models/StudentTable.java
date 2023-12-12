package com.digitaldream.winskool.models;

import androidx.annotation.Keep;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.List;

@Keep
public class StudentTable implements Serializable {
    public static final String STUDENTLEVEL = "studentLevel";
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String studentId;
    @DatabaseField
    private String studentSurname;
    @DatabaseField
    private String studentFirstname;
    @DatabaseField
    private String studentMiddlename;
    @DatabaseField
    private String studentGender;
    @DatabaseField
    private String studentReg_no;
    @DatabaseField
    private String studentClass;
    @DatabaseField(canBeNull = false, columnName = STUDENTLEVEL)
    private String studentLevel;
    @DatabaseField
    private String guardianName;
    @DatabaseField
    private String guardianAddress;
    @DatabaseField
    private String guardianEmail;
    @DatabaseField
    private String guardianPhoneNo;
    @DatabaseField
    private String lga;
    @DatabaseField
    private String state_of_origin;
    @DatabaseField
    private String nationality;
    @DatabaseField
    private String date_admitted;
    @DatabaseField
    private String date_of_birth;

    private int color;

    private boolean isSelectAll;

    private String courseCount;
    private String studentCount;
    private String date;
    private String studentFullName;
    private String responseId;

    private boolean showText = false;
    private boolean isSelected = false;

    private List<String> selectedPhoneNumbers;


    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentSurname() {
        return studentSurname;
    }

    public void setStudentSurname(String studentSurname) {
        this.studentSurname = studentSurname;
    }

    public String getStudentFirstname() {
        return studentFirstname;
    }

    public void setStudentFirstname(String studentFirstname) {
        this.studentFirstname = studentFirstname;
    }

    public String getStudentMiddlename() {
        return studentMiddlename;
    }

    public void setStudentMiddlename(String studentMiddlename) {
        this.studentMiddlename = studentMiddlename;
    }

    public String getStudentGender() {
        return studentGender;
    }

    public void setStudentGender(String studentGender) {
        this.studentGender = studentGender;
    }

    public String getStudentReg_no() {
        return studentReg_no;
    }

    public void setStudentReg_no(String studentReg_no) {
        this.studentReg_no = studentReg_no;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getStudentLevel() {
        return studentLevel;
    }

    public void setStudentLevel(String studentLevel) {
        this.studentLevel = studentLevel;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianAddress() {
        return guardianAddress;
    }

    public void setGuardianAddress(String guardianAddress) {
        this.guardianAddress = guardianAddress;
    }

    public String getGuardianEmail() {
        return guardianEmail;
    }

    public void setGuardianEmail(String guardianEmail) {
        this.guardianEmail = guardianEmail;
    }

    public String getGuardianPhoneNo() {
        return guardianPhoneNo;
    }

    public void setGuardianPhoneNo(String guardianPhoneNo) {
        this.guardianPhoneNo = guardianPhoneNo;
    }

    public String getLga() {
        return lga;
    }

    public void setLga(String lga) {
        this.lga = lga;
    }

    public String getState_of_origin() {
        return state_of_origin;
    }

    public void setState_of_origin(String state_of_origin) {
        this.state_of_origin = state_of_origin;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDate_admitted() {
        return date_admitted;
    }

    public void setDate_admitted(String date_admitted) {
        this.date_admitted = date_admitted;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<String> getSelectedPhoneNumbers() {
        return selectedPhoneNumbers;
    }

    public void setSelectedPhoneNumbers(List<String> selectedPhoneNumbers) {
        this.selectedPhoneNumbers = selectedPhoneNumbers;
    }

    public boolean isSelectAll() {
        return isSelectAll;
    }

    public void setSelectAll(boolean selectAll) {

        isSelectAll = selectAll;

    }

    public String getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(String courseCount) {
        this.courseCount = courseCount;
    }

    public String getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(String sStudentCount) {
        studentCount = sStudentCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String sDate) {
        date = sDate;
    }

    public String getStudentFullName() {
        return studentFullName;
    }

    public void setStudentFullName(String sStudentFullName) {
        studentFullName = sStudentFullName;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String sResponseId) {
        responseId = sResponseId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }
}
