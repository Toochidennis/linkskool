package com.digitaldream.winskool.models

data class CourseOutlineModel(
    val id: String,
    val title: String,
    val description: String,
    val courseId: String,
    val levelId: String,
    val teacherName:String,
    val term: String
)
