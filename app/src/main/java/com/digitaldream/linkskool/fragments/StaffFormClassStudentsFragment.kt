package com.digitaldream.linkskool.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.StudentProfile
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.adapters.StaffFormClassStudentsAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.StudentTable
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import java.util.*


private const val ARG_PARAM1 = "param1"


class StaffFormClassStudentsFragment : Fragment(), OnItemClickListener {

    private lateinit var mMainView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mErrorMessage: TextView
    private lateinit var mSearchBar: EditText
    private lateinit var mAdapter: StaffFormClassStudentsAdapter

    private var mClassId: String? = null
    private var mStudentList = mutableListOf<StudentTable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mClassId = it.getString(ARG_PARAM1)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(sClassId: String) =
            StaffFormClassStudentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, sClassId)
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_staff_form_class_students, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        mMainView = view.findViewById(R.id.student_name_view)
        mRecyclerView = view.findViewById(R.id.student_name_recycler)
        mErrorMessage = view.findViewById(R.id.student_error_message)
        mSearchBar = view.findViewById(R.id.search_bar)

        toolbar.apply {
            setNavigationIcon(R.drawable.arrow_left)
            toolbar.title = "Students"
            toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        }

        mSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                filterName(s.toString())
            }
        })

        return view
    }

    private fun filterName(sName: String) {
        val filteredList = mutableListOf<StudentTable>()

        mStudentList.forEach { name ->
            if (name.studentFullName.lowercase(Locale.getDefault())
                    .contains(sName.lowercase(Locale.getDefault()))
            ) {

                filteredList.add(name)
            }
        }

        mAdapter.filterList(filteredList)
    }

    private fun getStudentNames() {
        try {
            val databaseHelper =
                DatabaseHelper(requireContext())
            val mDao: Dao<StudentTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, StudentTable::class.java
            )
            mStudentList = mDao.queryBuilder().where().eq("studentClass", mClassId).query()

            if (mStudentList.isEmpty()) {
                mMainView.isVisible = true
                mErrorMessage.isVisible = true
                mSearchBar.isVisible = false
                mRecyclerView.isVisible = false
            } else {
                mAdapter = StaffFormClassStudentsAdapter(mStudentList, this)
                mRecyclerView.hasFixedSize()
                mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                mRecyclerView.adapter = mAdapter
                mMainView.isVisible = true
                mRecyclerView.isVisible = true
                mErrorMessage.isVisible = false
                mSearchBar.isVisible = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        getStudentNames()
    }

    override fun onItemClick(position: Int) {

        val studentTable = StudentTable()
        studentTable.studentSurname = mStudentList[position].studentSurname
        studentTable.studentFirstname = mStudentList[position].studentFirstname
        studentTable.studentMiddlename = mStudentList[position].studentMiddlename
        studentTable.studentLevel = mStudentList[position].studentLevel
        studentTable.guardianName = mStudentList[position].guardianName
        studentTable.guardianPhoneNo = mStudentList[position].guardianPhoneNo
        studentTable.guardianEmail = mStudentList[position].guardianEmail
        studentTable.guardianAddress = mStudentList[position].guardianAddress
        studentTable.studentGender = mStudentList[position].studentGender
        studentTable.studentReg_no = mStudentList[position].studentReg_no
        studentTable.studentLevel = mStudentList[position].studentLevel
        studentTable.studentClass = mStudentList[position].studentClass
        studentTable.state_of_origin = mStudentList[position].state_of_origin
        studentTable.date_of_birth = mStudentList[position].date_of_birth
        studentTable.studentId = mStudentList[position].studentId

        startActivity(
            Intent(requireContext(), StudentProfile::class.java)
                .putExtra("studentObject", studentTable)
        )

    }
}