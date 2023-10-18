package com.digitaldream.linkskool.models

data class CommentDataModel(
    val id: String,
    val authorId: String,
    val contentId: String,
    val authorName: String,
    val comment: String,
    val date: String = ""
)
