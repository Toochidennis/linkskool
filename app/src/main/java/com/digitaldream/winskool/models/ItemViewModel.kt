package com.digitaldream.winskool.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItemViewModel : ViewModel() {
    private var mutableLiveData = MutableLiveData<String>()

    fun setText(s: String) {
        mutableLiveData.value = s
    }

    fun getText() = mutableLiveData;
}
