package com.digitaldream.linkskool.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.ReceiptStudentBudgetActivity
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.adapters.ReceiptStudentNameAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.dialog.OnInputListener
import com.digitaldream.linkskool.dialog.TermFeeDialog
import com.digitaldream.linkskool.models.StudentTable
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"


class  ReceiptStudentNameFragment : Fragment(), OnItemClickListener {

    private lateinit var mMainView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mErrorMessage: TextView
    private lateinit var mToolbarText: TextView
    private lateinit var mBackBtn: ImageView
    private lateinit var mLevelBtn: Button
    private lateinit var mSearchBar: EditText

    private lateinit var mAdapter: ReceiptStudentNameAdapter
    private var classId: String? = null
    private var className: String? = null
    private var levelName: String? = null
    private var mLevelName: String? = null
    private var mStudentList = mutableListOf<StudentTable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            classId = it.getString(ARG_PARAM1)
            className = it.getString(ARG_PARAM2)
            levelName = it.getString(ARG_PARAM3)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(sClassId: String, sClassName: String, sLevelName: String) =
            ReceiptStudentNameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, sClassId)
                    putString(ARG_PARAM2, sClassName)
                    putString(ARG_PARAM3, sLevelName)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_receipt_student_name, container, false)

        mMainView = view.findViewById(R.id.student_name_view)
        mRecyclerView = view.findViewById(R.id.student_name_recycler)
        mErrorMessage = view.findViewById(R.id.student_error_message)
        mSearchBar = view.findViewById(R.id.search_bar)
        mLevelBtn = view.findViewById(R.id.level_name)
        mToolbarText = view.findViewById(R.id.toolbar_text)
        mBackBtn = view.findViewById(R.id.back_btn)

        "Select Student".also { mToolbarText.text = it }
        mLevelBtn.text = levelName

        mBackBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
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

        getStudentNames(classId!!)
        changeLevel()

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

    private fun getStudentNames(sClassId: String) {
        try {
            val databaseHelper =
                DatabaseHelper(requireContext())
            val mDao: Dao<StudentTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, StudentTable::class.java
            )
            mStudentList = mDao.queryBuilder().where().eq("studentClass", sClassId).query()

            if (mStudentList.isEmpty()) {
                mMainView.isVisible = true
                mErrorMessage.isVisible = true
                mSearchBar.isVisible = false
                mRecyclerView.isVisible = false
            } else {
                mAdapter = ReceiptStudentNameAdapter(mStudentList, this)
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

    override fun onItemClick(position: Int) {
        val studentTable = mStudentList[position]
        startActivity(
            Intent(requireContext(), ReceiptStudentBudgetActivity::class.java)
                .putExtra("student_name", studentTable.studentFullName)
                .putExtra("levelId", studentTable.studentLevel)
                .putExtra("classId", studentTable.studentClass)
                .putExtra("studentId", studentTable.studentId)
                .putExtra("reg_no", studentTable.studentReg_no)
                .putExtra("level_name", levelName)
        )

    }

    private fun setLevelName() {
        mLevelBtn.text = mLevelName
    }

    private fun changeLevel() {
        mLevelBtn.setOnClickListener {
            TermFeeDialog(
                requireContext(),
                "changeLevel",
                object : OnInputListener {
                    override fun sendInput(input: String) {
                        mLevelName = input
                        setLevelName()
                    }

                    override fun sendId(levelId: String) {
                        mStudentList.clear()
                        getStudentNames(levelId)
                    }
                },
            ).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}