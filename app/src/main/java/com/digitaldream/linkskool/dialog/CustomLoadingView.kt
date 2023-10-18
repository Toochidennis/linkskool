package com.digitaldream.linkskool.dialog

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.ProgressBar
import com.digitaldream.linkskool.R

class CustomLoadingView(sActivity: Activity) : Dialog(sActivity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogAnimation
        window!!.setGravity(Gravity.BOTTOM)
        setContentView(R.layout.custom_loading_view)

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        ObjectAnimator.ofInt(progressBar, "progress", 100).start()


    }
}