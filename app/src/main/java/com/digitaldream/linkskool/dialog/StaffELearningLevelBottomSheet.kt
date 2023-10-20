package com.digitaldream.linkskool.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.LevelTable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.j256.ormlite.dao.DaoManager

class StaffELearningLevelBottomSheet : BottomSheetDialogFragment() {

    private lateinit var backBtn: ImageButton
    private lateinit var levelRecyclerView: RecyclerView

    private lateinit var levelAdapter: GenericAdapter<LevelTable>
    private lateinit var databaseHelper: DatabaseHelper

    private var levelTable = mutableListOf<LevelTable>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_staff_e_learning_level, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        loadLevels()
    }

    private fun setUpViews(view: View) {
        view.apply {
            backBtn = findViewById(R.id.backBtn)
            levelRecyclerView = findViewById(R.id.levelRecyclerView)
        }

        databaseHelper = DatabaseHelper(requireContext())

        backBtn.setOnClickListener { dismiss() }
    }

    private fun loadLevels() {
        try {
            val levelDao =
                DaoManager.createDao(databaseHelper.connectionSource, LevelTable::class.java)
            levelTable = levelDao.queryForAll()

            setUpLevelAdapter()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpLevelAdapter() {
        levelAdapter = GenericAdapter(
            levelTable,
            R.layout.item_staff_e_learning_level_layout,
            bindItem = {itemView, model, _ ->
                val levelNameTxt:TextView = itemView.findViewById(R.id.levelNameTxt)

                levelNameTxt.text = model.levelName
            }
        ){

        }

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        levelRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = levelAdapter
        }
    }

}