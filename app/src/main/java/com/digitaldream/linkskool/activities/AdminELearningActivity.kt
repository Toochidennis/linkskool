package com.digitaldream.linkskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.fragments.*


class AdminELearningActivity : AppCompatActivity(R.layout.activity_admin_e_learning) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val levelId = intent.getStringExtra("levelId") ?: ""
        val courseId = intent.getStringExtra("courseId") ?: ""
        val courseName = intent.getStringExtra("courseName") ?: ""
        val json = intent.getStringExtra("json") ?: ""
        val task = intent.getStringExtra("task") ?: ""

        when (intent.getStringExtra("from")) {
            "view_post" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningClassFragment.newInstance(
                            levelId, courseId,
                            courseName
                        )
                    )
                }
            }

            "question_settings" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningQuestionSettingsFragment.newInstance(
                            levelId, courseId, courseName,
                        )
                    )
                }
            }

            "question" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningQuestionFragment.newInstance(json, task)
                    )
                }
            }

            "material" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningMaterialFragment
                            .newInstance(
                                levelId, courseId,
                                json, courseName, task
                            )
                    )
                }
            }

            "assignment" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningAssignmentFragment
                            .newInstance(
                                levelId, courseId,
                                json, courseName,
                                task
                            )
                    )
                }
            }

            "topic" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningCreateTopicFragment
                            .newInstance(
                                courseId,
                                levelId,
                                courseName,
                                json,
                                task
                            )
                    )
                }
            }

            "assignment_details" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningAssignmentDetailsFragment.newInstance(json, task)
                    )
                }
            }

            "question_details" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningQuestionDetailsFragment.newInstance(json, task)
                    )
                }
            }

            "material_details" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningMaterialDetailsFragment.newInstance(json, task)
                    )
                }
            }

            "question_test" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningQuizFragment.newInstance(json)
                    )
                }
            }

            "assignment_student_work"->{
                supportFragmentManager.commit {
                    replace(
                        R.id.learning_container,
                        AdminELearningAssignmentStudentWorkDetailsFragment.newInstance(json)
                    )
                }
            }

        }
    }
}