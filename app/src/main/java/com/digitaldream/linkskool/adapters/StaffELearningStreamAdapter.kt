package com.digitaldream.linkskool.adapters

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.StaffELearningActivity
import com.digitaldream.linkskool.activities.StudentELearningActivity
import com.digitaldream.linkskool.models.CommentDataModel
import com.digitaldream.linkskool.models.ContentModel
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.getDate
import com.digitaldream.linkskool.utils.FunctionUtils.hideKeyboard
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class StaffELearningStreamAdapter(
    private val context: Context,
    private val itemList: MutableList<ContentModel>,
    private val commentItemMap: HashMap<String, MutableList<CommentDataModel>>
) : RecyclerView.Adapter<StaffELearningStreamAdapter.ContentViewModel>() {

    private var userName: String? = null
    private var userId: String? = null
    private var year: String? = null
    private var courseName: String? = null

    private var newCommentItemMap = hashMapOf<String, MutableList<CommentDataModel>>()
    private lateinit var commentAdapter: StudentELearningStreamCommentAdapter
    private var refreshJob: Job? = null
    private val sharedPreferences = context.getSharedPreferences("loginDetail", MODE_PRIVATE)

    init {
        startPeriodicRefresh(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewModel {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_stream_layout, parent, false)

        return ContentViewModel(view)
    }

    override fun onBindViewHolder(holder: ContentViewModel, position: Int) {
        val itemModel = itemList[position]
        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size

    inner class ContentViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val commentRecyclerView: RecyclerView =
            itemView.findViewById(R.id.commentRecyclerView)
        private val commentEditText: EditText = itemView.findViewById(R.id.commentEditText)
        private val sendBtn: ImageButton = itemView.findViewById(R.id.sendBtn)

        fun bind(contentModel: ContentModel) {
            setImageResource(imageView, contentModel)

            val description = "${contentModel.description} ${contentModel.title}"
            descriptionTxt.text = description

            val formattedDate = formatDate2(contentModel.date, "custom")
            dateTxt.text = formattedDate

            loadComment(contentModel)

            viewContentDetails(itemView, contentModel)
        }

        private fun loadComment(contentModel: ContentModel) {
            loadInitialContentComment(commentRecyclerView, contentModel)

            editTextWatcher(commentEditText, sendBtn)
            sendComment(contentModel, sendBtn, commentEditText, commentRecyclerView)
        }
    }

    private fun setImageResource(imageView: ImageView, contentModel: ContentModel) {
        imageView.setImageResource(
            when (contentModel.viewType) {
                "material" -> R.drawable.ic_material
                "question" -> R.drawable.ic_question
                else -> R.drawable.baseline_assignment_24
            }
        )

        imageView.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }

    private fun showSendBtn(imageButton: ImageButton) {
        imageButton.isVisible = true
    }

    private fun hideSendBtn(imageButton: ImageButton) {
        imageButton.isVisible = false
    }

    private fun viewContentDetails(itemView: View, contentModel: ContentModel) {
        itemView.setOnClickListener {
            val url =
                "${it.context.getString(R.string.base_url)}/getContent.php?" +
                        "id=${contentModel.id}&type=${contentModel.type}"

            sendRequest(url) { response ->
                launchActivity(contentModel.viewType, response)
            }
        }
    }

    private fun sendRequest(
        url: String,
        method: Int = Request.Method.GET,
        isShowProgressBar: Boolean = true,
        data: HashMap<String, String>? = null,
        onResponse: (String) -> Unit
    ) {
        sendRequestToServer(
            method,
            url,
            context,
            data,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    onResponse(response)
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        context, "Something went wrong please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, isShowProgressBar
        )
    }


    private fun launchActivity(from: String, response: String) {
        context.startActivity(
            Intent(context, StaffELearningActivity::class.java)
                .putExtra("from", "${from}_dashboard")
                .putExtra("json", response)
                .putExtra("task", "edit")
        )
    }

    private fun editTextWatcher(editText: EditText, imageButton: ImageButton) {
        hideSendBtn(imageButton)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotBlank()) {
                    showSendBtn(imageButton)
                } else {
                    hideSendBtn(imageButton)
                }
            }
        })
    }

    private fun prepareComment(commentEditText: EditText, contentModel: ContentModel) {
        with(sharedPreferences) {
            userId = getString("user_id", "")
            userName = getString("user", "")
            year = getString("school_year", "")
            courseName = getString("course_name", "")
        }

        val message = commentEditText.text.toString().trim()

        val commentDataModel = CommentDataModel(
            "", userId ?: "", contentModel.id,
            userName ?: "", message, getDate()
        )

        updateCommentData(commentDataModel, contentModel.id, newCommentItemMap)
        updateCommentData(commentDataModel, contentModel.id, commentItemMap)

        hideKeyboard(commentEditText, commentEditText.context)
    }

    private fun setUpCommentRecyclerView(
        recyclerView: RecyclerView,
        contentModel: ContentModel,
        commentData: MutableList<CommentDataModel>? = null
    ) {
        if (commentData == null) {
            val commentList = returnCommentList(contentModel.id)
            if (commentList != null) {
                commentList.sortBy { it.date }
                commentAdapter = StudentELearningStreamCommentAdapter(commentList)
            }
        } else {
            commentData.sortBy { it.date }
            commentAdapter = StudentELearningStreamCommentAdapter(commentData)
        }

        recyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(recyclerView.context)
            adapter = commentAdapter
        }
    }

    private fun sendComment(
        contentModel: ContentModel,
        imageButton: ImageButton,
        editText: EditText,
        recyclerView: RecyclerView
    ) {
        imageButton.setOnClickListener {
            prepareComment(editText, contentModel)
            setUpCommentRecyclerView(recyclerView, contentModel)

            postComment(contentModel)
        }
    }

    private fun prepareCommentJson(contentModel: ContentModel): HashMap<String, String> {
        val commentList = newCommentItemMap[contentModel.id]

        return HashMap<String, String>().apply {
            put("content_id", contentModel.id)
            put("author_id", userId ?: "")
            put("author_name", userName ?: "")

            commentList?.forEach { commentData ->
                put("comment", commentData.comment)
            }

            put("content_title", contentModel.title)
            put("level", contentModel.levelId)
            put("course", contentModel.courseId)
            put("course_name", courseName ?: "")
            put("term", contentModel.term)
            put("year", year ?: "")
            put("content_type", contentModel.type)
        }
    }

    private fun postComment(contentModel: ContentModel) {
        val commentHashMap = prepareCommentJson(contentModel)
        val url = "${context.getString(R.string.base_url)}/addContentComment.php"

        sendRequest(url, Request.Method.POST, false, commentHashMap) {}
    }

    private fun loadInitialContentComment(recyclerView: RecyclerView, contentModel: ContentModel) {
        val commentList = commentItemMap[contentModel.id]

        if (commentList != null) {
            commentList.sortBy { it.date }
            setUpCommentRecyclerView(recyclerView, contentModel, commentList)
        }
    }

    private fun updateCommentData(
        comment: CommentDataModel,
        contentId: String,
        map: HashMap<String, MutableList<CommentDataModel>>
    ) {
        val newCommentList =
            mutableListOf<CommentDataModel>().apply {
                add(comment)
            }

        val previousCommentList = map[contentId]

        if (previousCommentList == null) {
            map[contentId] = newCommentList
        } else {
            previousCommentList.addAll(newCommentList)
        }
    }

    private fun returnCommentList(commentId: String) = commentItemMap[commentId]

    private fun getContentComment(context: Context) {
        val contentModel = itemList[0]
        val url =
            "${context.getString(R.string.base_url)}/getContentComment" +
                    ".php?level=${contentModel.levelId}" +
                    "&course=${contentModel.courseId}&term=${contentModel.term}"

        sendRequest(url, Request.Method.GET, false) { response ->
            parseCommentResponse(response)
        }
    }

    private fun parseCommentResponse(response: String) {
        try {
            commentItemMap.clear()

            with(JSONArray(response)) {
                for (i in 0 until length()) {
                    getJSONObject(i).let {
                        val commentId = it.getString("id")
                        val contentId = it.getString("content_id")
                        val comment = it.getString("body")
                        val userId = it.getString("author_id")
                        val userName = it.getString("author_name")
                        val date = it.getString("upload_date")

                        val commentModel =
                            CommentDataModel(
                                commentId, userId,
                                contentId, userName,
                                comment, date
                            )

                        updateCommentData(commentModel, contentId, commentItemMap)
                    }
                }
            }

            notifyDataSetChanged()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startPeriodicRefresh(context: Context) {
        val delayMillis = 60000L // 1 minute
        refreshJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {

                withContext(Dispatchers.Main) {
                    getContentComment(context)
                }

                delay(delayMillis)
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        refreshJob?.cancel()
        refreshJob = null
    }

}