package com.digitaldream.linkskool.models;

import java.io.Serializable;

public class QAObject implements Serializable {
    private String question;
    private String answer;
    private boolean isQuestionOwner = false;
    private String user;
    private String date;
    private String picUrl;
    private String questionId;
    private String commentNo;
    private String likesNo;
    private String shareNo;
    private String feedType;
    private String preText;
    private String preImage;
    private String answerId;
    private String id;


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isQuestionOwner() {
        return isQuestionOwner;
    }

    public void setQuestionOwner(boolean questionOwner) {
        isQuestionOwner = questionOwner;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getCommentNo() {
        return commentNo;
    }

    public void setCommentNo(String commentNo) {
        this.commentNo = commentNo;
    }

    public String getLikesNo() {
        return likesNo;
    }

    public void setLikesNo(String likesNo) {
        this.likesNo = likesNo;
    }

    public String getShareNo() {
        return shareNo;
    }

    public void setShareNo(String shareNo) {
        this.shareNo = shareNo;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getPreText() {
        return preText;
    }

    public void setPreText(String preText) {
        this.preText = preText;
    }

    public String getPreImage() {
        return preImage;
    }

    public void setPreImage(String preImage) {
        this.preImage = preImage;
    }


    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
