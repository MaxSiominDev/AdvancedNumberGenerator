package dev.maxsiomin.advancednumbergenerator.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.maxsiomin.advancednumbergenerator.util.UiActions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(uiActions: UiActions) : ViewModel(), UiActions by uiActions

fun stringMutableLiveData(value: String? = null): MutableLiveData<String> {
    return MutableLiveData<String>().apply {
        if (value != null)
            this.value = value
    }
}
