package com.digitaldream.linkskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.fragments.StudentELearningAssignmentFragment
import com.digitaldream.linkskool.fragments.StudentELearningMaterialFragment
import com.digitaldream.linkskool.fragments.StudentELearningQuestionFragment

class StudentELearningActivity : AppCompatActivity(R.layout.activity_student_e_learning) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra("json") ?: ""

        when (intent.getStringExtra("from")) {
            "material" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learningContainer,
                        StudentELearningMaterialFragment.newInstance(json)
                    )
                }
            }

            "assignment" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learningContainer,
                        StudentELearningAssignmentFragment.newInstance(json)
                    )
                }
            }

            "question" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.learningContainer,
                        StudentELearningQuestionFragment.newInstance(json)
                    )
                }
            }
        }


    }
}