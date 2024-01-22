package com.digitaldream.linkskool.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.AdminResultDashboardActivity
import com.digitaldream.linkskool.adapters.GenericAdapter2
import com.digitaldream.linkskool.config.DatabaseHelper
import com.digitaldream.linkskool.models.ClassNameTable
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager

class ResultClassDialog(context: Context, private val levelId: String) : Dialog(context) {

    private lateinit var classRecyclerView: RecyclerView
    private lateinit var feedbackTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.windowAnimations = R.style.DialogAnimation
            setGravity(Gravity.BOTTOM)
        }

        setContentView(R.layout.dialog_result_class)

        classRecyclerView = findViewById(R.id.classRecyclerView)
        feedbackTextView = findViewById(R.id.feedbackTextView)

        getClasses()
    }


    private fun getClasses() {
        try {
            DatabaseHelper(context).use { databaseHelper ->
                val mDao: Dao<ClassNameTable, Long> = DaoManager.createDao(
                    databaseHelper.connectionSource, ClassNameTable::class.java
                )

                val classList = mDao.queryBuilder().where().eq("level", levelId).query()

                if (classList.isEmpty()) {
                    feedbackTextView.isVisible = true
                    classRecyclerView.isVisible = false
                } else {
                    setUpClassAdapter(classList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpClassAdapter(classList: MutableList<ClassNameTable>) {
        val classAdapter = GenericAdapter2(
            itemResLayout = R.layout.item_result_class,
            itemList = classList,
            bindItem = { itemView, model, _ ->
                val classNameTextView: TextView = itemView.findViewById(R.id.classNameTextView)

                classNameTextView.text = model.className

                itemView.setOnClickListener {
                    context.startActivity(
                        Intent(context, AdminResultDashboardActivity().javaClass)
                            .putExtra("classId", model.classId)
                            .putExtra("class_name", model.className)
                            .putExtra("levelId", model.level)
                    )

                    dismiss()
                }
            }
        )

        classRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(context, 2)
            adapter = classAdapter
        }
    }

}