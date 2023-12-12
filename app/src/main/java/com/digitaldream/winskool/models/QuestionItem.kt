package com.digitaldream.winskool.models

import androidx.recyclerview.widget.RecyclerView

sealed class QuestionItem {
    data class MultiChoice(val question: MultiChoiceQuestion) : QuestionItem()
    data class ShortAnswer(val question: ShortAnswerModel) : QuestionItem()
}

data class MultiChoiceQuestion(
    var questionId: String = "",
    var questionText: String = "",
    var attachmentName: String = "",
    var attachmentUri: Any? = null,
    var previousAttachmentName: String = "",
    var options: MutableList<MultipleChoiceOption>? = null,
    var checkedPosition: Int = RecyclerView.NO_POSITION,
    var correctAnswer: String? = null,
)

data class ShortAnswerModel(
    var questionId: String = "",
    var questionText: String = "",
    var attachmentName: String = "",
    var attachmentUri: Any? = null,
    var previousAttachmentName: String = "",
    var answerText: String = ""
)

data class MultipleChoiceOption(
    var optionText: String = "",
    var optionOrder: String = "",
    var attachmentName: String = "",
    var attachmentUri: Any? = null,
    var previousAttachmentName: String = "",
    var isSelected: Boolean = false
)

