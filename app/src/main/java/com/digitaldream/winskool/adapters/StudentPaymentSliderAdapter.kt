package com.digitaldream.winskool.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.PagerAdapter
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.StudentPaymentModel
import com.digitaldream.winskool.utils.FunctionUtils
import java.util.*

class StudentPaymentSliderAdapter(
    private val sContext: Context,
    private val sCardList: MutableList<StudentPaymentModel>,
    private val sOnCardClickListener: OnCardClickListener,
) : PagerAdapter() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater =
            sContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view =
            layoutInflater.inflate(R.layout.fragment_student_payment_slider_item, null)

        val sessionTitle: TextView = view.findViewById(R.id.session_title)
        val termAmount: TextView = view.findViewById(R.id.term_amount)
        val viewDetails: Button = view.findViewById(R.id.view_details_btn)
        val payFee: Button = view.findViewById(R.id.btn_pay)

        val paymentModel = sCardList[position]

        when (paymentModel.getTerm()) {
            "1" -> {
                sessionTitle.text = String.format(
                    Locale.getDefault(), "%s " +
                            "%s", paymentModel.getSession(), "First Term Fees"
                )
            }

            "2" -> {
                sessionTitle.text = String.format(
                    Locale.getDefault(), "%s " +
                            "%s", paymentModel.getSession(), "Second Term Fees"
                )
            }
            "3" -> {
                sessionTitle.text = String.format(
                    Locale.getDefault(), "%s " +
                            "%s", paymentModel.getSession(), "Third Term Fees"
                )
            }
        }

        String.format(
            Locale.getDefault(), "%s%s",
            sContext.getString(R.string.naira),
            FunctionUtils.currencyFormat(
                paymentModel.getAmount()!!.toDouble()
            )
        ).also { termAmount.text = it }

        payFee.setOnClickListener {
            sOnCardClickListener.makePayment(position)
        }
        viewDetails.setOnClickListener {
            sOnCardClickListener.viewDetails(position)
        }

        container.addView(view)

        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount() = sCardList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    interface OnCardClickListener {
        fun viewDetails(position: Int)
        fun makePayment(position: Int)
    }

}