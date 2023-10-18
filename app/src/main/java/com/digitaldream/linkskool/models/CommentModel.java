package com.digitaldream.linkskool.models;

public class CommentModel {
    private String user;
    private String date;
    private String answer;
    private String likeNo;
    private String parent;
    private String commentId;

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

    public String getLikeNo() {
        return likeNo;
    }

    public void setLikeNo(String likeNo) {
        this.likeNo = likeNo;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
