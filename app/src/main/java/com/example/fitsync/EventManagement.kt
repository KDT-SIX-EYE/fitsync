package com.example.fitsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class EventManagementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarWindow(onClick = { /*TODO*/ }) {

            }
        }
    }

    @Composable
    fun CalendarWindow (onClick: () -> Unit, onClickedDate: () -> Unit){
        val context = LocalContext.current
        FirebaseApp.initializeApp(context)
        Column {

            val dataSource = CalendarDataSource()
            calendarUiModel = dataSource.getData(lastSelectedDate = dataSource.today)
            var currentYearMonth by remember {
                mutableStateOf(YearMonth.now())
            }
            Header(
                data = calendarUiModel!!,
                onPrevClickListener = { startDate ->
                    val finalStartDate = startDate.minusDays(1)
                    calendarUiModel = dataSource.getData(
                        startDate = finalStartDate,
                        lastSelectedDate = calendarUiModel!!.selectedDate.date
                    )
                },
                onNextClickListener = { endDate ->
                    val finalStartDate = endDate.plusDays(2)
                    calendarUiModel = dataSource.getData(
                        startDate = finalStartDate,
                        lastSelectedDate = calendarUiModel!!.selectedDate.date
                    )
                },
                onMinusMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
                onPlusMonth = { currentYearMonth = currentYearMonth.plusMonths(1) }
            )

            Content(
                currentYearMonth = currentYearMonth, data = calendarUiModel!!,
                onDateClickListener = { date ->
                    calendarUiModel = calendarUiModel!!.copy(
                        selectedDate = date,
                        visibleDates = calendarUiModel!!.visibleDates.map {
                            it.copy(
                                isSelected = it.date.isEqual(date.date)
                            )
                        }
                    )
                    onClick()
                },
                onClickedDate = onClickedDate
            )        }
    }
    @Composable
    fun Header(
        data: CalendarUiModel,
        onPrevClickListener: (LocalDate) -> Unit,
        onNextClickListener: (LocalDate) -> Unit,
        onMinusMonth: () -> Unit,
        onPlusMonth: () -> Unit,
    ) {
        val currentYearMonth = YearMonth.now()
        val currentMonth = remember { mutableStateOf(currentYearMonth) }
        Column(modifier = Modifier.padding(10.dp)) {
            Row {
                Text(
                    text =
                    currentMonth.value.format(
                        DateTimeFormatter.ofPattern(
                            "yyyy년 MMMM",
                            Locale("ko")
                        )
                    ),
                    modifier = Modifier.weight(1f),
                    fontSize = 30.sp
                )

                IconButton(onClick = {
                    onPrevClickListener(data.startDate.date)
                    val newMonth = currentMonth.value.minusMonths(1)
                    currentMonth.value = newMonth
                    onMinusMonth()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Back"
                    )
                }
                IconButton(onClick = {
                    onNextClickListener(data.endDate.date)
                    val newMonth = currentMonth.value.plusMonths(1)
                    currentMonth.value = newMonth
                    onPlusMonth()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Next"
                    )
                }
            }
        }
    }

    @Composable
    fun Content(
        currentYearMonth: YearMonth,
        data: CalendarUiModel,
        onDateClickListener: (CalendarUiModel.Date) -> Unit,
        onClickedDate: () -> Unit,
    ) {
        Column {
            repeat(6) { rowIndex ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val startIndex = rowIndex * 7
                    val endIndex = minOf(startIndex + 7, data.visibleDates.size)
                    for (i in startIndex until endIndex) {
                        val date = data.visibleDates[i]
                        ContentItem(
                            currentYearMonth = currentYearMonth,
                            date = date,
                            onDateClickListener,
                            onClickedDate = onClickedDate
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ContentItem(
        currentYearMonth: YearMonth,
        date: CalendarUiModel.Date,
        onClickListener: (CalendarUiModel.Date) -> Unit,
        onClickedDate: () -> Unit,
    ) {
        val textColor =
            if (date.date.year == currentYearMonth.year && date.date.month == currentYearMonth.month) {
                if (date.date.dayOfWeek == DayOfWeek.SUNDAY) {
                    Color.Red
                } else if (date.date.dayOfWeek == DayOfWeek.SATURDAY) {
                    Color.Blue
                } else {
                    MaterialTheme.colorScheme.primary
                }
            } else {
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            }
        val backgroundColor = if (date.isToday && !date.isSelected) {
            Color.LightGray
        } else if (date.isSelected) {
            Color(0xFFF781F3).copy(alpha = 0.7f)
        } else {
            Color.White
        }
        var lastClickTime by remember {
            mutableStateOf(0L)
        }
        var clickCount by remember { mutableStateOf(0) }


        Card(
            modifier = Modifier
                .width(45.dp)
                .height(45.dp)
                .clickable {
                    clickCount++
                    val currentTime = System.currentTimeMillis()

                    lastClickTime = currentTime
                    onClickListener(date)
                    onClickedDate()

                },
            colors = CardDefaults.cardColors(
                backgroundColor
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.day,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = textColor
                )
                Text(
                    text = date.date.dayOfMonth.toString(),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = textColor
                )
            }
        }
    }
}