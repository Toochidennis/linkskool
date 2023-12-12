package com.digitaldream.winskool.models

data class UpcomingQuizModel(
    val id: String,
    val title: String,
    val date: String,
    val type: String,
    val courseName: String,
    val courseId: String,
    val levelId: String,
    val term: String,
)
