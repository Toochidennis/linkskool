package com.digitaldream.linkskool.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningFilesAdapter
import com.digitaldream.linkskool.dialog.StudentELearningAssignmentSubmissionDialogFragment
import com.digitaldream.linkskool.models.AttachmentModel
import com.digitaldream.linkskool.utils.FileViewModel
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StudentELearningAssignmentFragment : Fragment() {

    private lateinit var dateTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var gradeTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var attachmentTxt: TextView
    private lateinit var attachmentRecyclerView: RecyclerView
    private lateinit var submitBtn: Button

    private lateinit var attachmentAdapter: AdminELearningFilesAdapter
    private var attachmentList = mutableListOf<AttachmentModel>()

    private lateinit var fileViewModel: FileViewModel

    private var jsonData: String? = null
    private var param2: String? = null
    private var title: String? = null
    private var dueDate: String? = null
    private var description: String? = null
    private var grade: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        fileViewModel = ViewModelProvider(this)[FileViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_e_learning_assignment, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String = "") =
            StudentELearningAssignmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        parseJsonObject()

        submitAssignment()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            dateTxt = findViewById(R.id.dateTxt)
            titleTxt = findViewById(R.id.titleTxt)
            gradeTxt = findViewById(R.id.gradeTxt)
            descriptionTxt = findViewById(R.id.descriptionTxt)
            attachmentTxt = findViewById(R.id.attachmentTxt)
            attachmentRecyclerView = findViewById(R.id.attachmentRecyclerView)
            submitBtn = findViewById(R.id.submitAssignmentBtn)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "Assignment"
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        }
    }

    private fun parseJsonObject() {
        try {
            if (jsonData?.isNotBlank() == true) {
                jsonData?.let { json ->
                    JSONObject(json).let {
                        title = it.getString("title")
                        grade = it.getString("objective")
                        description = it.getString("description")
                        dueDate = formatDate2(it.getString("end_date"), "date time")
                        parseFilesArray(JSONArray(it.getString("picref")))

                        requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)
                            .edit()
                            .putString("content_id", it.getString("id"))
                            .putString("content_title", title)
                            .putString("content_type", it.getString("type"))
                            .apply()
                    }
                }

                setTextOnViews()

                setUpAttachmentRecyclerView()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpAttachmentRecyclerView() {
        attachmentAdapter =
            AdminELearningFilesAdapter(parentFragmentManager, attachmentList, fileViewModel)

        attachmentRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = attachmentAdapter
        }

    }

    private fun setTextOnViews() {
        if (dueDate?.isNotBlank() == true) {
            "Due $dueDate".let { dateTxt.text = it }
        }

        if (grade?.isNotBlank() == true) {
            "$grade points".let { gradeTxt.text = it }
        }

        titleTxt.text = title
        descriptionTxt.text = description
    }

    private fun parseFilesArray(files: JSONArray) {
        for (i in 0 until files.length()) {
            files.getJSONObject(i).let {
                val fileName = trimText(it.getString("file_name"))
                val oldFileName = trimText(it.getString("file_name"))
                val type = it.getString("type")
                val uri = it.getString("file_name")

                val attachmentModel = AttachmentModel(fileName, oldFileName, type, uri)

                attachmentList.add(attachmentModel)
            }
        }
    }

    private fun trimText(text: String): String {
        return text.replace("../assets/elearning/practice/", "").ifEmpty { "" }
    }

    private fun submitAssignment() {
        submitBtn.setOnClickListener {
            StudentELearningAssignmentSubmissionDialogFragment().show(parentFragmentManager, "")
        }

    }

}