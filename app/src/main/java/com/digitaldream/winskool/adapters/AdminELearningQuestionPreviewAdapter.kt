package com.digitaldream.winskool.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.MultiChoiceQuestion
import com.digitaldream.winskool.models.MultipleChoiceOption
import com.digitaldream.winskool.models.QuestionItem
import com.digitaldream.winskool.models.ShortAnswerModel
import com.squareup.picasso.Picasso
import timber.log.Timber

class AdminELearningQuestionPreviewAdapter(
    private var questionList: MutableList<QuestionItem?>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        private const val VIEW_TYPE_MULTI_CHOICE_OPTION = 1
        private const val VIEW_TYPE_SHORT_ANSWER = 2
    }

    private var picasso = Picasso.get()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MULTI_CHOICE_OPTION -> {
                val view = inflater.inflate(
                    R.layout.item_multi_choice_option_layout, parent,
                    false
                )
                MultipleChoiceViewHolder(view)
            }

            VIEW_TYPE_SHORT_ANSWER -> {
                val view = inflater.inflate(
                    R.layout.item_short_answer_layout, parent,
                    false
                )
                ShortAnswerViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type: $viewType")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val questionItem = questionList[position]
        when (holder) {
            is MultipleChoiceViewHolder -> {
                val question = (questionItem as? QuestionItem.MultiChoice)?.question ?: return
                holder.bind(question)
            }

            is ShortAnswerViewHolder -> {
                val question = (questionItem as? QuestionItem.ShortAnswer)?.question ?: return
                holder.bind(question)
            }
        }
    }

    override fun getItemCount() = questionList.size

    override fun getItemViewType(position: Int): Int {
        return when (questionList[position]) {
            is QuestionItem.MultiChoice -> VIEW_TYPE_MULTI_CHOICE_OPTION
            is QuestionItem.ShortAnswer -> VIEW_TYPE_SHORT_ANSWER
            else -> position
        }
    }

    inner class MultipleChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTxt: TextView = itemView.findViewById(R.id.questionTxt)
        private val questionImage: ImageView = itemView.findViewById(R.id.questionImage)
        private val optionRecyclerView: RecyclerView =
            itemView.findViewById(R.id.optionsRecyclerView)

        fun bind(multiItem: MultiChoiceQuestion) {
            questionTxt.text = multiItem.questionText
            loadImage(itemView.context, multiItem.attachmentUri, questionImage)

            multiItem.options?.let {
                optionsAdapter(it, optionRecyclerView)
            }
        }

    }

    inner class ShortAnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTxt: TextView = itemView.findViewById(R.id.questionTxt)
        private val questionImage: ImageView = itemView.findViewById(R.id.questionImage)
        private val answerEditText: EditText = itemView.findViewById(R.id.answerEditText)

        fun bind(shortAnswer: ShortAnswerModel) {
            questionTxt.text = shortAnswer.questionText
            loadImage(itemView.context, shortAnswer.attachmentUri, questionImage)
            answerEditText.setText(shortAnswer.answerText)
        }

    }


    private fun optionsAdapter(
        options: MutableList<MultipleChoiceOption>,
        recyclerView: RecyclerView
    ) {
        val labelList =
            arrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', '0', 'P')

        GenericAdapter2(
            options,
            R.layout.item_options_layout,
            bindItem = { itemView, model, position ->
                val optionLabel: TextView = itemView.findViewById(R.id.optionsLabel)
                val optionTxt: TextView = itemView.findViewById(R.id.optionsTxt)
                val optionImage: ImageView = itemView.findViewById(R.id.optionImage)

                optionLabel.text = labelList[position].toString()

                if (model.optionText.isEmpty()) {
                    loadImage(itemView.context, model.attachmentUri, optionImage)
                    optionTxt.isVisible = false
                } else {
                    optionTxt.text = model.optionText
                    optionTxt.isVisible = true
                    optionImage.isVisible = false
                }

                itemView.alpha = 0f
                itemView.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setStartDelay(position * 150L)
                    .start()
            }
        ).let {
            recyclerView.apply {
                hasFixedSize()
                layoutManager = LinearLayoutManager(context)
                adapter = it
            }
        }

    }

    private fun isBased64(encodedString: String): Boolean {
        return try {
            org.apache.commons.codec.binary.Base64.isBase64(encodedString)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    private fun loadImage(context: Context, imageUri: Any?, imageView: ImageView) {
        try {
            when (imageUri) {
                is String -> {
                    if (imageUri.isNotEmpty()) {
                        val isBase64 = isBased64(imageUri)

                        if (isBase64) {
                            val bitmap = decodeBase64(imageUri)
                            imageView.isVisible = bitmap != null
                            imageView.setImageBitmap(bitmap)
                        } else {
                            val url = "${context.getString(R.string.base_url)}/$imageUri"
                            picasso.load(url).into(imageView)
                            imageView.isVisible = true
                        }

                    } else {
                        imageView.isVisible = false
                    }
                }

                is Uri -> {
                    picasso.load(imageUri).into(imageView)
                    imageView.isVisible = true
                }

                else -> imageView.isVisible = false

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun decodeBase64(encodedString: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
            Timber.tag("decode").d("$decodedBytes")
            Bitmap.createBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}