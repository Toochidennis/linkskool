package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.AdminResultDashboardActivity
import com.digitaldream.linkskool.activities.PaymentActivity
import com.digitaldream.linkskool.adapters.DialogClassNameAdapter
import com.digitaldream.linkskool.adapters.OnClassClickListener
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.adapters.TermFeeDialogAdapter
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.interfaces.ResultListener
import com.digitaldream.linkskool.models.ClassNameTable
import com.digitaldream.linkskool.models.LevelTable
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager

class AdminClassesDialog(
    sContext: Context,
    private val sFrom: String,
    private val sType: String?,
    private val sResulClick: ResultListener?,
) : Dialog(sContext), OnItemClickListener, OnClassClickListener {

    private var mLevelList = mutableListOf<LevelTable>()
    private var mClassList = mutableListOf<ClassNameTable>()
    private lateinit var mAdapter: DialogClassNameAdapter

    private lateinit var mLevelRecyclerView: RecyclerView
    private lateinit var mClassRecyclerView: RecyclerView
    private lateinit var mLeftToLeftAnimation: Animation
    private lateinit var mRightToLeftAnimation: Animation
    private lateinit var mRightToRightAnimation: Animation
    private lateinit var mLeftToRightAnimation: Animation
    private lateinit var mTitle: TextView
    private lateinit var mErrorMessage: TextView
    private lateinit var mBackBtn: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        setContentView(R.layout.dialog_admin_classes)

        mLevelRecyclerView = findViewById(R.id.level_recycler)
        mClassRecyclerView = findViewById(R.id.class_recycler)
        mTitle = findViewById(R.id.title)
        mErrorMessage = findViewById(R.id.error_message)
        mBackBtn = findViewById(R.id.back_btn)


        mLeftToLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.move_left_left)
        mRightToRightAnimation = AnimationUtils.loadAnimation(context, R.anim.move_right_right)
        mRightToLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
        mLeftToRightAnimation = AnimationUtils.loadAnimation(context, R.anim.move_left_right)

        getLevelName()

        mBackBtn.setOnClickListener {
            mBackBtn.isVisible = false
            "Select Level".also { mTitle.text = it }
            mLevelRecyclerView.isVisible = true
            mErrorMessage.isVisible = false
            mClassRecyclerView.startAnimation(mRightToRightAnimation)
            mLevelRecyclerView.startAnimation(mLeftToRightAnimation)
        }


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
                mLevelRecyclerView.isVisible = false
            } else {
                TermFeeDialogAdapter(context, mLevelList, this).let {
                    mLevelRecyclerView.apply {
                        hasFixedSize()
                        layoutManager = GridLayoutManager(context, 2)
                        adapter = it
                        isVisible = true
                        isAnimating
                    }

                    mErrorMessage.isVisible = false

                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getClassName(sLevelId: String) {
        try {
            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, ClassNameTable::class.java
            )
            mClassList = mDao.queryBuilder().where().eq("level", sLevelId).query()

            if (mClassList.isEmpty()) {
                mErrorMessage.isVisible = true
                mClassRecyclerView.isVisible = false
                mLevelRecyclerView.isVisible = false
                mBackBtn.isVisible = true
                "Select Class".also { mTitle.text = it }
            } else {
                mAdapter = DialogClassNameAdapter(mClassList, this)
                mClassRecyclerView.hasFixedSize()
                mClassRecyclerView.layoutManager = GridLayoutManager(context, 2)
                mClassRecyclerView.adapter = mAdapter
                mClassRecyclerView.isVisible = true
                mErrorMessage.isVisible = false
                mBackBtn.isVisible = true
                "Select Class".also { mTitle.text = it }
                mLevelRecyclerView.startAnimation(mLeftToLeftAnimation)
                mClassRecyclerView.startAnimation(mRightToLeftAnimation)
                mLevelRecyclerView.isVisible = false
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onItemClick(position: Int) {
        val levelTable = mLevelList[position]

        getClassName(levelTable.levelId)

    }

    override fun onClassClick(position: Int) {
        val classTable = mClassList[position]

        if (sFrom == "result") {
            if (sType == "changeLevel") {
                sResulClick!!.sendClassName(classTable.className)
                sResulClick.sendClassId(classTable.classId)
                sResulClick.sendLevelId(classTable.level)

            } else {
                context.startActivity(
                    Intent(context, AdminResultDashboardActivity().javaClass)
                        .putExtra("classId", classTable.classId)
                        .putExtra("class_name", classTable.className)
                        .putExtra("levelId", classTable.level)
                )
            }

        } else {
            if (sType == "changeLevel") {
                sResulClick!!.sendClassName(classTable.className)
                sResulClick.sendClassId(classTable.classId)

            } else {
                context.startActivity(
                    if (sFrom == "debt")
                        Intent(context, PaymentActivity().javaClass)
                            .putExtra("classId", classTable.classId)
                            .putExtra("class_name", classTable.className)
                            .putExtra("from", "debt")
                    else
                        Intent(context, PaymentActivity().javaClass)
                            .putExtra("classId", classTable.classId)
                            .putExtra("class_name", classTable.className)
                            .putExtra("from", "paid")
                )
            }
        }

        dismiss()

    }
}