package com.digitaldream.linkskool.activities

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter2
import com.digitaldream.linkskool.fragments.PostCommentDialogFragment
import com.digitaldream.linkskool.models.AdminCommentsModel
import com.digitaldream.linkskool.models.AdminDashboardModel
import com.digitaldream.linkskool.utils.FunctionUtils.capitaliseFirstLetter
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class QuestionViewActivity : AppCompatActivity(R.layout.activity_question_view) {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var backButton: CardView
    private lateinit var usernameTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var questionImageView: ImageView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentButton: LinearLayout
    private lateinit var errorTextView: TextView
    private lateinit var noOfLikesTextView: TextView
    private lateinit var noOfCommentsTextView: TextView
    private lateinit var noOfRepostTextView: TextView

    private var questionModel: AdminDashboardModel? = null
    private val commentList = mutableListOf<AdminCommentsModel>()
    private lateinit var commentsAdapter: GenericAdapter2<AdminCommentsModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

        processData()

    }

    private fun initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        backButton = findViewById(R.id.backButton)
        usernameTextView = findViewById(R.id.usernameTxt)
        dateTextView = findViewById(R.id.dateTxt)
        questionTextView = findViewById(R.id.questionTextView)
        questionImageView = findViewById(R.id.questionImageView)
        commentRecyclerView = findViewById(R.id.commentsRecyclerView)
        commentButton = findViewById(R.id.commentButton)
        errorTextView = findViewById(R.id.feedbackTextView)
        noOfLikesTextView = findViewById(R.id.likesTextView)
        noOfCommentsTextView = findViewById(R.id.commentsTextView)
        noOfRepostTextView = findViewById(R.id.repostTextView)

        questionModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("question_object", AdminDashboardModel::class.java)
        } else {
            intent.getSerializableExtra("question_object") as AdminDashboardModel
        }

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        commentButton.setOnClickListener {
            PostCommentDialogFragment().show(supportFragmentManager, "")
        }
    }

    private fun processData() {
        questionModel?.let { questionModel ->
            questionTextView.text = questionModel.question
            usernameTextView.text = capitaliseFirstLetter(questionModel.username)
            dateTextView.text = formatDate2(questionModel.date, "custom")
            noOfLikesTextView.text = questionModel.likesNo
            noOfCommentsTextView.text = questionModel.commentsNo
            noOfRepostTextView.text = questionModel.noOfShared

            getComments(questionModel.id)
        }
    }


    private fun getComments(questionId: String) {
        val url = "${getString(R.string.base_url)}/getAnswers.php"
        val data = hashMapOf("id" to questionId)

        sendRequestToServer(Request.Method.POST, url, this, data, object : VolleyCallback {
            override fun onResponse(response: String) {
                hideFeedbackTextView()

                if (response == "[]") {
                    showFeedbackTexView(getString(R.string.no_comments_yet))
                } else {
                    parseComment(response)
                }
            }

            override fun onError(error: VolleyError) {
                showFeedbackTexView(getString(R.string.no_internet))
            }
        })
    }


    private fun parseComment(comments: String) {
        try {
            with(JSONObject(comments)) {
                for (key in keys()) {
                    getJSONArray(key).getJSONObject(0).let { jsonObject ->
                        val id = jsonObject.getString("id")
                        val body = jsonObject.getString("body")
                        val authorName = jsonObject.getString("author_name")
                        val datePosted = jsonObject.getString("upload_date")

                        with(JSONArray(body)) {
                            for (j in 0 until length()) {
                                getJSONObject(j).let { bodyObject ->
                                    val bodyType = bodyObject.getString("type")
                                    var content = ""
                                    var imageUrl = ""

                                    if (bodyType.equals("text", ignoreCase = true)) {
                                        content = bodyObject.getString("content")
                                    } else if (bodyType.equals("image", ignoreCase = true)) {
                                        imageUrl = bodyObject.getString("src")
                                    }

                                    val commentsModel = AdminCommentsModel(
                                        commentId = id,
                                        username = capitaliseFirstLetter(authorName),
                                        comment = content,
                                        imageUrl = imageUrl,
                                        date = formatDate2(datePosted, "custom"),
                                    )

                                    commentList.add(commentsModel)
                                }
                            }
                        }
                    }
                }

                setUpCommentsAdapter()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showFeedbackTexView(getString(R.string.no_internet))
        }
    }

    private fun setUpCommentsAdapter() {
        commentsAdapter = GenericAdapter2(
            itemResLayout = R.layout.item_admin_comments_layout,
            itemList = commentList,
            bindItem = { itemView, model, _ ->
                val usernameTxt: TextView = itemView.findViewById(R.id.usernameTxt)
                val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
                val commentTxt: TextView = itemView.findViewById(R.id.commentTxt)
                val imageCard: CardView = itemView.findViewById(R.id.imageHolder)

                usernameTxt.text = model.username
                dateTxt.text = model.date
                commentTxt.text = model.comment

                val randomTintedColor = generateRandomTintedColor()
                imageCard.setCardBackgroundColor(randomTintedColor)
            }
        )

        commentRecyclerView.apply {
            hasFixedSize()
            adapter = commentsAdapter
        }
    }

    private fun generateRandomTintedColor(): Int {
        val colorResources = listOf(
            R.color.tinted_color_icon_5,
            R.color.tinted_0, R.color.tinted_1, R.color.tinted_2, R.color.tinted_3, R.color
                .tinted_4, R.color.tinted_5, R.color.tinted_6, R.color.tinted_7
        )

        val randomColorResourceId = colorResources[Random.nextInt(colorResources.size)]

        return ContextCompat.getColor(this, randomColorResourceId)
    }


    private fun showFeedbackTexView(message: String) {
        commentList.clear()
        errorTextView.run {
            text = message
            isVisible = true
        }
    }

    private fun hideFeedbackTextView() {
        errorTextView.isVisible = false
    }

}
