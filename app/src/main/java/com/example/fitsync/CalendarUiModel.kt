package com.example.fitsync

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

data class CalendarUiModel(
    val selectedDate: Date,
    val visibleDates: List<Date>,

) {
    val startDate: Date = visibleDates.first()
    val endDate: Date = visibleDates.last()

    data class Date(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean,
        val eventName: String? = null // 추가: 이벤트 이름을 위한 속성
    ) {
        val day: String = date.format(DateTimeFormatter.ofPattern("E"))
    }
}
