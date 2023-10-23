package com.digitaldream.linkskool.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.StaffELearningActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class StaffELearningCreateContentDialogFragment : BottomSheetDialogFragment() {

    private lateinit var assignmentBtn: TextView
    private lateinit var questionBtn: TextView
    private lateinit var materialBtn: TextView
    private lateinit var reuseBtn: TextView
    private lateinit var topicBtn: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_staff_e_learning_create_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        handleClicks()
    }

    private fun setUpViews(view: View) {
        view.apply {
            assignmentBtn = findViewById(R.id.assignment_btn)
            questionBtn = findViewById(R.id.question_btn)
            materialBtn = findViewById(R.id.material_btn)
            reuseBtn = findViewById(R.id.reuse_btn)
            topicBtn = findViewById(R.id.topic_btn)
        }

    }

    private fun handleClicks() {
        assignmentBtn.setOnClickListener {
            launchActivity("create_assignment")
        }

        questionBtn.setOnClickListener {
            launchActivity("question_settings")
        }

        materialBtn.setOnClickListener {
            launchActivity("create_material")
        }

        topicBtn.setOnClickListener {
            launchActivity("create_topic")
        }
    }

    private fun launchActivity(from: String) {
        requireActivity().startActivity(
            Intent(context, StaffELearningActivity::class.java)
                .putExtra("from", from)
        )

        dismiss()
    }

}