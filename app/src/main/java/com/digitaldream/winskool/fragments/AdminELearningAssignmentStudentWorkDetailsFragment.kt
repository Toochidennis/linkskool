package com.digitaldream.winskool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.winskool.R
import com.digitaldream.winskool.adapters.AdminELearningFilesAdapter
import com.digitaldream.winskool.models.AttachmentModel
import com.digitaldream.winskool.utils.FileViewModel
import com.digitaldream.winskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.winskool.utils.VolleyCallback
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningAssignmentStudentWorkDetailsFragment : Fragment() {

    private lateinit var detailsRecyclerView: RecyclerView
    private lateinit var markEditText: EditText
    private lateinit var returnBtn: Button

    private lateinit var attachmentAdapter: AdminELearningFilesAdapter
    private lateinit var fileViewModel: FileViewModel
    private var attachmentList = mutableListOf<AttachmentModel>()

    private var responseId: String? = null
    private var param2: String? = null
    private var menuHost: MenuHost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            responseId = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        fileViewModel = ViewModelProvider(this)[FileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_admin_e_learning_assignment_student_work_details,
            container,
            false
        )
    }

    companion object {

        @JvmStatic
        fun newInstance(id: String, param2: String = "") =
            AdminELearningAssignmentStudentWorkDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, id)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        setUpMenu()

        getResponseDetails()
    }

    private fun setUpViews(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            detailsRecyclerView = findViewById(R.id.detailsRecyclerView)
            markEditText = findViewById(R.id.scoreEditText)
            returnBtn = findViewById(R.id.returnBtn)

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            menuHost = requireActivity()

            actionBar?.apply {
                title = ""
                setHomeButtonEnabled(true)
                setDisplayHomeAsUpEnabled(true)
            }

            toolbar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        }
    }


    private fun getResponseDetails() {
        val url =
            "${requireActivity().getString(R.string.base_url)}/getResponseDetails.php?id=$responseId"

        sendRequestToServer(
            Request.Method.GET, url, requireContext(), null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    if (response != "{}") {
                        parseAnswerResponse(response)
                    }
                }

                override fun onError(error: VolleyError) {

                }
            }, false
        )
    }

    private fun parseAnswerResponse(response: String) {
        try {
            JSONObject(response).run {
                val responseArray = getString("response")

                with(JSONArray(responseArray)) {
                    for (i in 0 until length()) {
                        getJSONObject(i).let {
                            val fileName = trimText(it.getString("file_name"))
                            val type = it.getString("type")
                            val file = it.getString("file_name")

                            val attachmentModel = AttachmentModel(fileName, "", type, file)

                            attachmentList.add(attachmentModel)
                        }
                    }
                }
            }

            setUpRecyclerView()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpRecyclerView() {
        attachmentAdapter =
            AdminELearningFilesAdapter(parentFragmentManager, attachmentList, fileViewModel)

        detailsRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = attachmentAdapter
        }
    }


    private fun trimText(text: String): String {
        return text.replace("../assets/elearning/practice/", "").ifEmpty { "" }
    }

    private fun setUpMenu() {
        menuHost?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        })
    }
}