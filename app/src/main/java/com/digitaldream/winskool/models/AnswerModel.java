package com.digitaldream.winskool.models;


public class AnswerModel {
    private String user;
    private String date;
    private String answer;
    private String commentNo;
    private String likeNo;
    private String shareNo;
    private String parent;
    private String answerId;

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCommentNo() {
        return commentNo;
    }

    public void setCommentNo(String commentNo) {
        this.commentNo = commentNo;
    }

    public String getLikeNo() {
        return likeNo;
    }

    public void setLikeNo(String likeNo) {
        this.likeNo = likeNo;
    }

    public String getShareNo() {
        return shareNo;
    }

    public void setShareNo(String shareNo) {
        this.shareNo = shareNo;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }
}
