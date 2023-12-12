package com.digitaldream.winskool.models;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionsModel implements Serializable {

    private String question;
    private List<OptionsModel> options = new ArrayList<>();
    private String questionId;
    private String questionType;
    private String sectionTitle;
    private String sectionDescription;
    private String answer;
    private String answerOrder;
    private String questionNumber;
    private String questionImage;
    private Uri questionImageUri;
    private String old_filename;


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<OptionsModel> getOptions() {
        return options;
    }

    public void setOptions(List<OptionsModel> options) {
        this.options = options;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getSectionDescription() {
        return sectionDescription;
    }

    public void setSectionDescription(String sectionDescription) {
        this.sectionDescription = sectionDescription;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerOrder() {
        return answerOrder;
    }

    public void setAnswerOrder(String sAnswerOrder) {
        answerOrder = sAnswerOrder;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public Uri getQuestionImageUri() {
        return questionImageUri;
    }

    public void setQuestionImageUri(Uri questionImageUri) {
        this.questionImageUri = questionImageUri;
    }

    public String getOld_filename() {
        return old_filename;
    }

    public void setOld_filename(String old_filename) {
        this.old_filename = old_filename;
    }
}
