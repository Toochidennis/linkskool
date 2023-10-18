package com.digitaldream.linkskool.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class CustomViewPager2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    private var isCustomBarVisible = false

    fun setCustomBarVisibility(visible: Boolean) {
        isCustomBarVisible = visible
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return !isCustomBarVisible && super.onTouchEvent(ev)
    }

}