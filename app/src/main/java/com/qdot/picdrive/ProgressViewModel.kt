package com.qdot.picdrive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProgressViewModel : ViewModel() {
    private val _progressCount = MutableLiveData<Long>()
    private val _done = MutableLiveData<Boolean>()
    val progressCount : LiveData<Long>
        get() = _progressCount
    val done : LiveData<Boolean>
        get() = _done

    fun setProgress(value : Long){
        _progressCount.postValue(value)
    }
    fun setDoneOrNot(final : Boolean){
        _done.postValue(final)
    }
}