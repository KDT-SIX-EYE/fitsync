package com.example.fitsync

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CalenderViewModel : ViewModel() {
private val _clickedDateState = MutableStateFlow(0)
    val clickedDateState: StateFlow<Int> = _clickedDateState

    fun updateClickedDate(date: Int) {
        _clickedDateState.value = date
    }
}