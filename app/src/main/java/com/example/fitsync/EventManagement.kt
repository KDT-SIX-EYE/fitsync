package com.example.fitsync

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

import java.util.Locale

var calendarUiModel: CalendarUiModel? = null //calendarUiModel 사용시 주석 풀기

class EventManagementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val db = Firebase.firestore
                var eventDate by remember { mutableStateOf("") }
                var eventName by remember { mutableStateOf("") }
                var registrant by remember { mutableStateOf("") }
                var startTime by remember { mutableStateOf("") }
                var endTime by remember { mutableStateOf("") }

                CalendarWindow(
                    onClick = { /*TODO*/ },
                    onClickedDate = { /*TODO*/ }
                )
                EventInputFields(
                    date = eventDate,
                    onDateChange = { eventDate = it },
                    eventname = eventName,
                    onNameChange = { eventName = it },
                    registrant = registrant,
                    onRegistrantChange = { registrant = it },
                    startTime = startTime,
                    onStartTimeChange = { startTime = it },
                    endTime = endTime,
                    onEndTimeChange = { endTime = it },
                    selectedDate = calendarUiModel?.selectedDate,
                    onSaveButtonClick = {
                        val event = hashMapOf(
                            "eventDate" to eventDate,
                            "eventName" to eventName,
                            "registrant" to registrant,
                            "startTime" to startTime,
                            "endTime" to endTime
                        )
                        db.collection("events")
                            .add(event)
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot added with ID: ${documentReference.id}"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    }
                )
                Button(onClick = {
                    val intent = Intent(context, EventCheckActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Text(text = "등록일정 확인하기")
                }
            }
        }
    }


    @Composable
    fun CalendarWindow(
        onClick: () -> Unit,
        onClickedDate: () -> Unit,
    ) {
        val context = LocalContext.current
        FirebaseApp.initializeApp(context)
        Column {

            val dataSource = CalendarDataSource()
            calendarUiModel = dataSource.getData(lastSelectedDate = dataSource.today)
            var currentYearMonth by remember {
                mutableStateOf(YearMonth.now())
            }
            Header(data = calendarUiModel!!,
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
                onPlusMonth = { currentYearMonth = currentYearMonth.plusMonths(1) })

            Content(
                currentYearMonth = currentYearMonth,
                data = calendarUiModel!!,
                onDateClickListener = { date ->
                    calendarUiModel = calendarUiModel!!.copy(
                        selectedDate = date,
                        visibleDates = calendarUiModel!!.visibleDates.map {
                            it.copy(
                                isSelected = it.date.isEqual(date.date)
                            )
                        })
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
                    text = currentMonth.value.format(
                        DateTimeFormatter.ofPattern(
                            "yyyy년 MMMM", Locale("ko")
                        )
                    ), modifier = Modifier.weight(1f), fontSize = 30.sp
                )

                IconButton(onClick = {
                    onPrevClickListener(data.startDate.date)
                    val newMonth = currentMonth.value.minusMonths(1)
                    currentMonth.value = newMonth
                    onMinusMonth()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft, contentDescription = "Back"
                    )
                }
                IconButton(onClick = {
                    onNextClickListener(data.endDate.date)
                    val newMonth = currentMonth.value.plusMonths(1)
                    currentMonth.value = newMonth
                    onPlusMonth()
                }) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight, contentDescription = "Next"
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
    @Composable
    fun ContentItem(
        currentYearMonth: YearMonth,
        date: CalendarUiModel.Date,
        eventDate: String,
        eventName: String,
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
        var lastClickTime by remember { mutableStateOf(0L) }
        var clickCount by remember { mutableStateOf(0) }


        Card(
            modifier = Modifier
                .width(50.dp)
                .height(80.dp)
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
                modifier = Modifier.fillMaxSize(),
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
                Spacer(modifier = Modifier.height(4.dp)) // 추가: 간격 조정
                Text(
                    text = eventDate, // 추가: Event 날짜 표시
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp)) // 추가: 간격 조정
                Text(
                    text = eventName, // 추가: Event 이름 표시
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EventInputFields(
        date: String,
        onDateChange: (String) -> Unit,
        eventname: String,
        onNameChange: (String) -> Unit,
        registrant: String,
        onRegistrantChange: (String) -> Unit,
        startTime: String,
        onStartTimeChange: (String) -> Unit,
        endTime: String,
        onEndTimeChange: (String) -> Unit,
        selectedDate: CalendarUiModel.Date?,
        onSaveButtonClick: () -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = date,
                onValueChange = onDateChange,
                label = { Text(text = "날짜설정") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = eventname,
                onValueChange = onNameChange,
                label = { Text(text = "제목설정") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = registrant,
                onValueChange = onRegistrantChange,
                label = { Text(text = "등록자") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = onStartTimeChange,
                    label = { Text(text = "시작시간") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = endTime,
                    onValueChange = onEndTimeChange,
                    label = { Text(text = "종료시간") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
             // 입력한 데이터 저장 버튼
            Button(
                onClick = {
                    val eventData = hashMapOf(
                        "eventDate" to date,
                        "eventName" to eventname,
                        "registrant" to registrant,
                        "startTime" to startTime,
                        "endTime" to  endTime
                    )
                    db.collection("EventData").
                    add(eventData)
                        .addOnSuccessListener {  documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = "저장")
            }
        }
    }
}


//                if (enteredText.isNotEmpty()) {
//                    Text(
//                        text = enteredText,
//                        modifier = Modifier
//                            .padding(top = 4.dp)
//                            .fillMaxWidth()
//                            .wrapContentWidth(Alignment.CenterHorizontally),
//                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 8.sp),
//                    color = textColor
//                    )
//                }

//        if (isPopupVisible) {
//            AlertDialog(
//                onDismissRequest = closePopup,
//                title = { Text(text = "일정등록") },
//                text = {
//                    Column {
//                        TextField(
//                            value = enteredText,
//                            onValueChange = { enteredText = it },
//                            label = { Text("일정내용입력") }
//                        )
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier.padding(top = 8.dp)
//                        ) {
//                            Text("색 선택:", modifier = Modifier.padding(end = 4.dp))
//                            ColorPicker(selectedColor, colorOptions) { color ->
//                                selectedColor = color
//                            }
//                        }
//                    }
//                },
//                confirmButton = {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.End
//                    ) {
//                        Button(
//                            onClick = closePopup,
//                            modifier = Modifier.padding(12.dp)
//                        ) {
//                            Text(text = "닫기")
//                        }
//                        Button(
//                            onClick = {
//                                val userData = hashMapOf(
//                                    "EventTitle" to enteredText,
//                                    "PickedColor" to selectedColor,
//                                )
//                                db.collection("Event").add(userData)
//                            }
//                        )
//                        { Text(text = "저장") }
//                    }
//                }
//            )
//        }

//    @Composable
//    fun ColorPicker(
//        selectedColor: Color,
//        colorOptions: List<Color>,
//        onColorSelected: (Color) -> Unit,
//    ) {
//        Row {
//            colorOptions.forEach { color ->
//                Box(
//                    modifier = Modifier
//                        .size(24.dp)
//                        .clickable {
//                            onColorSelected(color)
//                        }
//                        .background(
//                            color = color,
//                            shape = CircleShape
//                        )
//                        .padding(4.dp)
//                ) {
//                    if (color == selectedColor) {
//                        Icon(
//                            imageVector = Icons.Default.Done,
//                            contentDescription = "Selected",
//                            tint = Color.White
//                        )
//                    }
//                }
//            }
//        }
//    }