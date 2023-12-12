package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.adapters.TermFeeDialogAdapter
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.models.LevelTable
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager

class TermlyFeeDialog(
    private val sContext: Context,
    private val sCurrentText: String,
    private var sInputListener: OnInputListener,
) : Dialog(sContext), OnItemClickListener {

    private lateinit var mLevelList: MutableList<LevelTable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)
        setContentView(R.layout.dialog_fee_termly)

        getLevelName()

    }

    private fun getLevelName() {
        try {
            val mRecyclerView: RecyclerView = findViewById(R.id.level_recycler)
            mLevelList = mutableListOf()
            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<LevelTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, LevelTable::class.java
            )

            mLevelList = mDao.queryForAll()
            mLevelList.sortBy { it.levelName }
            val mAdapter = TermFeeDialogAdapter(context, mLevelList, this, sCurrentText)
            mRecyclerView.hasFixedSize()
            mRecyclerView.layoutManager = GridLayoutManager(context, 2)
            mRecyclerView.adapter = mAdapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onItemClick(position: Int) {
        val levelTable = mLevelList[position]
        AlertDialog.Builder(sContext).apply {
            setTitle("Warning!")
            setMessage("Your unsaved changes will be lost if you change level")
            setCancelable(false)
            setPositiveButton("I have saved my changes") { _, _ ->
                sInputListener.sendInput(levelTable.levelName)
                sInputListener.sendId(levelTable.levelId)
                dismiss()
            }
            setNegativeButton("No") { _, _ -> dismiss() }
            show()
        }

    }

}

interface OnInputListener {
    fun sendInput(input: String)
    fun sendId(levelId: String)
}