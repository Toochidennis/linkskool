package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.digitaldream.linkskool.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminResultDashboardFragment : Fragment(R.layout.fragment_admin_result_dashboard) {


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminResultDashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}