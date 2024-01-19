package com.digitaldream.linkskool.models

import java.io.Serializable

data class AdminDashboardModel(
    val id: String = "",
    val username: String = "",
    val question: String = "",
    val commentsNo: String = "",
    val likesNo: String = "",
    val noOfShared: String = "",
    val type: String = "",
    val date: String = "",
    val questionImageUrl: String = "",
    val replyList: List<AdminCommentsModel> = arrayListOf()
) : Serializable


data class AdminCommentsModel(
    val commentId: String = "",
    val username: String = "",
    val comment: String = "",
    val imageUrl: String = "",
    val date: String = ""
) : Serializable