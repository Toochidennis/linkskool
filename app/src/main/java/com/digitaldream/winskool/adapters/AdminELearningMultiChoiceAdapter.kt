package com.digitaldream.winskool.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.AdminELearningAttachmentDialog
import com.digitaldream.winskool.dialog.AdminELearningFilePreviewDialogFragment
import com.digitaldream.winskool.models.MultiChoiceQuestion
import com.digitaldream.winskool.models.MultipleChoiceOption
import com.digitaldream.winskool.utils.FunctionUtils
import com.digitaldream.winskool.utils.FunctionUtils.smoothScrollEditText
import java.io.File

class AdminELearningMultiChoiceAdapter(
    private val parentFragmentManager: FragmentManager,
    private val itemList: MutableList<MultipleChoiceOption>,
    private var questionModelCopy: MultiChoiceQuestion,
    private val optionsRecyclerView: RecyclerView,
    private val taskType: String
) : RecyclerView.Adapter<AdminELearningMultiChoiceAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION
    private var newItemList = HashMap<Int, MultipleChoiceOption>()
    private lateinit var newOptionModel: MultipleChoiceOption
    private val deletedOptionFileList = mutableListOf<MultipleChoiceOption>()

    init {
        if (questionModelCopy.checkedPosition != RecyclerView.NO_POSITION) {
            selectedPosition = questionModelCopy.checkedPosition
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_admin_e_learning_multi_choice_item, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val options = itemList[position]

        if (position == 0) {
            newItemList.clear()
        }

        holder.bind(options)
    }

    override fun getItemCount() = itemList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val radioButton: RadioButton = itemView.findViewById(R.id.radioButtonOption)
        private val editText: EditText = itemView.findViewById(R.id.editTextAnswer)
        private val showAttachmentPopUpBtn: ImageButton =
            itemView.findViewById(R.id.removeOptionButton)
        private val attachmentTxt: TextView = itemView.findViewById(R.id.attachmentName)
        private val removeAttachmentBtn: ImageButton =
            itemView.findViewById(R.id.removeAttachmentButton)

        fun bind(option: MultipleChoiceOption) {
            smoothScrollEditText(editText)

            if (option.attachmentName.isEmpty()) {
                editText.setText(option.optionText)
                attachmentTxt.isVisible = false
                showAttachmentPopUpBtn.isVisible = true
                removeAttachmentBtn.isVisible = false
                editText.isVisible = true

                newOptionModel = MultipleChoiceOption(
                    option.optionText,
                    (adapterPosition).toString(),
                    "",
                    null,
                    ""
                )

                newItemList[adapterPosition] = newOptionModel
            } else {
                attachmentTxt.text = option.attachmentName
                setDrawableOnTextView(attachmentTxt)
                attachmentTxt.isVisible = true
                removeAttachmentBtn.isVisible = true
                showAttachmentPopUpBtn.isVisible = false
                editText.isVisible = false

                newOptionModel = MultipleChoiceOption(
                    "",
                    (adapterPosition).toString(),
                    option.attachmentName,
                    option.attachmentUri,
                    option.previousAttachmentName
                )

                newItemList[adapterPosition] = newOptionModel
            }

            radioButton.isChecked = adapterPosition == selectedPosition

            attachmentTxt.setOnClickListener {
                previewAttachment(option.attachmentUri!!, option.attachmentName, itemView.context)
            }

            radioButtonAction(radioButton, adapterPosition)

            showAttachmentPopUpBtn.setOnClickListener { view ->
                val popUpMenu = PopupMenu(view.context, view)
                popUpMenu.inflate(R.menu.pop_menu)
                popUpMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.attach -> {
                            AdminELearningAttachmentDialog("multiple choice")
                            { _, name: String, uri: Any? ->
                                option.attachmentName = name
                                option.attachmentUri = uri
                                option.optionText = ""
                                option.optionOrder = (adapterPosition).toString()

                                newOptionModel = MultipleChoiceOption(
                                    "",
                                    (adapterPosition).toString(),
                                    name,
                                    uri,
                                    option.previousAttachmentName
                                )

                                newItemList[adapterPosition] = newOptionModel

                                if (selectedPosition == adapterPosition)
                                    questionModelCopy.correctAnswer = option.attachmentName

                                setDrawableOnTextView(attachmentTxt)

                                attachmentTxt.isVisible = true
                                removeAttachmentBtn.isVisible = true
                                showAttachmentPopUpBtn.isVisible = false
                                editText.isVisible = false

                                updateList()

                            }.show(parentFragmentManager, "")

                            true
                        }

                        R.id.delete -> {
                            removeOption(adapterPosition, itemView)
                            true
                        }

                        else -> false
                    }
                }

                popUpMenu.show()
            }

            removeAttachmentBtn.setOnClickListener { view ->
                val popUpMenu = PopupMenu(view.context, view)
                popUpMenu.inflate(R.menu.detach_menu)
                popUpMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.detach -> {
                            removeAttachmentBtn.isVisible = false
                            showAttachmentPopUpBtn.isVisible = true
                            removeOption(adapterPosition, itemView)
                            true
                        }

                        else -> false
                    }
                }

                popUpMenu.show()
            }

            setEditTextOnWatch(editText, adapterPosition)
        }
    }


    private fun setEditTextOnWatch(
        editText: EditText,
        position: Int
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                newOptionModel = MultipleChoiceOption(
                    s.toString(),
                    (position).toString(),
                    "",
                    null,
                    ""
                )

                newItemList[position] = newOptionModel
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun radioButtonAction(
        radioButton: RadioButton,
        position: Int
    ) {
        radioButton.setOnClickListener {
            if (radioButton.isChecked) {
                selectedPosition = position
                questionModelCopy.checkedPosition = selectedPosition

                val optionModel = newItemList[position]
                questionModelCopy.correctAnswer =
                    optionModel?.optionText?.ifEmpty { optionModel.attachmentName }

                updateRadioButtonState(position)
            }
        }
    }

    fun addOption() {
        val optionText = ""
        val option = MultipleChoiceOption(optionText)
        itemList.add(option)
        notifyItemInserted(itemList.size - 1)
    }

    private fun removeOption(position: Int, itemView: View) {
        val focusedEditText = itemView.findFocus() as? EditText
        focusedEditText?.clearFocus()

        if (selectedPosition == position) {
            selectedPosition = RecyclerView.NO_POSITION
            questionModelCopy.checkedPosition = selectedPosition
        } else if (selectedPosition > position) {
            selectedPosition--
            questionModelCopy.checkedPosition = selectedPosition
        }

        if (taskType == "edit") {
            val deletedOptionModel = newItemList[position]
            if (deletedOptionModel?.attachmentName?.isNotEmpty() == true) {
                deletedOptionModel.attachmentName = ""
                deletedOptionFileList.add(deletedOptionModel)
            }
        }

        newItemList.remove(position)

        updateList()
    }


    private fun updateRadioButtonState(position: Int) {
        val itemCount = optionsRecyclerView.childCount
        for (i in 0 until itemCount) {
            val itemView = optionsRecyclerView.getChildAt(i)
            val radioButton: RadioButton = itemView.findViewById(R.id.radioButtonOption)
            radioButton.isChecked = i == position
        }
    }

    private fun setDrawableOnTextView(textView: TextView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(textView.context, R.drawable.ic_image24),
            null, null, null
        )
    }

    private fun previewAttachment(uri: Any, uriName: String, context: Context) {
        try {
            when (uri) {
                is File -> {
                    val file = File(uri.absolutePath)

                    val fileUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )

                    launchUriIntent(fileUri as Uri, context)
                }

                is String -> {
                    val isBase64 = FunctionUtils.isBased64(uri)

                    if (isBase64) {
                        val bitmap = FunctionUtils.decodeBase64ToBitmap(uri)
                        if (bitmap != null) {
                            launchImagePreviewDialog(bitmap, uriName)
                        }
                    } else {
                        val url = "${context.getString(R.string.base_url)}/$uri"
                        launchImagePreviewDialog(url, uriName)
                    }
                }

                else -> {
                    launchUriIntent(uri as Uri, context)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context, "Error occurred while viewing the file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun launchUriIntent(uri: Uri, context: Context) {
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.let {
            context.startActivity(it)
        }
    }

    private fun launchImagePreviewDialog(uri: Any, uriName: String) {
        AdminELearningFilePreviewDialogFragment(file = uri, uriName)
            .show(parentFragmentManager, "")
    }

    private fun updateList() {
        itemList.clear()
        newItemList.forEach { map ->
            itemList.add(map.value)
        }
        notifyDataSetChanged()
    }

    fun prepareOptions(context: Context): Boolean {
        return if (newItemList.isEmpty()) {
            Toast.makeText(context, "Please set at least one option", Toast.LENGTH_SHORT).show()
            false
        } else if (selectedPosition == RecyclerView.NO_POSITION) {
            Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
            false
        } else {
            val optionList = mutableListOf<MultipleChoiceOption>()

            newItemList.forEach { map ->
                optionList.add(map.value)
            }

            if (deletedOptionFileList.isNotEmpty()) {
                optionList.addAll(deletedOptionFileList)
            }

            questionModelCopy.options = optionList

            true
        }

    }
}