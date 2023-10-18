package com.digitaldream.linkskool.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.PaymentActivity
import com.digitaldream.linkskool.adapters.DebtStudentsAdapter
import com.digitaldream.linkskool.adapters.OnItemClickListener
import com.digitaldream.linkskool.dialog.AdminClassesDialog
import com.digitaldream.linkskool.interfaces.ResultListener
import com.digitaldream.linkskool.models.AdminPaymentModel
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.json.JSONObject

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val CLASS_ID = "id"
private const val CLASS_NAME = "name"


class DebtStudentsFragment : Fragment(), OnItemClickListener {

    //declare view variables
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: DebtStudentsAdapter
    private lateinit var mTitle: TextView
    private lateinit var mClassBtn: Button
    private lateinit var mErrorMessage: TextView
    private lateinit var mRefreshBtn: Button
    private lateinit var mErrorImage: ImageView
    private lateinit var mSearchBar: EditText
    private lateinit var mBackBtn: ImageView

    private val mStudentList = mutableListOf<AdminPaymentModel>()

    private var mClassId: String? = null
    private var mClassName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mClassId = it.getString(CLASS_ID)
            mClassName = it.getString(CLASS_NAME)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(classId: String, className: String) =
            DebtStudentsFragment().apply {
                arguments = Bundle().apply {
                    putString(CLASS_ID, classId)
                    putString(CLASS_NAME, className)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_debt_students, container, false)

        mRecyclerView = view.findViewById(R.id.debt_receipt_recycler)
        mTitle = view.findViewById(R.id.toolbar_text)
        mClassBtn = view.findViewById(R.id.class_name_btn)
        mErrorImage = view.findViewById(R.id.error_image)
        mErrorMessage = view.findViewById(R.id.error_message)
        mRefreshBtn = view.findViewById(R.id.refresh_btn)
        mSearchBar = view.findViewById(R.id.search_bar)
        mBackBtn = view.findViewById(R.id.back_btn)


        mBackBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        //set class name to button
        mClassBtn.text = mClassName

        // tool bar title
        "Debt".also { mTitle.text = it }

        changeClass()

        getDebtStudents(mClassId!!)

        filterNames()

        refresh()

        return view
    }

    // change class
    private fun changeClass() {
        mClassBtn.setOnClickListener {
            AdminClassesDialog(requireContext(),
                "payment",
                "changeLevel",
                object : ResultListener {
                    override fun sendClassName(sName: String) {
                        mClassBtn.text = sName
                        mClassName = sName

                    }

                    override fun sendLevelId(sLevelId: String) {

                    }

                    override fun sendClassId(sClassId: String) {
                        mClassId = sClassId
                        getDebtStudents(sClassId)

                    }
                })
                .apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

        }

    }


    // search for a student
    private fun filterNames() {
        mSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val name = mutableListOf<AdminPaymentModel>()
                mStudentList.forEach {
                    if (it.mStudentName!!.lowercase().contains(s.toString().lowercase()))
                        name.add(it)
                }

                mAdapter.updateFilterList(name)
            }
        })
    }

    private fun refresh() {
        mRefreshBtn.setOnClickListener {
            getDebtStudents(mClassId!!)
        }
    }


    // get names of students owing in a class
    private fun getDebtStudents(classId: String) {
        val hashMap = hashMapOf<String, String>()
        val url = "${getString(R.string.base_url)}/manageReceipts.php?debt_class=$classId"

        // make a request to the server
        sendRequestToServer(Request.Method.GET, url, requireActivity(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {

                    try {
                        mStudentList.clear()

                        if (response != "[]") {

                            JSONObject(response).let {
                                val invoiceArray = it.getJSONArray("invoice")

                                for (i in 0 until invoiceArray.length()) {
                                    val invoiceObject = invoiceArray.getJSONObject(i)
                                    val invoiceId = invoiceObject.getString("tid")
                                    val studentId = invoiceObject.getString("student_id")
                                    val regNo = invoiceObject.getString("reg_no")
                                    val name = invoiceObject.getString("name")
                                    val descriptionArray =
                                        invoiceObject.getJSONArray("description")
                                    val amount =
                                        invoiceObject.getString("amount").replace(".00", "")
                                    val mClassId = invoiceObject.getString("class")
                                    val levelId = invoiceObject.getString("level")
                                    val year = invoiceObject.getString("year")
                                    val term = invoiceObject.getString("term")

                                    // pass data to the model
                                    AdminPaymentModel().apply {
                                        mStudentId = studentId
                                        mInvoice = invoiceId
                                        mRegistrationNumber = regNo
                                        mStudentName = name
                                        mJson = descriptionArray.toString()
                                        mReceivedAmount = amount
                                        mLevelName = levelId
                                        mClassName = mClassId
                                        mTerm = term
                                        mSession = year
                                    }.also { model ->
                                        mStudentList.add(model)
                                        mStudentList.sortBy { sort -> sort.mStudentName }

                                    }

                                }

                            }

                            mAdapter = DebtStudentsAdapter(
                                requireContext(),
                                mStudentList,
                                this@DebtStudentsFragment
                            )


                            mRecyclerView.apply {
                                hasFixedSize()
                                isAnimating
                                isVisible = true
                                adapter = mAdapter
                                layoutManager = LinearLayoutManager(requireContext())
                            }

                            mSearchBar.isVisible = true
                            mErrorImage.isVisible = false
                            mRefreshBtn.isVisible = false
                            mErrorMessage.isVisible = false

                        } else {
                            mSearchBar.isVisible = false
                            mRecyclerView.isVisible = false
                            mErrorImage.isVisible = true
                            mRefreshBtn.isVisible = false
                            mErrorMessage.isVisible = true
                            mErrorMessage.text = getString(R.string.no_data)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        mSearchBar.isVisible = false
                        mRecyclerView.isVisible = false
                        mErrorImage.isVisible = true
                        mRefreshBtn.isVisible = false
                        mErrorMessage.isVisible = true
                        mErrorMessage.text = getString(R.string.server_error)
                    }

                }

                override fun onError(error: VolleyError) {
                    mSearchBar.isVisible = false
                    mRecyclerView.isVisible = false
                    mErrorImage.isVisible = false
                    mRefreshBtn.isVisible = true
                    mErrorMessage.isVisible = true
                    mErrorMessage.text = getString(R.string.can_not_retrieve)

                }

            }

        )
    }

    override fun onItemClick(position: Int) {
        val model = mStudentList[position]

        startActivity(
            Intent(requireContext(), PaymentActivity::class.java)
                .putExtra("student_name", model.mStudentName)
                .putExtra("levelId", model.mLevelName)
                .putExtra("classId", model.mClassName)
                .putExtra("invoiceId", model.mInvoice)
                .putExtra("studentId", model.mStudentId)
                .putExtra("year", model.mSession)
                .putExtra("term", model.mTerm)
                .putExtra("reg_no", model.mRegistrationNumber)
                .putExtra("amount", model.mReceivedAmount)
                .putExtra("json", model.mJson)
                .putExtra("from", "debt_details")
        )

    }

}