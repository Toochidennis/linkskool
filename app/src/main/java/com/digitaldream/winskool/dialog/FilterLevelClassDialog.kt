package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.FilterLevelClassAdapter
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.interfaces.OnLevelClassClickListener
import com.digitaldream.winskool.models.ClassNameTable
import com.digitaldream.winskool.models.LevelTable
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.digitaldream.winskool.utils.FunctionUtils.getSelectedItem
import com.digitaldream.winskool.utils.FunctionUtils.onItemClick
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager

class FilterLevelClassDialog(
    private val context: Context,
    private val sTimeFrameDataModel: TimeFrameDataModel,
    private val sFrom: String,
    private val sDismiss: () -> Unit
) : Dialog(context), OnLevelClassClickListener {


    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTitle: TextView
    private lateinit var mErrorMessage: TextView
    private lateinit var mDoneBtn: Button
    private lateinit var mDismissBtn: ImageView

    private var mLevelList = mutableListOf<LevelTable>()
    private var mClassList = mutableListOf<ClassNameTable>()

    private val selectedItems = hashMapOf<String, String>()

    private var levelItemPosition = LevelTable()
    private var classItemPosition = ClassNameTable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        setContentView(R.layout.dialog_filter_level_class)

        mDismissBtn = findViewById(R.id.close_btn)
        mRecyclerView = findViewById(R.id.recycler_view)
        mTitle = findViewById(R.id.title)
        mErrorMessage = findViewById(R.id.error_message)
        mDoneBtn = findViewById(R.id.done_btn)


        if (sFrom == "level") {
            getLevelName()
        } else {
            getClassName()
        }

        mDismissBtn.setOnClickListener { dismiss() }
    }


    private fun getLevelName() {
        try {
            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<LevelTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, LevelTable::class.java
            )

            mLevelList = mDao.queryForAll()
            mLevelList.sortBy { it.levelName }


            if (mLevelList.isEmpty()) {
                mErrorMessage.isVisible = true
                mRecyclerView.isVisible = false

            } else {
                FilterLevelClassAdapter(
                    sLevelList = mLevelList,
                    sClassList = null,
                    this
                ).let {
                    mRecyclerView.apply {
                        isVisible = true
                        isAnimating
                        layoutManager = LinearLayoutManager(context)
                        hasFixedSize()
                        adapter = it
                    }

                }

                mTitle.text = context.getString(R.string.select_level)
                mErrorMessage.isVisible = false

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getClassName() {
        try {
            mClassList.clear()

            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, ClassNameTable::class.java
            )
            mClassList = mDao.queryForAll()
            mClassList.sortBy { it.className }

            if (mClassList.isEmpty()) {
                mErrorMessage.isVisible = true
                mRecyclerView.isVisible = false

            } else {
                FilterLevelClassAdapter(
                    sLevelList = null,
                    sClassList = mClassList,
                    this
                ).let {
                    mRecyclerView.apply {
                        isVisible = true
                        isAnimating
                        layoutManager = LinearLayoutManager(context)
                        hasFixedSize()
                        adapter = it
                    }

                }
                mTitle.text = context.getString(R.string.select_class)
                mErrorMessage.isVisible = false

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onNameClick(holder: FilterLevelClassAdapter.ViewHolder) {

        holder.itemView.setOnClickListener {

            if (mLevelList.isNotEmpty()) {
                levelItemPosition = mLevelList[holder.adapterPosition]

                onItemClick(
                    context,
                    levelItemPosition,
                    selectedItems,
                    holder.itemTextLayout,
                    holder.itemImageLayout,
                    buttonView = mDoneBtn,
                    dismissView = mDismissBtn
                )
            } else {
                classItemPosition = mClassList[holder.adapterPosition]

                onItemClick(
                    context,
                    classItemPosition,
                    selectedItems,
                    holder.itemTextLayout,
                    holder.itemImageLayout,
                    buttonView = mDoneBtn,
                    dismissView = mDismissBtn
                )
            }

        }

        performButtonClick(selectedItems)

    }

    private fun performButtonClick(selectedItem: HashMap<String, String>) {

        mDoneBtn.setOnClickListener {

            if (mLevelList.isNotEmpty()) {
                sTimeFrameDataModel.levelData = getSelectedItem(selectedItem, "level")
            } else {
                sTimeFrameDataModel.classData = getSelectedItem(selectedItem, "class")
            }

            dismiss()

        }

    }

    override fun dismiss() {
        super.dismiss()
        sDismiss()
    }


}