package com.digitaldream.winskool.models

data class AdminDashboardModel(
    val id: String = "",
    val username: String = "",
    val question: String = "",
    val commentsNo: String = "",
    val likesNo: String = "",
    val noOfShared: String = "",
    val type: String = "",
    val date: String = "",
    val replyList: List<AdminReplyModel> = arrayListOf()
)


data class AdminReplyModel(
    val replyId: String,
    val username: String,
    val reply: String = "",
    val replyImage: Any? = null,
    val date: String
)