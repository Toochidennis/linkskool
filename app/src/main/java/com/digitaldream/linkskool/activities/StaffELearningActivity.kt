package com.digitaldream.linkskool.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.fragments.StaffELearningPostDashboardFragment

class StaffELearningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_e_learning)

        when (intent.getStringExtra("from")) {
            "post_dashboard" -> {
                supportFragmentManager.commit {
                    replace(
                        R.id.staffLearningContainer,
                        StaffELearningPostDashboardFragment()
                    )
                }
            }
        }


    }
}