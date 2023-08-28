package com.example.fitsync

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

val db = Firebase.firestore

class ScheduleCheckActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScheduleCheckScreen()
        }
    }
}

@Composable
fun ScheduleCheckScreen() {
    var clickedDate by remember {
        mutableStateOf(0)
    }
    var timeListOpen by remember {
        mutableStateOf(false)
    }
    Column {
        Column {
            val dataSource = CalendarDataSource()
            var calendarUiModel by remember {
                mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today))
            }

            fun convertLocalDateToInt(dateModel: CalendarUiModel.Date): Int {
                val date = dateModel.date
                return date.year * 10000 + date.monthValue * 100 + date.dayOfMonth
            }
            CalendarWindow3(
                data = calendarUiModel,
                onPrevClickListener = { startDate ->
                    val finalStartDate = startDate.minusDays(1)
                    calendarUiModel = dataSource.getData(
                        startDate = finalStartDate,
                        lastSelectedDate = calendarUiModel.selectedDate.date
                    )
                },
                onNextClickListener = { endDate ->
                    val finalStartDate = endDate.plusDays(2)
                    calendarUiModel = dataSource.getData(
                        startDate = finalStartDate,
                        lastSelectedDate = calendarUiModel.selectedDate.date
                    )
                },
                onDateClickListener = { date ->
                    calendarUiModel = calendarUiModel.copy(
                        selectedDate = date,
                        visibleDates = calendarUiModel.visibleDates.map {
                            it.copy(
                                isSelected = it.date.isEqual(date.date)
                            )
                        }
                    )
                    clickedDate = convertLocalDateToInt(calendarUiModel.selectedDate)

                },
                onTimeListOpen = { timeListOpen = !timeListOpen }
            )
        }

        TimeButton(clickedDate, timeListOpen)
    }
}

@Composable
fun TimeButton(clickedDate: Int, timeListOpen: Boolean) {
    Row {
        if (timeListOpen) {
            LazyColumn {
                item {
                    var selectTime by remember {
                        mutableStateOf(0) // 기본 시간을 선택합니다.
                    }
                    val startTime = 7
                    val endTime = 23
                    val timeOptions = mutableListOf<Int>()

                    for (hour in startTime until endTime) {
                        timeOptions.add(hour)
                    }

                    var affList by remember { mutableStateOf(listOf<String>()) }
                    var wqeqweList by remember { mutableStateOf(listOf<Triple<String, String, String>>()) }
                    if (affList.isEmpty()) {
                        val tempList = mutableListOf<String>()
                        val tripelList = mutableListOf<Triple<String, String, String>>()
                        db.collection("schedule").document("$clickedDate").collection("Time").get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val aff = document.get("Selected Time").toString()
                                    val membername = document.get("Member Name").toString()
                                    val trainername = document.get("Trainer Name").toString()
                                    tempList.add(aff)
                                    tripelList.add(Triple(aff, membername, trainername))
                                    Log.d(TAG, "${document.id} => ${document.data}")
                                }
                                // affList를 업데이트하여 UI를 자동으로 업데이트
                                affList = tempList
                                wqeqweList = tripelList
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "$it")
                            }
                    }

                    Column {
                        for (item in wqeqweList) {
                            Text(text = item.first)
                            Text(text = item.second)
                        }
                    }
                    var selectedButtonTime by remember { mutableStateOf(-1) }
                    for (timeOption in timeOptions) {
                        val isSelectedAndInAffList = affList.contains(timeOption.toString())
                        val buttonBackgroundColor = if (isSelectedAndInAffList) {
                            Color.Red // affList에 포함되어 있으면 빨간색
                        } else {
                            Color.Blue // 포함되어 있지 않으면 파란색
                        }

                        val buttonContentColor = if (isSelectedAndInAffList) {
                            Color.White // affList에 포함되어 있으면 글자색 흰색
                        } else {
                            Color.Black // 포함되어 있지 않으면 글자색 검은색
                        }

                        Row {
                            Text(text = "$timeOption")
                            Button(
                                onClick = {
                                    selectTime = timeOption
                                    selectedButtonTime = selectTime // 선택된 버튼 시간 저장
                                },
                                colors = ButtonDefaults.buttonColors(
                                    buttonBackgroundColor,
                                    contentColor = buttonContentColor // 버튼 내용의 글자색 설정
                                )
                            ) {
                                Text(text = "$timeOption")
                            }

                            // 오른쪽에 선택된 버튼 정보 표시
                            if (selectedButtonTime == timeOption) {
                                val matchingTriples =
                                    wqeqweList.filter { it.first == timeOption.toString() }
                                if (matchingTriples.isNotEmpty()) {
                                    Column {
                                        for (matchingTriple in matchingTriples) {
                                            Text(text = "Member Name: ${matchingTriple.second}")
                                            Text(text = "Trainer Name: ${matchingTriple.third}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarWindow3(
    data: CalendarUiModel,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
    onTimeListOpen: () -> Unit
) {
    val context = LocalContext.current
    FirebaseApp.initializeApp(context)
    Column {
        var currentYearMonth by remember {
            mutableStateOf(YearMonth.now())
        }
        Header3(
            data = data,
            onPrevClickListener = {
                onPrevClickListener(data.startDate.date)
            },
            onNextClickListener = {
                onNextClickListener(data.endDate.date)
            },
            onMinusMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
            onPlusMonth = { currentYearMonth = currentYearMonth.plusMonths(1) }
        )

        Content3(
            currentYearMonth = currentYearMonth, data = data,
            onDateClickListener =
            onDateClickListener,
            onTimeListOpen
        )
    }
}


@Composable
fun Header3(
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
fun Content3(
    currentYearMonth: YearMonth,
    data: CalendarUiModel,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
    onTimeListOpen: () -> Unit
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
                    ContentItem3(
                        currentYearMonth = currentYearMonth,
                        date = date,
                        onDateClickListener,
                        onTimeListOpen
                    )
                }
            }
        }
    }
}

@Composable
fun ContentItem3(
    currentYearMonth: YearMonth,
    date: CalendarUiModel.Date,
    onClickListener: (CalendarUiModel.Date) -> Unit,
    onTimeListOpen: () -> Unit
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
                onTimeListOpen()
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


//fun asd(db: FirebaseFirestore, clickedDate: Int, selectedTime: Int,): String {
//    var scheduledName = ""
//    db.collection("schedule")
//        .document("$clickedDate")
//        .collection("asd").document("$selectedTime")
//        .get()
//        .addOnSuccessListener { document ->
//            Log.d(TAG, "DocumentSnapshot data: ${document.data}")
////                            Log.d(TAG, "${document.id} => ${document.data}")
//            var membername: String  = document.get("Member Name") as String
//            Log.d(TAG, membername)
//            if (membername != null) {
//                scheduledName = membername
//            } else {
//                Log.w(TAG, "Member Name is null")
//            }
//        }
//        .addOnFailureListener { exception ->
//            Log.w(TAG, "Error getting documents: ", exception)
//        }
//    return scheduledName
//}
