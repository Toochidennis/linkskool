package com.digitaldream.linkskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.fragments.*


class StaffELearningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_e_learning)

        val json = intent.getStringExtra("json") ?: ""
        val task = intent.getStringExtra("task") ?: ""

        when (intent.getStringExtra("from")) {
            "create_assignment" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningCreateAssignmentFragment.newInstance(json, task)
                    )
                }
            }

            "question_settings" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningQuestionSettingsFragment()
                    )
                }
            }

            "create_material" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningCreateMaterialFragment.newInstance(json, task)
                    )
                }
            }

            "create_topic" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningCreateTopicFragment.newInstance(json, task)
                    )
                }
            }

            "create_question" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningCreateQuestionFragment.newInstance(json, task)
                    )
                }
            }

            "assignment_dashboard" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningAssignmentDashboardFragment.newInstance(json, task)
                    )
                }
            }

            "question_dashboard" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningQuestionDashboardFragment.newInstance(json, task)
                    )
                }
            }

            "material_dashboard" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningMaterialDetailsFragment.newInstance(json, task)
                    )
                }
            }
        }
    }
}