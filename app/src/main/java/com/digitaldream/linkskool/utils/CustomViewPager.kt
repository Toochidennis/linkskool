package com.digitaldream.linkskool.utils

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager


class CustomViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val h = child.measuredHeight
            if (h > height) height = h
        }
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }

    /**
     * @author ToochiDennis
     * We start by initializing the height variable to 0.
     * We iterate over all the child views of the ViewPager using a for loop.
     * For each child view, we measure its height by calling
     * child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)).
     * This tells the child to measure its width based on the parent's widthMeasureSpec,
     * but to leave its height unconstrained (i.e., MeasureSpec.UNSPECIFIED).
     * We get the measured height of the child by calling child.measuredHeight.
     *If the height of the child is greater than the current value of height, we update height to the child's height.
     * Once we've measured all the child views, we create a new heightMeasureSpec with the maximum height by calling
     * MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY).
     * Finally, we call super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
     * to set the ViewPager's dimensions based on the widthMeasureSpec
     * and the newHeightMeasureSpec we just created.
     *
     * */
}

