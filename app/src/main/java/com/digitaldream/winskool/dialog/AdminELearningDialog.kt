package com.digitaldream.winskool.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.GenericAdapter
import com.digitaldream.winskool.config.DatabaseHelper
import com.digitaldream.winskool.interfaces.ELearningListener
import com.digitaldream.winskool.models.CourseTable
import com.digitaldream.winskool.models.LevelTable
import com.digitaldream.winskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.winskool.utils.FunctionUtils.getRandomColor
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager

class AdminELearningDialog(
    context: Context,
    private val onItemClick: ELearningListener
) : Dialog(context) {

    private lateinit var mLevelRecyclerView: RecyclerView
    private lateinit var mCourseRecyclerView: RecyclerView
    private lateinit var mLeftToLeftAnimation: Animation
    private lateinit var mRightToLeftAnimation: Animation
    private lateinit var mRightToRightAnimation: Animation
    private lateinit var mLeftToRightAnimation: Animation
    private lateinit var mTitle: TextView
    private lateinit var mErrorMessage: TextView
    private lateinit var mBackBtn: ImageView
    private lateinit var mDismissBtn: ImageView


    private var mCourseList = mutableListOf<CourseTable>()
    private var mLevelName: String? = null
    private var mLevelId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        setContentView(R.layout.dialog_admin_elearning)

        mLevelRecyclerView = findViewById(R.id.level_recycler)
        mCourseRecyclerView = findViewById(R.id.course_recycler)
        mTitle = findViewById(R.id.title)
        mErrorMessage = findViewById(R.id.error_message)
        mBackBtn = findViewById(R.id.back_btn)
        mDismissBtn = findViewById(R.id.dismiss_btn)

        mLeftToLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.move_left_left)
        mRightToRightAnimation = AnimationUtils.loadAnimation(context, R.anim.move_right_right)
        mRightToLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.move_right_left)
        mLeftToRightAnimation = AnimationUtils.loadAnimation(context, R.anim.move_left_right)

        mBackBtn.setOnClickListener {
            mBackBtn.isVisible = false
            "Select Level".also { mTitle.text = it }
            mLevelRecyclerView.isVisible = true
            mErrorMessage.isVisible = false
            mCourseRecyclerView.startAnimation(mRightToRightAnimation)
            mLevelRecyclerView.startAnimation(mLeftToRightAnimation)
        }

        mDismissBtn.setOnClickListener {
            onItemClick.goBackToHome()
            dismiss()
        }


        getLevels()

    }


    private fun getLevels() {
        try {
            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<LevelTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, LevelTable::class.java
            )

            val mLevelList = mDao.queryForAll()
            mLevelList.sortBy { it.levelName }

            if (mLevelList.isEmpty()) {
                mErrorMessage.isVisible = true
                mLevelRecyclerView.isVisible = false
            } else {
                GenericAdapter(
                    mLevelList,
                    R.layout.dialog_fee_term_item,
                    bindItem = { itemView, model, _ ->
                        val levelName: Button = itemView.findViewById(R.id.level_name)
                        levelName.text = model.levelName

                    }
                ) { position ->
                    val levelModel = mLevelList[position]
                    mLevelId = levelModel.levelId
                    mLevelName = levelModel.levelName
                    getCourses()

                }.let {

                    mLevelRecyclerView.apply {
                        hasFixedSize()
                        isAnimating
                        layoutManager = GridLayoutManager(context, 2)
                        adapter = it
                        isVisible = true
                    }
                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getCourses() {
        try {
            val databaseHelper =
                DatabaseHelper(context)
            val mDao: Dao<CourseTable, Long> = DaoManager.createDao(
                databaseHelper.connectionSource, CourseTable::class.java
            )
            mCourseList = mDao.queryForAll()
            mCourseList.sortBy { it.courseName }

            if (mCourseList.isEmpty()) {
                mErrorMessage.isVisible = true
                mCourseRecyclerView.isVisible = false
                mLevelRecyclerView.isVisible = false
                mBackBtn.isVisible = true
                "Select Course".also { mTitle.text = it }
            } else {
                GenericAdapter(
                    mCourseList,
                    R.layout.elearning_course_item,
                    bindItem = { itemView, model, _ ->
                        val courseName: TextView = itemView.findViewById(R.id.course_name)
                        val courseInitials: TextView = itemView.findViewById(R.id.course_initials)
                        val linearLayout: LinearLayout = itemView.findViewById(R.id.initials_bg)

                        courseName.text = capitaliseFirstLetter(model.courseName)
                        getRandomColor(linearLayout)
                        courseInitials.text = model.courseName.substring(0, 1).uppercase()
                    }
                ) { position ->
                    val courseModel = mCourseList[position]

                    onItemClick.openELearning(
                        courseModel.courseName, courseModel.courseId,
                        mLevelName!!, mLevelId!!
                    )
                    dismiss()

                }.let {
                    mCourseRecyclerView.apply {
                        hasFixedSize()
                        isAnimating
                        layoutManager = LinearLayoutManager(context)
                        adapter = it
                        isVisible = true
                    }


                    "Select Course".also { mTitle.text = it }
                    mLevelRecyclerView.startAnimation(mLeftToLeftAnimation)
                    mCourseRecyclerView.startAnimation(mRightToLeftAnimation)
                    mLevelRecyclerView.isVisible = false
                    mErrorMessage.isVisible = false
                    mBackBtn.isVisible = true
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}