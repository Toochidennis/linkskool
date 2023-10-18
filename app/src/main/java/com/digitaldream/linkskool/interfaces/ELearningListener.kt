package com.digitaldream.linkskool.interfaces

interface ELearningListener {
    fun goBackToHome()
    fun openELearning(courseName: String, courseId: String, levelName: String, levelId: String)
}