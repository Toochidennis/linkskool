package com.digitaldream.winskool.models

data class StudentResponseModel(
    val id: String,
    val examId: String,
    val score: String,
    val studentId: String,
    val studentName: String,
    val term: String,
    val date: String
)
