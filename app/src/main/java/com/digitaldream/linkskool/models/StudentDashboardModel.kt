package com.digitaldream.linkskool.models

import java.io.Serializable

data class StudentDashboardModel(
    val id: String = "",
    val username: String = "",
    val question: String = "",
    val commentsNo: String = "",
    val likesNo: String = "",
    val noOfShared: String = "",
    val type: String = "",
    val date: String = "",
    val replyList: List<StudentReplyModel> = arrayListOf()
):Serializable

data class StudentReplyModel(
    val replyId: String,
    val username: String,
    val reply: String = "",
    val replyImage: Any? = null,
    val date: String
):Serializable
