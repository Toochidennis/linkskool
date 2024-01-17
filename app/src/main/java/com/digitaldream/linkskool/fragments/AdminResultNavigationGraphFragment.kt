package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.digitaldream.linkskool.R


class AdminResultNavigationGraphFragment :
    Fragment(R.layout.fragment_admin_result_navigation_graph) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)

        toolbar.apply {
            title = "Result"
        }

        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as? NavHostFragment

        val navController = navHostFragment?.findNavController()

        navController?.let {
            setupActionBarWithNavController((requireActivity() as AppCompatActivity), navController)
        }

    }

}