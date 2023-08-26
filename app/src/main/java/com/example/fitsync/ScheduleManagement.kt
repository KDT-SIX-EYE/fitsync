package com.example.fitsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

var calendarUiModel: CalendarUiModel? = null

class ScheduleManagement : ComponentActivity() {
//    private val calenderViewModel: CalenderViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val db = Firebase.firestore
            asd(db)
        }
    }
}

//data class SharedData(val clickedDate: Int)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun asd(db: FirebaseFirestore) {
    var calendarOpen by remember {
        mutableStateOf(false)
    }
    var clickedDate by remember {
        mutableStateOf(0)
    }
    var timeListOpen by remember {
        mutableStateOf(false)
    }
    Column {
        var memberName by remember {
            mutableStateOf("")
        }
        var selectedTime by remember {
            mutableStateOf(0) // 기본 시간을 선택합니다.
        }
        Button(onClick = { calendarOpen = true }) {
Text(text = "날짜")
        }
        Button(onClick = { timeListOpen = true }) {
            Text(text = "시간 $selectedTime")
        }
        val startTime = 7
        val endTime = 23
        val timeOptions = mutableListOf<Int>()

        for (hour in startTime until endTime) {
            timeOptions.add(hour)
        }

        Box {

            Column {
                TextField(value = memberName, onValueChange = { memberName = it })
                var trainers = listOf("Trainer 1", "Trainer 2", "Trainer 3")
                var trainerList by remember {
                    mutableStateOf(trainers)
                }
                var expandedTrainerIndex by remember { mutableStateOf(-1) }
                var selectedTrainer by remember { mutableStateOf("") }

                Button(onClick = { expandedTrainerIndex = if (expandedTrainerIndex == -1) 0 else -1 }) {
                    Text(text = "트레이너 선택")
                }
                if (expandedTrainerIndex != -1) {
                    trainerList.forEachIndexed { index, trainer ->
                        Text(
                            text = trainer,
                            modifier = Modifier
                                .clickable {
                                    selectedTrainer = trainer
                                    expandedTrainerIndex = -1
                                }
                        )
                    }
                }
                Button(
                    onClick = {
                        val userData = hashMapOf(
                            "Member Name" to memberName,
                            "Trainer Name" to selectedTrainer,
                            "Clicked Date" to clickedDate,
                            "Clicked Time" to selectedTime
                        )
                        db.collection("schedule").add(userData)
                    }
                )
                { Text(text = "예약") }
            }
            Column(modifier = Modifier.background(Color.LightGray)) {
                if (timeListOpen){
                    timeOptions.forEach { timeOption ->
                        Text(
                            text = "$timeOption ~ ${timeOption + 1}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTime = timeOption
                                    timeListOpen = false
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }

        }



        Text(text = "$clickedDate")
    }
    if (calendarOpen) {
        fun convertLocalDateToInt(dateModel: CalendarUiModel.Date): Int {
            val date = dateModel.date
            return date.year * 10000 + date.monthValue * 100 + date.dayOfMonth
        }

        CalendarWindow(onClick = {
            clickedDate = convertLocalDateToInt(calendarUiModel!!.selectedDate)
        }, onClickedDate = { calendarOpen = false })
    }

}

@Composable
fun CalendarWindow(onClick: () -> Unit, onClickedDate: () -> Unit) {
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
        )
    }
}


@Composable
fun Header(
    data: CalendarUiModel,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit,
    onMinusMonth: () -> Unit,
    onPlusMonth: () -> Unit
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
    onClickedDate: () -> Unit
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
    onClickedDate: () -> Unit
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