package com.digitaldream.linkskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.fragments.AdminELearningAssignmentDashboardFragment
import com.digitaldream.linkskool.fragments.AdminELearningMaterialDetailsFragment
import com.digitaldream.linkskool.fragments.AdminELearningQuestionDashboardFragment
import com.digitaldream.linkskool.fragments.StaffELearningContentDashboardFragment
import com.digitaldream.linkskool.fragments.StaffELearningCreateAssignmentFragment
import com.digitaldream.linkskool.fragments.StaffELearningCreateMaterialFragment
import com.digitaldream.linkskool.fragments.StaffELearningCreateTopicFragment
import com.digitaldream.linkskool.fragments.StaffELearningQuestionSettingsFragment

class StaffELearningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_e_learning)

        val json = intent.getStringExtra("json") ?: ""
        val task = intent.getStringExtra("task") ?: ""

        when (intent.getStringExtra("from")) {
            "content_dashboard" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningContentDashboardFragment()
                    )
                }
            }

            "create_assignment" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningCreateAssignmentFragment()
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
                        StaffELearningCreateMaterialFragment()
                    )
                }
            }

            "create_topic" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningCreateTopicFragment()
                    )
                }
            }

            "assignment" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        AdminELearningAssignmentDashboardFragment.newInstance(json, task)
                    )
                }
            }

            "question" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        AdminELearningQuestionDashboardFragment.newInstance(json, task)
                    )
                }
            }

            "material" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        AdminELearningMaterialDetailsFragment.newInstance(json, task)
                    )
                }
            }
        }
    }
}