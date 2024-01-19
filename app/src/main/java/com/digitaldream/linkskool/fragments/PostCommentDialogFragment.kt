package com.digitaldream.linkskool.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.digitaldream.linkskool.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PostCommentDialogFragment : DialogFragment(R.layout.fragment_post_comment_dialog) {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostCommentDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}