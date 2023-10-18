package com.digitaldream.linkskool.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class SharedViewModel : ViewModel() {
    private val _customActionBarVisible = MutableLiveData<Boolean>()

    val customActionBarVisible: LiveData<Boolean>
        get() = _customActionBarVisible

    fun showCustomActionBar() {
        _customActionBarVisible.value = true
    }

    fun hideCustomActionBar() {
        _customActionBarVisible.value = false
    }
}