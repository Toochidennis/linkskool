package com.digitaldream.linkskool.models;

import android.net.Uri;
import android.widget.EditText;

public class OptionsModel {
    private String optionText;
    private boolean isChecked=false;
    private String correctAnswer;
    private String correctAnswerOrder;

    private EditText editText;
    private String type;
    private Uri optionsImage;
    private String optionImageUrl;


    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getCorrectAnswerOrder() {
        return correctAnswerOrder;
    }

    public void setCorrectAnswerOrder(String sCorrectAnswerOrder) {
        correctAnswerOrder = sCorrectAnswerOrder;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Uri getOptionsImage() {
        return optionsImage;
    }

    public void setOptionsImage(Uri optionsImage) {
        this.optionsImage = optionsImage;
    }

    public String getOptionImageUrl() {
        return optionImageUrl;
    }

    public void setOptionImageUrl(String optionImageUrl) {
        this.optionImageUrl = optionImageUrl;
    }
}
