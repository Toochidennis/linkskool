package com.digitaldream.linkskool.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.AdminELearningActivity
import com.digitaldream.linkskool.interfaces.ItemTouchHelperAdapter
import com.digitaldream.linkskool.models.ContentModel
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.sendRequestToServer
import com.digitaldream.linkskool.utils.VolleyCallback
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections

class AdminELearningCourseWorkAdapter(
    private val itemList: MutableList<ContentModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    companion object {
        private const val VIEW_TYPE_TOPIC = 1
        private const val VIEW_TYPE_ASSIGNMENT = 2
        private const val VIEW_TYPE_MATERIAL = 3
        private const val VIEW_TYPE_QUESTION = 4
    }


    private val viewHolderList = mutableListOf<RecyclerView.ViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TOPIC -> {
                val view = inflater.inflate(R.layout.item_topic_layout, parent, false)
                TopicViewHolder(view)
            }

            VIEW_TYPE_ASSIGNMENT -> {
                val view = inflater.inflate(R.layout.item_content_layout, parent, false)
                AssignmentViewHolder(view)
            }

            VIEW_TYPE_MATERIAL -> {
                val view = inflater.inflate(R.layout.item_content_layout, parent, false)
                MaterialViewHolder(view)
            }

            VIEW_TYPE_QUESTION -> {
                val view = inflater.inflate(R.layout.item_content_layout, parent, false)
                QuestionViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val content = itemList[position]

        when (holder) {
            is TopicViewHolder -> holder.bind(content)

            is AssignmentViewHolder -> {
                viewHolderList.add(holder)
                holder.bind(content)
            }

            is MaterialViewHolder -> {
                viewHolderList.add(holder)
                holder.bind(content)
            }

            is QuestionViewHolder -> {
                viewHolderList.add(holder)
                holder.bind(content)
            }
        }
    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int): Int {
        val viewType = itemList[position]

        return when (viewType.viewType) {
            "topic" -> VIEW_TYPE_TOPIC
            "assignment" -> VIEW_TYPE_ASSIGNMENT
            "material" -> VIEW_TYPE_MATERIAL
            "question" -> VIEW_TYPE_QUESTION
            else -> position
        }
    }

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val topicTxt: TextView = itemView.findViewById(R.id.topicTxt)
        private val optionBtn: ImageButton = itemView.findViewById(R.id.moreBtn)

        fun bind(topic: ContentModel) {
            topicTxt.text = topic.title

            itemView.setOnLongClickListener {
                viewHolderList.forEach { viewHolder ->
                    val layoutParams = viewHolder.itemView.layoutParams
                    layoutParams.height = 0
                    viewHolder.itemView.layoutParams = layoutParams
                }
                true
            }

            menuButtonAction("topic", optionBtn, topic, itemView, adapterPosition)
        }
    }

    inner class AssignmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageType)
        private val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val optionBtn: ImageView = itemView.findViewById(R.id.moreBtn)

        fun bind(assignment: ContentModel) {
            descriptionTxt.text = assignment.title

            val date = formatDate2(assignment.date, "custom")
            "Posted $date".let { dateTxt.text = it }

            setImageResource(imageView = imageView, assignment)

            menuButtonAction("assignment", optionBtn, assignment, itemView, adapterPosition)


            itemView.setOnClickListener {
                viewOrEditContentDetails(itemView, assignment, "assignment_details")
            }

        }
    }

    inner class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageType)
        private val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val optionBtn: ImageView = itemView.findViewById(R.id.moreBtn)

        fun bind(material: ContentModel) {
            setImageResource(imageView = imageView, material)

            descriptionTxt.text = material.title

            val date = formatDate2(material.date, "custom")
            "Posted $date".let { dateTxt.text = it }

            itemView.setOnClickListener {
                viewOrEditContentDetails(itemView, material, "material_details")
            }

            menuButtonAction("material", optionBtn, material, itemView, adapterPosition)
        }
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageType)
        private val descriptionTxt: TextView = itemView.findViewById(R.id.descriptionTxt)
        private val dateTxt: TextView = itemView.findViewById(R.id.dateTxt)
        private val optionBtn: ImageView = itemView.findViewById(R.id.moreBtn)

        fun bind(question: ContentModel) {
            setImageResource(imageView = imageView, question)

            descriptionTxt.text = question.title

            val date = formatDate2(question.date, "custom")
            "Posted $date".let { dateTxt.text = it }

            menuButtonAction("question", optionBtn, question, itemView, adapterPosition)

            itemView.setOnClickListener {
                viewOrEditContentDetails(itemView, question, "question_details")
            }
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

    private fun menuButtonAction(
        from: String,
        optionBtn: ImageView,
        topicModel: ContentModel,
        itemView: View,
        position: Int
    ) {
        optionBtn.setOnClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.inflate(R.menu.section_menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editSection -> {
                        viewOrEditContentDetails(itemView, topicModel, from)
                        true
                    }

                    R.id.deleteSection -> {
                        warningDialog(itemView.context, contentModel = topicModel, position)
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
    }


    private fun viewOrEditContentDetails(
        itemView: View,
        contentModel: ContentModel,
        from: String
    ) {
        val url =
            "${itemView.context.getString(R.string.base_url)}/getContent.php?" +
                    "id=${contentModel.id}&type=${contentModel.type}"

        sendRequest(url, itemView) { response ->
            launchActivity(itemView, from, response)
        }
    }

    private fun sendRequest(
        url: String,
        itemView: View,
        onResponse: (String) -> Unit
    ) {
        sendRequestToServer(
            Request.Method.GET,
            url,
            itemView.context,
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {
                    onResponse(response)
                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        itemView.context, "Something went wrong please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun launchActivity(itemView: View, from: String, response: String) {
        itemView.context.startActivity(
            Intent(itemView.context, AdminELearningActivity::class.java)
                .putExtra("from", from)
                .putExtra("task", "edit")
                .putExtra("json", response)
        )
    }

    private fun deleteContent(context: Context, url: String) {
        sendRequestToServer(
            Request.Method.GET,
            url,
            context,
            null,
            object : VolleyCallback {
                override fun onResponse(response: String) {

                }

                override fun onError(error: VolleyError) {
                    Toast.makeText(
                        context, "Something went wrong please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun warningDialog(context: Context, contentModel: ContentModel, position: Int) {
        val title = "Delete ${contentModel.viewType}?"

        val message = when (contentModel.viewType) {
            "topic" -> "Posts with this topic will not be deleted."
            "material" -> "Comments will also be deleted."
            else -> "Marks and comments will also be deleted."
        }

        val url = "${context.getString(R.string.base_url)}/deleteContent.php?id=${contentModel.id}"

        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Delete") { _, _ ->
                deleteContent(context, url)
                itemList.removeAt(position)
                notifyItemRemoved(position)
            }

            show()
        }.create()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val draggedItem = itemList[fromPosition]
        val targetItem = itemList[toPosition]

        if (draggedItem.viewType == "topic" && targetItem.viewType == "topic") {
            val hasContentsBelowDraggedTopic = hasQuestionsBelowTopic(fromPosition)
            val hasContentsBelowTargetTopic = hasQuestionsBelowTopic(toPosition)

            if (hasContentsBelowTargetTopic || hasContentsBelowDraggedTopic) {
                GlobalScope.launch {
                    delay(100L)
                    swapTopicsWithAssociatedItems(fromPosition, toPosition)
                }

            } else {
                swapTopics(fromPosition, toPosition)
            }
        } else if (draggedItem.viewType == "topic" &&
            (targetItem.viewType == "assignment" || targetItem.viewType == "material" ||
                    targetItem.viewType == "question")
        ) {
            return
        } else {
            swapTopics(fromPosition, toPosition)
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(recyclerView: RecyclerView) {
        viewHolderList.forEach {
            val layoutParams = it.itemView.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.itemView.layoutParams = layoutParams
        }

        Handler(Looper.getMainLooper()).postDelayed({
            recyclerView.post {
                notifyDataSetChanged()
            }
        }, 500)

    }

    private fun hasQuestionsBelowTopic(topicPosition: Int): Boolean {
        for (i in topicPosition + 1 until itemList.size) {
            val item = itemList[i]
            if (item.viewType == "assignment" || item.viewType == "material"
                || item.viewType == "question"
            ) {
                return true
            } else if (item.viewType == "topic") {
                return false
            }
        }
        return false
    }

    private fun swapTopicsWithAssociatedItems(fromPosition: Int, toPosition: Int) {
        val draggedTopic = itemList[fromPosition]
        val targetTopic = itemList[toPosition]

        // get the associated items
        val draggedTopicItems = getAssociatedItems(draggedTopic)
        val targetTopicItems = getAssociatedItems(targetTopic)

        // swap topics
        // itemList[fromPosition] = targetTopic
        //   itemList[toPosition] = draggedTopic
        swapTopics(fromPosition, toPosition)

        itemList.removeAll(draggedTopicItems)
        itemList.removeAll(targetTopicItems)

        itemList.addAll(itemList.indexOf(draggedTopic) + 1, draggedTopicItems)
        itemList.addAll(itemList.indexOf(targetTopic) + 1, targetTopicItems)
    }

    private fun getAssociatedItems(topicModel: ContentModel): MutableList<ContentModel> {
        val topicPosition = itemList.indexOf(topicModel)
        val associatedItems = mutableListOf<ContentModel>()

        for (i in topicPosition + 1 until itemList.size) {
            val item = itemList[i]
            if (item.viewType == "assignment" || item.viewType == "material"
                || item.viewType == "question"
            ) {
                associatedItems.add(item)
            } else if (item.viewType == "topic") {
                break
            }
        }

        return associatedItems
    }

    private fun swapTopics(fromPosition: Int, toPosition: Int) {
        Collections.swap(itemList, fromPosition, toPosition)
    }


}