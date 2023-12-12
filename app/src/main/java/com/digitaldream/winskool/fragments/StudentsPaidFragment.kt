package com.digitaldream.winskool.fragments

import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.OnItemClickListener
import com.digitaldream.winskool.adapters.StudentsPaidAdapter
import com.digitaldream.winskool.dialog.AdminClassesDialog
import com.digitaldream.winskool.dialog.StudentsPaidBottomSheet
import com.digitaldream.winskool.interfaces.ResultListener
import com.digitaldream.winskool.models.AdminPaymentModel
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import org.json.JSONObject
import java.util.Locale

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val CLASS_NAME = "name"
private const val CLASS_ID = "id"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class StudentsPaidFragment : Fragment(), OnItemClickListener {


    //declare view variables
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: StudentsPaidAdapter
    private lateinit var mTitle: TextView
    private lateinit var mClassBtn: Button
    private lateinit var mErrorMessage: TextView
    private lateinit var mRefreshBtn: Button
    private lateinit var mErrorImage: ImageView
    private lateinit var mSearchBar: EditText
    private lateinit var mBackBtn: ImageView

    private val mStudentList = mutableListOf<AdminPaymentModel>()

    private var mClassName: String? = null
    private var mClassId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mClassName = it.getString(CLASS_NAME)
            mClassId = it.getString(CLASS_ID)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(sClassName: String, sClassId: String) =
            StudentsPaidFragment().apply {
                arguments = Bundle().apply {
                    putString(CLASS_NAME, sClassName)
                    putString(CLASS_ID, sClassId)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_students_paid, container, false)

        //initialise variables
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
        "Received".also { mTitle.text = it }

        getPaidStudents(mClassId!!)

        changeClass()

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
                        getPaidStudents(sClassId)

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
            getPaidStudents(mClassId!!)
        }
    }

    // get names of students that have paid in a class
    private fun getPaidStudents(classId: String) {
        val hashMap = hashMapOf<String, String>()
        val url = "${getString(R.string.base_url)}/manageReceipts.php?class=$classId"

        sendRequestToServer(Request.Method.GET, url, requireActivity(), hashMap,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    try {
                        mStudentList.clear()

                        if (response != "[]") {

                            // parse response to jsonObject for information retrieval if not empty
                            JSONObject(response).also {
                                val receiptArray = it.getJSONArray("receipts")

                                for (i in 0 until receiptArray.length()) {
                                    val receiptsObject = receiptArray.getJSONObject(i)
                                    val name = receiptsObject.getString("name")
                                    val levelName = receiptsObject.getString("level_name")
                                    val regNo = receiptsObject.getString("reg_no")
                                    val term = when (receiptsObject.getString("term")) {
                                        "1" -> "First Term Fees"
                                        "2" -> "Second Term Fees"
                                        else -> "Third Term Fees"
                                    }

                                    val year = receiptsObject.getString("year")
                                    val previousYear = year.toInt() - 1
                                    val session =
                                        String.format(
                                            Locale.getDefault(),
                                            "%d/%s",
                                            previousYear,
                                            year
                                        )

                                    val reference = receiptsObject.getString("reference")
                                    val amount =
                                        receiptsObject.getString("amount").replace(".00", "")
                                    val date = receiptsObject.getString("date")

                                    val model = AdminPaymentModel()
                                    model.mStudentName = name
                                    model.mLevelName = levelName
                                    model.mRegistrationNumber = regNo
                                    model.mTerm = term
                                    model.mTransactionDate = date
                                    model.mReceivedAmount = amount
                                    model.mReferenceNumber = reference
                                    model.mSession = session

                                    mStudentList.add(model)
                                    mStudentList.sortBy { sort -> sort.mStudentName }

                                }

                            }

                            mAdapter = StudentsPaidAdapter(
                                requireContext(),
                                mStudentList,
                                this@StudentsPaidFragment
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

        StudentsPaidBottomSheet.newInstance(
            model.mStudentName!!,
            model.mLevelName!!,
            mClassName!!,
            model.mRegistrationNumber!!,
            model.mTerm!!,
            model.mTransactionDate!!,
            model.mReceivedAmount!!,
            model.mReferenceNumber!!,
            model.mSession!!
        ).show(
            requireActivity().supportFragmentManager,
            "bottomSheetDialog"
        )

    }


}