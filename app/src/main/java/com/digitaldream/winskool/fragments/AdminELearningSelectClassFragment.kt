package com.digitaldream.winskool.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.AdminELearningSelectClassAdapter
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.TagModel
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager


class AdminELearningSelectClassFragment(
    private var selectedClasses: HashMap<String, String>,
    private val levelId: String,
    private val onSelected: (HashMap<String, String>) -> Unit
) : DialogFragment(R.layout.fragment_admin_e_learning_select_class) {

    // Define UI elements
    private lateinit var backBtn: ImageButton
    private lateinit var doneBtn: Button
    private lateinit var selectAllLayout: RelativeLayout
    private lateinit var selectAllStateLayout: LinearLayout
    private lateinit var classRecyclerView: RecyclerView

    private val tagList = mutableListOf<TagModel>()
    private var classList = mutableListOf<ClassNameTable>()
    private lateinit var selectedClassesCopy: HashMap<String, String>

    private lateinit var classAdapter: AdminELearningSelectClassAdapter

    private lateinit var databaseHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        setUpDataset()

        doneAction()

        backBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun setUpViews(view: View) {
        view.apply {
            backBtn = findViewById(R.id.backBtn)
            doneBtn = findViewById(R.id.doneBtn)
            selectAllLayout = findViewById(R.id.selectAllLayout)
            selectAllStateLayout = findViewById(R.id.selectedStateLayout)
            classRecyclerView = findViewById(R.id.classRecyclerView)
        }

        databaseHelper = DatabaseHelper(requireContext())

    }

    private fun setUpDataset() {
        try {
            selectAllLayout.isSelected = false
            selectedClassesCopy = selectedClasses
            val classDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, ClassNameTable::class.java
            )

            classList = classDao.queryBuilder().where().eq("level", levelId).query()

            classList.forEach { item ->
                val className = selectedClassesCopy[item.classId]
                if (className == item.className) {
                    tagList.add(TagModel(item.classId, item.className, true))
                } else {
                    tagList.add(TagModel(item.classId, item.className))
                }
            }

            tagList.sortBy { it.tagName }

            setUpRecyclerView()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpRecyclerView() {
        classAdapter = AdminELearningSelectClassAdapter(
            tagList, selectedClassesCopy,
            selectAllLayout, selectAllStateLayout
        )

        classRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = classAdapter
        }
    }

    private fun doneAction() {
        doneBtn.setOnClickListener {
            onSelected(selectedClasses)
            it.isEnabled = false
            dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        if (selectedClassesCopy.isEmpty())
            onSelected(hashMapOf())
    }

}