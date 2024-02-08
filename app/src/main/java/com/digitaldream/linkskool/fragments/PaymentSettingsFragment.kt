package com.digitaldream.linkskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.PaymentActivity
import com.digitaldream.linkskool.dialog.TermFeeDialog


class PaymentSettingsFragment : Fragment(R.layout.fragment_settings_payment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar

            actionBar?.apply {
                title = "Payment Settings"

                toolbar.setNavigationOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }

            val feeBtn = view.findViewById<CardView>(R.id.fee_settings)
            val termBtn: CardView = view.findViewById(R.id.term_settings)
            val accountBtn: CardView = view.findViewById(R.id.account_settings)

            feeBtn.setOnClickListener {
                startActivity(
                    Intent(context, PaymentActivity().javaClass).putExtra(
                        "from",
                        "fee_settings"
                    )
                )

            }

            termBtn.setOnClickListener {
                TermFeeDialog(requireContext(), "term", null)
                    .apply {
                        setCancelable(true)
                        show()
                    }.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            }

            accountBtn.setOnClickListener {
                startActivity(
                    Intent(context, PaymentActivity().javaClass).putExtra(
                        "from",
                        "account_settings"
                    )
                )
            }

        }
    }
}