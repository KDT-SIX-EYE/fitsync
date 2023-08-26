package com.example.fitsync

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

//var calendarUiModel: CalendarUiModel? = null //calendarUiModel 사용시 주석 풀기
class EventManagementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            )
            {
                val db = Firebase.firestore
                CalendarWindow(onClick = { /*TODO*/ }) {}
            }
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

    @SuppressLint("UnrememberedMutableState")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ContentItem(
        currentYearMonth: YearMonth,
        date: CalendarUiModel.Date,
        onClickListener: (CalendarUiModel.Date) -> Unit,
        onClickedDate: () -> Unit,
    ) {
        var isPopupVisible by remember { mutableStateOf(false) } // 팝업 띄울때 사용변수
        val closePopup = { isPopupVisible = false } // 팝업 닫는함수
        var enteredText by remember { mutableStateOf("") } // 사용자가 입력한 텍스트
        val colorOptions = listOf(Color.Red, Color(0xFF800080), Color.Green)
        var selectedColor by mutableStateOf(colorOptions[0])
        val db = Firebase.firestore


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
                .width(50.dp)
                .height(70.dp)
                .clickable {
                    clickCount++
                    val currentTime = System.currentTimeMillis()

                    lastClickTime = currentTime
                    onClickListener(date)
                    onClickedDate()

                    isPopupVisible = true // 팝업코드

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
                if (enteredText.isNotEmpty()) {
                    Text(
                        text = enteredText,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 8.sp),
                    color = textColor
                    )
                }
            }
        }

        if (isPopupVisible) {
            AlertDialog(
                onDismissRequest = closePopup,
                title = { Text(text = "일정등록") },
                text = {
                    Column {
                        TextField(
                            value = enteredText,
                            onValueChange = { enteredText = it },
                            label = { Text("일정내용입력") }
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("색 선택:", modifier = Modifier.padding(end = 4.dp))
                            ColorPicker(selectedColor, colorOptions) { color ->
                                selectedColor = color
                            }
                        }
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = closePopup,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(text = "닫기")
                        }
                        Button(
                            onClick = {
                                val userData = hashMapOf(
                                    "EventTitle" to enteredText,
                                    "PickedColor" to selectedColor,
                                )
                                db.collection("Event").add(userData)
                            }
                        )
                        { Text(text = "저장") }
                    }
                }
            )
        }
    }

    @Composable
    fun ColorPicker(
        selectedColor: Color,
        colorOptions: List<Color>,
        onColorSelected: (Color) -> Unit,
    ) {
        Row {
            colorOptions.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onColorSelected(color)
                        }
                        .background(
                            color = color,
                            shape = CircleShape
                        )
                        .padding(4.dp)
                ) {
                    if (color == selectedColor) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Selected",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

