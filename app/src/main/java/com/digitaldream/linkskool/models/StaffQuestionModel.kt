package com.digitaldream.linkskool.models

data class StaffQuestionModel(
    val id: String = "",
    val username: String = "",
    val question: String = "",
    val commentsNo: String = "",
    val likesNo: String = "",
    val noOfShared: String = "",
    val type: String = "",
    val date: String = "",
    val replyList: List<StaffReplyModel> = arrayListOf()
)

data class StaffReplyModel(
    val replyId: String,
    val username: String,
    val reply: String = "",
    val replyImage: Any? = null,
    val date: String
)