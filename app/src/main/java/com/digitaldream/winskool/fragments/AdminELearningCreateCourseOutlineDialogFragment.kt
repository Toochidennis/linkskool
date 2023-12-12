package com.digitaldream.winskool.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.TeachersTable
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import com.google.android.material.textfield.TextInputLayout
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale


class AdminELearningCreateCourseOutlineDialogFragment(
    private val levelId: String,
    private val courseName: String,
    private val courseId: String,
    private val onCreated: (String) -> Unit
) : DialogFragment(R.layout.dialog_fragment_admin_e_learning_create_course_outline) {

    private lateinit var mBackBtn: ImageButton
    private lateinit var mCreateBtn: Button
    private lateinit var mCourseOutlineTitleInputText: TextInputLayout
    private lateinit var mDescriptionInputText: TextInputLayout
    private lateinit var mSelectAllClassesBtn: Button
    private lateinit var mSelectAllTeachersBtn: Button
    private lateinit var mClassSeparator: View
    private lateinit var mClassLayout: LinearLayout
    private lateinit var mSwitchBtn: SwitchCompat

    private var mClassList = mutableListOf<ClassNameTable>()
    private var mTeacherList = mutableListOf<TeachersTable>()
    private var selectedTeachers = hashMapOf<String, String>()
    private var selectedClasses = hashMapOf<String, String>()

    private lateinit var mDatabaseHelper: DatabaseHelper

    private var tagState: String? = null
    private var year: String? = null
    private var term: String? = null
    private var userId: String? = null
    private var userName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        loadDataset()

        editTextWatcher()

        selectClass()

        selectTeacher()

        changeTagState()

        createOutline()

        dismissDialog()
    }


    private fun setUpViews(view: View) {
        view.apply {
            mBackBtn = findViewById(R.id.backBtn)
            mCreateBtn = findViewById(R.id.createBtn)
            mCourseOutlineTitleInputText = findViewById(R.id.courseOutlineTitleInputText)
            mDescriptionInputText = findViewById(R.id.descriptionInputText)
            mSelectAllClassesBtn = findViewById(R.id.selectAllClassesBtn)
            mSelectAllTeachersBtn = findViewById(R.id.selectAllTeachersBtn)
            mClassSeparator = findViewById(R.id.classSeparator)
            mClassLayout = findViewById(R.id.classLayout)
            mSwitchBtn = findViewById(R.id.switchBtn)
        }

        mDatabaseHelper = DatabaseHelper(requireContext())

        val sharedPreferences =
            requireActivity().getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        year = sharedPreferences.getString("school_year", "")
        term = sharedPreferences.getString("term", "")
        userId = sharedPreferences.getString("user_id", "")
        userName = sharedPreferences.getString("user", "")
    }


    private fun loadDataset() {
        try {
            val classDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                mDatabaseHelper.connectionSource, ClassNameTable::class.java
            )

            mClassList = classDao.queryBuilder().where().eq("level", levelId).query()

            mClassList.forEach { item ->
                selectedClasses[item.classId] = item.className
            }

            val teacherDao: Dao<TeachersTable, Long> = DaoManager.createDao(
                mDatabaseHelper.connectionSource, TeachersTable::class.java
            )

            mTeacherList = teacherDao.queryForAll()

            mTeacherList.forEach { teacher ->
                teacher.staffFullName =
                    String.format(
                        Locale.getDefault(), "%s %s %s",
                        teacher.staffSurname, teacher.staffMiddlename,
                        teacher.staffFirstname
                    )

                selectedTeachers[teacher.staffId] = teacher.staffFullName
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectClass() {
        mSelectAllClassesBtn.setOnClickListener {
            AdminELearningSelectClassFragment(selectedClasses, levelId) { classes ->
                if (classes.isEmpty())
                    loadDataset()
                else
                    selectedClasses = classes
            }.show(parentFragmentManager, "")
        }
    }

    private fun selectTeacher() {
        mSelectAllTeachersBtn.setOnClickListener {
            AdminELearningSelectTeacherFragment(selectedTeachers) { teachers ->
                if (teachers.isEmpty())
                    loadDataset()
                else
                    selectedTeachers = teachers
            }.show(parentFragmentManager, "teacher")
        }
    }

    private fun changeTagState() {
        mSwitchBtn.setOnClickListener {
            mClassLayout.isVisible = !mSwitchBtn.isChecked

            tagState = if (mSwitchBtn.isChecked) {
                selectedClasses.clear()
                "1"
            } else {
                loadDataset()
                "0"
            }
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
            if (tagState.isNullOrEmpty() || tagState == "0") {
                if (selectedClasses.isEmpty()) {
                    showToast("Please tag a class")
                } else if (selectedTeachers.isEmpty()) {
                    showToast("Please tag a teacher")
                } else {
                    postOutline()
                }
            } else if (tagState == "1") {
                if (selectedTeachers.isEmpty()) {
                    showToast("Please tag a teacher")
                } else {
                    postOutline()
                }
            }
        }
    }

    private fun prepareOutline(): HashMap<String, String> {
        val title = mCourseOutlineTitleInputText.editText?.text.toString().trim()
        val description = mDescriptionInputText.editText?.text.toString().trim()
        val classJsonArray = JSONArray()
        val teacherJsonArray = JSONArray()

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

        selectedTeachers.forEach { (teacherId, teacherName) ->
            JSONObject().apply {
                put("id", teacherId)
                put("name", teacherName)
            }.let {
                teacherJsonArray.put(it)
            }
        }

        return HashMap<String, String>().apply {
            put("title", title)
            put("description", description)
            put("class", if (selectedClasses.isEmpty()) "" else classJsonArray.toString())
            put("teacher", teacherJsonArray.toString())
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
                    showToast("Something went wrong, please try again.")
                }
            }
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun dismissDialog() {
        mBackBtn.setOnClickListener {
            dismiss()
        }
    }

}