package com.digitaldream.linkskool.dialog

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.CourseTable
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import com.google.android.material.textfield.TextInputLayout
import com.j256.ormlite.dao.DaoManager
import org.json.JSONArray
import org.json.JSONObject

class StaffELearningCreateCourseOutlineDialogFragment(
    private val onCreated: (String) -> Unit
) : DialogFragment() {

    private lateinit var mBackBtn: ImageButton
    private lateinit var mCreateBtn: Button
    private lateinit var mCourseOutlineTitleInputText: TextInputLayout
    private lateinit var mDescriptionInputText: TextInputLayout

    private var classList = mutableListOf<CourseTable>()
    private var selectedClasses = hashMapOf<String, String>()

    private lateinit var mDatabaseHelper: DatabaseHelper

    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var userName: String? = null
    private var levelId: String? = null
    private var courseName: String? = null
    private var courseId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_fragment_staff_e_learning_create_course_outline,
            container, false
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        loadClasses()

        editTextWatcher()

        createOutline()

        dismissDialog()
    }

    private fun setUpViews(view: View) {
        view.apply {
            mBackBtn = findViewById(R.id.backBtn)
            mCreateBtn = findViewById(R.id.createBtn)
            mCourseOutlineTitleInputText = findViewById(R.id.courseOutlineTitleInputText)
            mDescriptionInputText = findViewById(R.id.descriptionInputText)
        }

        mDatabaseHelper = DatabaseHelper(requireContext())

        val sharedPreferences =
            requireActivity().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)

        with(sharedPreferences) {
            year = getString("school_year", "")
            term = getString("term", "")
            userId = getString("user_id", "")
            userName = getString("user", "")
            courseId = getString("courseId", "")
            levelId = getString("level", "")
            courseName = getString("course_name", "")
        }

    }

    private fun loadClasses() {
        try {
            val classDao = DaoManager.createDao(
                mDatabaseHelper.connectionSource, CourseTable::class.java
            )
            classList = classDao.queryBuilder().groupBy("classId")
                .where().eq("levelId", levelId).query()

            classList.forEach { item ->
                selectedClasses[item.classId] = item.className
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun editTextWatcher() {
        mCreateBtn.isEnabled = false

        mCourseOutlineTitleInputText.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                mCreateBtn.isEnabled = s.toString().isNotEmpty()
            }
        })
    }


    private fun createOutline() {
        mCreateBtn.setOnClickListener {
            postOutline()
        }
    }

    private fun prepareOutline(): HashMap<String, String> {
        val title = mCourseOutlineTitleInputText.editText?.text.toString().trim()
        val description = mDescriptionInputText.editText?.text.toString().trim()
        val classJsonArray = JSONArray()

        if (selectedClasses.isNotEmpty()) {
            selectedClasses.forEach { (classId, className) ->
                JSONObject().apply {
                    put("id", classId)
                    put("name", className)
                }.let {
                    classJsonArray.put(it)
                }
            }
        }


        return HashMap<String, String>().apply {
            put("title", title)
            put("description", description)
            put("class", if (selectedClasses.isEmpty()) "" else classJsonArray.toString())
            put("teacher", "")
            put("course", courseId ?: "")
            put("course_name", courseName ?: "")
            put("level", levelId ?: "")
            put("author_id", userId ?: "")
            put("author_name", userName ?: "")
            put("year", year ?: "")
            put("term", term ?: "")
        }
    }

    private fun postOutline() {
        val data = prepareOutline()
        val url = "${requireContext().getString(R.string.base_url)}/addOutline.php"

        sendRequestToServer(
            Request.Method.POST,
            url,
            requireContext(),
            data,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    onCreated("created")
                    dismiss()
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong, please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }


    private fun dismissDialog() {
        mBackBtn.setOnClickListener {
            dismiss()
        }
    }

}