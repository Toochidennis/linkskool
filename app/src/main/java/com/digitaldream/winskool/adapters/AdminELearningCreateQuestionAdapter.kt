package com.digitaldream.winskool.adapters


import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.AdminELearningSectionDialog
import com.digitaldream.winskool.dialog.AdminELearningShortAnswerDialogFragment
import com.digitaldream.winskool.fragments.AdminELearningMultiChoiceDialogFragment
import com.digitaldream.winskool.interfaces.ItemTouchHelperAdapter
import com.digitaldream.winskool.models.MultiChoiceQuestion
import com.digitaldream.winskool.models.QuestionItem
import com.digitaldream.winskool.models.SectionModel
import com.digitaldream.winskool.models.ShortAnswerModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections

class AdminELearningCreateQuestionAdapter(
    private val fragmentManager: FragmentManager,
    private val itemList: MutableList<SectionModel>,
    private val taskType: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    private companion object {
        private const val VIEW_TYPE_SECTION = 1
        private const val VIEW_TYPE_QUESTION = 2
        private const val VIEW_TYPE_SHORT_QUESTION = 3
    }

    private val viewHolderList = mutableListOf<RecyclerView.ViewHolder>()
    private val deletedItemList = mutableListOf<SectionModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_SECTION -> {
                val view = inflater.inflate(
                    R.layout.item_section, parent,
                    false
                )
                SectionViewHolder(view)
            }

            VIEW_TYPE_QUESTION -> {
                val view = inflater.inflate(
                    R.layout.item_multi_choice_option, parent,
                    false
                )
                MultiChoiceViewHolder(view)
            }

            VIEW_TYPE_SHORT_QUESTION -> {
                val view = inflater.inflate(
                    R.layout.item_short_answer, parent,
                    false
                )
                ShortQuestionViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type $viewType")
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sectionModel = itemList[position]

        when (holder) {
            is SectionViewHolder -> {
                holder.bind(sectionModel)
            }

            is MultiChoiceViewHolder -> {
                viewHolderList.add(holder)
                val question =
                    (sectionModel.questionItem as QuestionItem.MultiChoice).question
                holder.bind(question)
            }

            is ShortQuestionViewHolder -> {
                viewHolderList.add(holder)
                val question =
                    (sectionModel.questionItem as QuestionItem.ShortAnswer).question
                holder.bind(question)
            }
        }

    }

    override fun getItemCount() = itemList.size

    override fun getItemViewType(position: Int): Int {
        val sectionModel = itemList[position]

        return when (sectionModel.viewType) {
            "section" -> VIEW_TYPE_SECTION
            "multiple_choice" -> VIEW_TYPE_QUESTION
            "short_answer" -> VIEW_TYPE_SHORT_QUESTION
            else -> position
        }
    }

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionTxt: TextView = itemView.findViewById(R.id.sectionTxt)
        private val sectionBtn: ImageButton = itemView.findViewById(R.id.sectionButton)

        fun bind(sectionModel: SectionModel) {
            sectionTxt.text = sectionModel.sectionTitle

            sectionAction(sectionBtn, sectionModel, adapterPosition)

            itemView.setOnClickListener {
                sectionEdit(sectionModel, itemView)
            }

            itemView.setOnLongClickListener {
                viewHolderList.forEach { viewHolder ->
                    val layoutParams = viewHolder.itemView.layoutParams
                    layoutParams.height = 0
                    viewHolder.itemView.layoutParams = layoutParams
                }
                true
            }
        }
    }

    inner class MultiChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTextView: TextView = itemView.findViewById(R.id.questionTxt)
        private val questionButton: ImageButton = itemView.findViewById(R.id.moreBtn)

        fun bind(multiItem: MultiChoiceQuestion) {
            "Multiple choice".let { questionTextView.text = it }

            itemView.setOnClickListener {
                multiChoiceItemClick(multiItem, adapterPosition)
            }

            questionButtonAction(
                questionButton, ShortAnswerModel(), multiItem,
                adapterPosition, "multi"
            )
        }
    }

    inner class ShortQuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTextView: TextView = itemView.findViewById(R.id.questionTxt)
        private val questionButton: ImageButton = itemView.findViewById(R.id.moreBtn)

        fun bind(shortAnswer: ShortAnswerModel) {
            "Short answer".let { questionTextView.text = it }

            itemView.setOnClickListener {
                shortAnswerItemClick(shortAnswer, adapterPosition)
            }

            questionButtonAction(
                questionButton, shortAnswer, MultiChoiceQuestion(),
                adapterPosition, "short"
            )
        }
    }

    private fun sectionAction(
        sectionBtn: ImageView,
        item: SectionModel,
        position: Int
    ) {
        sectionBtn.setOnClickListener { view ->
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.section_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editSection -> {
                        sectionEdit(item, view)
                        true
                    }

                    R.id.deleteSection -> {
                        deleteItem(position)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun sectionEdit(sectionModel: SectionModel, view: View) {
        sectionModel.sectionTitle?.let {
            AdminELearningSectionDialog(view.context, it) { updateSection ->
                sectionModel.sectionTitle = updateSection

                notifyDataSetChanged()
            }.apply {
                setCancelable(true)
                show()
            }.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun questionButtonAction(
        questionBtn: ImageView,
        shortAnswer: ShortAnswerModel,
        multiItem: MultiChoiceQuestion,
        position: Int,
        from: String
    ) {
        questionBtn.setOnClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.inflate(R.menu.section_menu)
            var updatedQuestionItem: Any
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editSection -> {
                        if (from == "short") {
                            AdminELearningShortAnswerDialogFragment(shortAnswer) { question ->
                                updatedQuestionItem = QuestionItem.ShortAnswer(question)
                                itemList[position].questionItem =
                                    updatedQuestionItem as QuestionItem
                                    .ShortAnswer
                                notifyItemChanged(position)
                            }.show(fragmentManager, "")
                        } else {
                            AdminELearningMultiChoiceDialogFragment(multiItem) { question ->
                                updatedQuestionItem = QuestionItem.MultiChoice(question)
                                itemList[position].questionItem =
                                    updatedQuestionItem as QuestionItem.MultiChoice
                                notifyItemChanged(position)
                            }.show(fragmentManager, "")
                        }
                        true
                    }

                    R.id.deleteSection -> {
                        deleteItem(position)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()

        }
    }


    private fun shortAnswerItemClick(shortAnswer: ShortAnswerModel, position: Int) {
        AdminELearningShortAnswerDialogFragment(shortAnswer) { question ->
            val updatedQuestionItem = QuestionItem.ShortAnswer(question)
            itemList[position].questionItem = updatedQuestionItem
            notifyItemChanged(position)
        }.show(fragmentManager, "")
    }

    private fun multiChoiceItemClick(multiItem: MultiChoiceQuestion, position: Int) {
        AdminELearningMultiChoiceDialogFragment(multiItem) { question ->
            val updatedQuestionItem = QuestionItem.MultiChoice(question)
            itemList[position].questionItem = updatedQuestionItem
            notifyItemChanged(position)
        }.show(fragmentManager, "")
    }

    private fun deleteItem(position: Int) {
        itemList.removeAt(position)
        notifyDataSetChanged()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val draggedItem = itemList[fromPosition]
        val targetItem = itemList[toPosition]

        if (draggedItem.viewType == "section" && targetItem.viewType == "section") {
            val hasQuestionBelowDragged = hasQuestionsBelowSection(fromPosition)
            val hasQuestionsBelowTarget = hasQuestionsBelowSection(toPosition)

            if (hasQuestionsBelowTarget || hasQuestionBelowDragged) {
                GlobalScope.launch {
                    delay(100L)
                    swapSectionsWithAssociatedQuestions(fromPosition, toPosition)
                }
            } else {

                swapSections(fromPosition, toPosition)
            }
        } else if (draggedItem.viewType == "section" &&
            (targetItem.viewType == "short_answer" || targetItem.viewType == "multiple_choice")
        ) {
            return
        } else {
            swapSections(fromPosition, toPosition)
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(recyclerView: RecyclerView) {
        viewHolderList.forEach { viewHolder ->
            val layoutParams = viewHolder.itemView.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            viewHolder.itemView.layoutParams = layoutParams
        }
        Handler(Looper.getMainLooper()).postDelayed({
            recyclerView.post {

                notifyDataSetChanged()
            }
        }, 500)
    }


    private fun hasQuestionsBelowSection(sectionPosition: Int): Boolean {
        for (i in sectionPosition + 1 until itemList.size) {
            val item = itemList[i]
            if (item.viewType == "short_answer" || item.viewType == "multiple_choice") {
                return true
            } else if (item.viewType == "section") {
                return false
            }
        }
        return false
    }

    private fun swapSectionsWithAssociatedQuestions(fromPosition: Int, toPosition: Int) {
        val draggedSection = itemList[fromPosition]
        val targetSection = itemList[toPosition]

        // swap the associated items
        val draggedSectionItems = getAssociatedItems(draggedSection)
        val targetSectionItems = getAssociatedItems(targetSection)

        // swap sections
        itemList[fromPosition] = targetSection
        itemList[toPosition] = draggedSection

        itemList.removeAll(draggedSectionItems)
        itemList.removeAll(targetSectionItems)

        itemList.addAll(itemList.indexOf(draggedSection) + 1, draggedSectionItems)
        itemList.addAll(itemList.indexOf(targetSection) + 1, targetSectionItems)
    }


    private fun getAssociatedItems(sectionModel: SectionModel): MutableList<SectionModel> {
        val sectionPosition = itemList.indexOf(sectionModel)
        val associatedItems = mutableListOf<SectionModel>()

        for (i in sectionPosition + 1 until itemList.size) {
            val item = itemList[i]
            if (item.viewType == "short_answer" || item.viewType == "multiple_choice") {
                associatedItems.add(item)
            } else if (item.viewType == "section") {
                break
            }
        }

        return associatedItems
    }

    private fun swapSections(fromPosition: Int, toPosition: Int) {
        Collections.swap(itemList, fromPosition, toPosition)
    }

}

