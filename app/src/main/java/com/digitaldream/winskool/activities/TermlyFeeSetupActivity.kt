package com.digitaldream.winskool.activities

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.TermFeeAdapter
import com.digitaldream.winskool.dialog.OnInputListener
import com.digitaldream.winskool.dialog.TermlyFeeDialog
import com.digitaldream.winskool.models.TermFeesDataModel
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class TermlyFeeSetupActivity : AppCompatActivity(R.layout.activity_termly_fee_setup) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTermView: RelativeLayout
    private lateinit var mTotalView: RelativeLayout
    private lateinit var mRefreshBtn: Button
    private lateinit var mErrorMessage: TextView
    private lateinit var mTermErrorMessage: TextView
    private lateinit var mFeeTotal: TextView
    private lateinit var mLevelNameBtn: Button
    private lateinit var mSaveBtn: Button

    private var mLevel: String? = null
    private var mLevelName: String? = null
    private var mYear: String? = null
    private var mTerm: String? = null
    private var mLevelId: String? = null
    private val mTermFeesList = mutableListOf<TermFeesDataModel>()
    private lateinit var mAdapter: TermFeeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mToolbar: Toolbar = findViewById(R.id.toolbar)
        mSaveBtn = findViewById(R.id.pay_btn)
        mFeeTotal = findViewById(R.id.fee_total)
        mRecyclerView = findViewById(R.id.term_fee_recycler)
        mTermView = findViewById(R.id.term_view)
        mTotalView = findViewById(R.id.total_view)
        mRefreshBtn = findViewById(R.id.refresh_btn)
        mErrorMessage = findViewById(R.id.error_message)
        mTermErrorMessage = findViewById(R.id.term_error_message)
        mLevelNameBtn = findViewById(R.id.level_name)

        mLevel = intent.getStringExtra("level_name")
        mLevelId = intent.getStringExtra("level_id")

        val sharedPreferences =
            getSharedPreferences("loginDetail", Context.MODE_PRIVATE)
        mYear = sharedPreferences.getString("school_year", "")
        mTerm = sharedPreferences.getString("term", "")

        try {

            val previousYear = mYear!!.toInt() - 1

            val termText = when (mTerm) {
                "1" -> "First Term"
                "2" -> "Second Term"
                "3" -> "Third Term"
                else -> ""
            }

            mToolbar.apply {
                title = String.format(
                    Locale.getDefault(),
                    "%d/%s %s %s",
                    previousYear, mYear, termText, "Fees"
                )
                setNavigationIcon(R.drawable.arrow_left)
                setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
            }

            mLevelNameBtn.text = mLevel

            mAdapter = TermFeeAdapter(this, mTermFeesList, mFeeTotal, mSaveBtn, mLevelId!!)
            mRecyclerView.layoutManager = LinearLayoutManager(this)
            mRecyclerView.hasFixedSize()
            mRecyclerView.isNestedScrollingEnabled = false
            mRecyclerView.adapter = mAdapter

            getTermFees(mLevelId!!)

            refreshData()

            openDialog()

        } catch (e: Exception) {
            when (e) {
                is IndexOutOfBoundsException -> {
                    e.printStackTrace()
                    mTermView.isVisible = true
                    mTotalView.isVisible = false
                    mTermErrorMessage.isVisible = true
                }

                else -> throw e
            }
        }

    }

    private fun refreshData() {
        mRefreshBtn.setOnClickListener {
            mTermFeesList.clear()
            getTermFees(mLevelId!!)
        }

    }

    private fun getTermFees(sLevelId: String) {
        val url =
            "${getString(R.string.base_url)}/manageTermFees" +
                    ".php?list=1&&level=$sLevelId&&term=$mTerm&&year=$mYear"
        val hashMap = hashMapOf<String, String>()

        sendRequestToServer(Request.Method.GET, url, this, hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        val jsonObjects = JSONObject(response)
                        for (objects in jsonObjects.keys()) {
                            val jsonObject = jsonObjects.getJSONObject(objects)
                            val feeId = jsonObject.getString("fee_id")
                            val feeName = jsonObject.getString("fee_name")
                            val mandatory = jsonObject.getString("mandatory")
                            val feeAmount = jsonObject.getString("amount")

                            val termFeesModel = TermFeesDataModel().apply {
                                mFeedId = feeId.toInt()
                                mFeeName = feeName
                                mFeeAmount = feeAmount.replace(".00", "")
                                mMandatory = mandatory

                            }

                            mTermFeesList.add(termFeesModel)
                            mTermFeesList.sortBy { it.mFeeName }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    if (mTermFeesList.isEmpty()) {
                        mTermView.isVisible = true
                        mTotalView.isVisible = false
                        mErrorMessage.isVisible = false
                        mTermErrorMessage.isVisible = true
                        mRefreshBtn.isVisible = false
                        mTermErrorMessage.text = getString(R.string.no_data)
                    } else {
                        mTermView.isVisible = true
                        mTotalView.isVisible = true
                        mErrorMessage.isVisible = false
                        mTermErrorMessage.isVisible = false
                        mRefreshBtn.isVisible = false
                    }
                    mAdapter.notifyItemChanged(mTermFeesList.size - 1)
                }

                override fun onError(error: VolleyError) {
                    mErrorMessage.isVisible = true
                    mTermView.isVisible = false
                    mTotalView.isVisible = false
                    mRefreshBtn.isVisible = true
                    mErrorMessage.text = getString(R.string.can_not_retrieve)
                }
            })
    }

    private fun setLevelName() {
        mLevelNameBtn.text = mLevelName
    }

    private fun openDialog() {
        mLevelNameBtn.setOnClickListener {
            TermlyFeeDialog(
                this@TermlyFeeSetupActivity,
                mLevelNameBtn.text.toString(),
                object : OnInputListener {
                    override fun sendInput(input: String) {
                        mLevelName = input
                        setLevelName()
                    }

                    override fun sendId(levelId: String) {
                        mTermFeesList.clear()
                        getTermFees(levelId)
                        mAdapter = TermFeeAdapter(
                            this@TermlyFeeSetupActivity, mTermFeesList, mFeeTotal, mSaveBtn,
                            levelId
                        )
                        mRecyclerView.adapter = mAdapter
                    }
                },
            ).apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

}