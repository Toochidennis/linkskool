package com.digitaldream.linkskool.dialog

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningQuestionPreviewAdapter
import com.digitaldream.linkskool.models.QuestionItem
import com.digitaldream.linkskool.models.SectionModel
import java.io.Serializable
import java.util.Locale

private const val ARG_PARAM1 = "section"


class AdminELearningQuestionPreviewFragment :
    DialogFragment(R.layout.fragment_admin_e_learning_question_preview) {

    private lateinit var questionCountTxt: TextView
    private lateinit var questionTotalCountTxt: TextView
    private lateinit var sectionTxt: TextView
    private lateinit var questionRecyclerView: RecyclerView
    private lateinit var previousBtn: Button
    private lateinit var nextBtn: Button

    // Initialise section items
    private lateinit var sectionItems: MutableList<SectionModel>

    // Variables to store data
    private var currentSectionIndex: Int = 0
    private var currentQuestionCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)

        arguments?.let {
            sectionItems = it.getSerializable(ARG_PARAM1) as MutableList<SectionModel>
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(sectionData: MutableList<SectionModel>) =
            AdminELearningQuestionPreviewFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, sectionData as Serializable)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        initialiseSectionItem()

        previousBtn.setOnClickListener {
            showPreviousQuestion()
        }

        nextBtn.setOnClickListener {
            showNextQuestion()
        }
    }


    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            questionCountTxt = findViewById(R.id.questionCount)
            questionTotalCountTxt = findViewById(R.id.questionTotalCount)
            sectionTxt = findViewById(R.id.sectionTxt)
            questionRecyclerView = findViewById(R.id.questionRecyclerView)
            previousBtn = findViewById(R.id.prevBtn)
            nextBtn = findViewById(R.id.nextBtn)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireContext() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "Question preview"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener {
                dismiss()
            }
        }
    }

    private fun initialiseSectionItem() {
        if (::sectionItems.isInitialized && sectionItems.isNotEmpty()) {

            val totalCount = totalCount()
            val countString = String.format(Locale.getDefault(), "/%d", totalCount)

            questionTotalCountTxt.text = countString

            showQuestion()
        }
    }

    private fun showQuestion() {
        val currentSection = sectionItems.getOrNull(currentSectionIndex)
        if (currentSection != null) {
            sectionTxt.text = currentSection.sectionTitle
            sectionTxt.isVisible = !currentSection.sectionTitle.isNullOrEmpty()

            val questionItem = currentSection.questionItem
            questionRecyclerView.isVisible = questionItem != null

            if (currentSectionIndex == 0) {
                if (questionItem != null) {
                    currentQuestionCount = 1

                    val countString =
                        String.format(Locale.getDefault(), "Question %d", currentQuestionCount)
                    questionCountTxt.text = countString
                }
            }

            if (questionItem != null) {
                showQuestionPreview(questionItem)

                val countString =
                    String.format(Locale.getDefault(), "Question %d", currentQuestionCount)
                questionCountTxt.text = countString
            }

            updateNavigationButtons()

        }
    }

    private fun updateNavigationButtons() =
        if (sectionItems.size == 1) {
            disableNextButton()
            disablePreviousButton()
        } else if (currentSectionIndex == 0) {
            enableNextButton()
            disablePreviousButton()
        } else if (currentSectionIndex == sectionItems.size - 1) {
            disableNextButton()
            enablePreviousButton()
        } else {
            enableNextButton()
            enablePreviousButton()
        }

    private fun showNextQuestion() {
        if (currentSectionIndex < sectionItems.size) {

            // Decrement the question count only if there are questions
            if (sectionItems.getOrNull(currentSectionIndex)?.questionItem != null) {
                currentQuestionCount++
            }

            currentSectionIndex++

            showQuestion()
        }
    }

    private fun showPreviousQuestion() {
        if (currentSectionIndex > 0) {

            // Decrement the question count only if there are questions
            if (sectionItems.getOrNull(currentSectionIndex)?.questionItem != null) {
                currentQuestionCount--
            } else {
                currentQuestionCount--
            }

            currentSectionIndex--

            showQuestion()
        }
    }

    private fun showQuestionPreview(nextQuestion: QuestionItem?) {
        AdminELearningQuestionPreviewAdapter(mutableListOf(nextQuestion)).let {
            questionRecyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(requireContext())
                adapter = it
            }
        }
    }

    private fun totalCount(): Int {
        var count = 0
        sectionItems.forEach {
            if (it.questionItem != null) {
                count++
            }
        }

        return count
    }

    private fun disablePreviousButton() {
        // Disable the previous button
        previousBtn.isEnabled = false
    }

    private fun enablePreviousButton() {
        // Enable the previous button
        previousBtn.isEnabled = true
    }

    private fun disableNextButton() {
        // Disable the next button
        nextBtn.isEnabled = false
    }

    private fun enableNextButton() {
        // Enable the next button
        nextBtn.isEnabled = true
    }

}