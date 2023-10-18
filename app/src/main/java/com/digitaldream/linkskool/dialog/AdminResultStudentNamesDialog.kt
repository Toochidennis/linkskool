package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.PaymentActivity
import com.digitaldream.linkskool.adapters.AdminResultStudentNamesAdapter
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.StudentTable
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import java.util.*

class AdminResultStudentNamesDialog(
    sContext: Context,
    private val sClassId: String,
    private val sFrom: String,
    private val sOnInputListener: OnInputListener?,
) : Dialog(sContext), OnItemClickListener {

    private lateinit var mAdapter: AdminResultStudentNamesAdapter
    private lateinit var mSearchBar: EditText
    private var mStudentList = mutableListOf<StudentTable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)
        setContentView(R.layout.dialog_admin_result_student_names)

        mSearchBar = findViewById(R.id.search_bar)


        mSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                filterName(s.toString())
            }
        })

        getStudent()
    }

    private fun getStudent() {
        try {
            val recyclerView: RecyclerView = findViewById(R.id.student_recycler)
            val errorView: LinearLayout = findViewById(R.id.error_view)
            val rootView: NestedScrollView = findViewById(R.id.student_view)

            val databaseHelper = DatabaseHelper(context)
            val mDao: Dao<StudentTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, StudentTable::class.java
            )

            mStudentList = mDao.queryBuilder().where().eq("studentClass", sClassId).query()

            if (mStudentList.isEmpty()) {
                mSearchBar.isVisible = false
                errorView.isVisible = true
                rootView.isVisible = false
            } else {
                mAdapter = AdminResultStudentNamesAdapter(mStudentList, this)
                recyclerView.apply {
                    hasFixedSize()
                    layoutManager = LinearLayoutManager(context)
                    isAnimating
                    adapter = mAdapter
                }
                mSearchBar.isVisible = true
                errorView.isVisible = false
                rootView.isVisible = true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun filterName(sName: String) {
        val filteredList = mutableListOf<StudentTable>()

        mStudentList.forEach {
            if (it.studentFullName.lowercase(Locale.getDefault())
                    .contains(sName.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(it)
            }
        }

        mAdapter.filterList(filteredList)
    }

    override fun onItemClick(position: Int) {
        val model = mStudentList[position]

        if (sFrom == "student_result") {
            sOnInputListener?.sendInput(model.studentId)
        } else {

            context.startActivity(
                Intent(context, PaymentActivity::class.java)
                    .putExtra("from", "st")
                    .putExtra("studentId", model.studentId)
                    .putExtra("classId", model.studentClass)
            )
        }

        dismiss()
    }
}