package com.digitaldream.winskool.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.ViewPager
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.SectionPagerAdapter
import com.digitaldream.winskool.fragments.DateRangeFragment
import com.digitaldream.winskool.fragments.ExpenditureFilterFragment
import com.digitaldream.winskool.fragments.ExpenditureGroupingFragment
import com.digitaldream.winskool.models.TimeFrameDataModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout

class ExpenditureTimeFrameBottomSheet(
    private val sTimeFrameDataModel: TimeFrameDataModel
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_expenditure_time_frame, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        val viewPager: ViewPager = view.findViewById(R.id.view_pager)
        val adapter = SectionPagerAdapter(childFragmentManager)
        val generateReportBtn: CardView = view.findViewById(R.id.confirm_btn)

        adapter.apply {
            addFragment(DateRangeFragment(sTimeFrameDataModel), "Date Range")
            addFragment(ExpenditureGroupingFragment(sTimeFrameDataModel), "Grouping")
            addFragment(ExpenditureFilterFragment(sTimeFrameDataModel), "Filter")

            viewPager.adapter = this
            tabLayout.setupWithViewPager(viewPager, true)
        }

        generateReportBtn.setOnClickListener {
            dismiss()
            sTimeFrameDataModel.getData()
        }
    }

}