package com.example.fitsync

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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

class CalendarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val db = Firebase.firestore
            FinalScreen(db = db)

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalScreen(db: FirebaseFirestore) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        // 상단바 이름 수정하십시오. 그 외 변경 금지
                        text = "My Profile",
                        fontSize = 17.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val context = LocalContext.current

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
//                            val intent = Intent(context, CalenderActivity::class.java)
//                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "캘린더 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "캘린더",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_home_24),
                                contentDescription = "메인 액티비티(홈)으로 이동"
                            )
                        }
                        Text(
                            text = "Home",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            val intent = Intent(context, AttendanceActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_qr_code_2_24),
                                contentDescription = "QR 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "QR",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            val intent = Intent(context, UsersActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "사용자 목록 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "프로필",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            val intent = Intent(context, MessengerActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_mark_chat_unread_24),
                                contentDescription = "메신저 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "채팅방",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                CalendarActivityScreen(db)
            }
        }
    }
}

@Composable
fun CalendarActivityScreen(db: FirebaseFirestore) {
    Column {
        val dataSource = CalendarDataSource()
        val calendarUiModel by remember {
            mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today))
        }
        val context = LocalContext.current
        var eventDate by remember { mutableStateOf("") }
        var eventName by remember { mutableStateOf("") }
        var registrant by remember { mutableStateOf("") }
        var startTime by remember { mutableStateOf("") }
        var endTime by remember { mutableStateOf("") }
        var showEventInputFields by remember { mutableStateOf(false) }
        var showEventCheckFields by remember {
            mutableStateOf(false)
        }
        val eventDataList = remember { mutableStateListOf<EventData>() }

        CalendarWindow2(eventDataList)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            Button(
                onClick = {
                    val intent = Intent(context, ScheduleCheckActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .sizeIn(minHeight = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.bookcheck), // 예약확인 아이콘 리소스 ID로 대체
//                    contentDescription = null,
//                    modifier = Modifier.size(24.dp)
//                )
                    Text(text = "예약확인")
                }
            }
            Button(
                onClick = {
                    val intent = Intent(context, ScheduleManagement::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .sizeIn(minHeight = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.book), // 예약 아이콘 리소스 ID로 대체
//                    contentDescription = null,
//                    modifier = Modifier.size(24.dp)
//                )
                    Text(text = "예약하기")
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    showEventInputFields = !showEventInputFields // 버튼을 누르면 입력 필드를 보이도록 상태 변경
                },
                modifier = Modifier
                    .padding(8.dp)
                    .sizeIn(minHeight = 0.dp)
            ) {
                Text(text = "일정 등록하기")
            }
            Button(
                onClick = {
//                        val intent = Intent(context, EventCheckActivity::class.java)
//                        context.startActivity(intent)
                    showEventCheckFields = !showEventCheckFields
                },
                modifier = Modifier
                    .padding(8.dp)
                    .sizeIn(minHeight = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.schedule), // 일정등록 아이콘 리소스 ID로 대체
//                        contentDescription = null,
//                        modifier = Modifier.size(24.dp)
//                    )
                    Text(text = "일정확인")
                }
            }
        }
        if (showEventInputFields) {
            Column {
                EventInputFields(date = eventDate,
                    onDateChange = { eventDate = it },
                    eventname = eventName,
                    onNameChange = { eventName = it },
                    registrant = registrant,
                    onRegistrantChange = { registrant = it },
                    startTime = startTime,
                    onStartTimeChange = { startTime = it },
                    endTime = endTime,
                    onEndTimeChange = { endTime = it },
                    selectedDate = calendarUiModel.selectedDate,
                    onSaveButtonClick = {
                        val event = hashMapOf(
                            "eventDate" to eventDate,
                            "eventName" to eventName,
                            "registrant" to registrant,
                            "startTime" to startTime,
                            "endTime" to endTime
                        )
                        db.collection("EventData").add(event)
                            .addOnSuccessListener { documentReference ->

                            }.addOnFailureListener { e ->

                            }
                        showEventInputFields = false
                    },
                    onCancelButtonClick = {
                        showEventInputFields = false
                    })
            }
        }

        db.collection("EventData")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val seenEventDates = HashSet<String>() // 이미 나온 날짜들을 저장할 Set
                val newEventDataList = mutableListOf<EventData>()
                for (document in querySnapshot) {
                    val eventDate = document.get("eventDate").toString()
                    if (eventDate !in seenEventDates) { // 중복이 아닌 경우만 추가
                        seenEventDates.add(eventDate)
                        val eventName = document.get("eventName").toString()
                        val registrant = document.get("registrant").toString()
                        val startTime = document.get("startTime").toString()
                        val endTime = document.get("endTime").toString()
                        newEventDataList.add(
                            EventData(
                                eventDate,
                                eventName,
                                registrant,
                                startTime,
                                endTime
                            )
                        )
                    }
                }
                eventDataList.addAll(newEventDataList)
            }
            .addOnFailureListener { exception ->
            }
        if (showEventCheckFields) {
            val distinctEventDataList = eventDataList.distinctBy { it.eventDate }

            Column {

                Text(
                    text = "일정 목록",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                distinctEventDataList.forEach { eventData ->
                    EventCard(eventData)
                }

            }

        }
    }
}

@Composable
fun CalendarWindow2(eventDataList: List<EventData>) {
    val context = LocalContext.current
    FirebaseApp.initializeApp(context)
    Column {

        val dataSource = CalendarDataSource()
        var calendarUiModel by remember {
            mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today))
        }
        var currentYearMonth by remember {
            mutableStateOf(YearMonth.now())
        }
        Header2(
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
            onMinusMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
            onPlusMonth = { currentYearMonth = currentYearMonth.plusMonths(1) }
        )
        fun convertLocalDateToInt(dateModel: CalendarUiModel.Date): Int {
            val date = dateModel.date
            return date.year * 10000 + date.monthValue * 100 + date.dayOfMonth
        }

        var clickedDate by remember {
            mutableStateOf(0)
        }
        Content2(
            currentYearMonth = currentYearMonth, data = calendarUiModel,
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

            }, eventDataList = eventDataList
        )
    }
}


@Composable
fun Header2(
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
fun Content2(
    currentYearMonth: YearMonth,
    data: CalendarUiModel,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
    eventDataList: List<EventData>

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
                    ContentItem2(
                        currentYearMonth = currentYearMonth,
                        date = date,
                        onDateClickListener,
                        eventDataList = eventDataList

                    )
                }
            }
        }
    }
}

@Composable
fun ContentItem2(
    currentYearMonth: YearMonth,
    date: CalendarUiModel.Date,
    onClickListener: (CalendarUiModel.Date) -> Unit,
    eventDataList: List<EventData>
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
    }
//    else if (isDateInEventDataList(date, eventDataList)) {
//        Color.Yellow // eventDataList에 해당 날짜의 데이터가 있으면 검은색 배경색
//    }
    else {
        Color.White
    }
    var lastClickTime by remember {
        mutableStateOf(0L)
    }
    var clickCount by remember { mutableStateOf(0) }


    Card(
        modifier = Modifier
            .width(45.dp)
            .height(75.dp)
            .clickable {
                clickCount++
                val currentTime = System.currentTimeMillis()

                lastClickTime = currentTime
                onClickListener(date)


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
            if (isDateInEventDataList(date, eventDataList)) {
                val matchingEventData = getEventDataForDate(date, eventDataList)
                Text(
                    text = matchingEventData?.eventName ?: "",
                    fontSize = 12.sp
                )
            }
        }
    }
}

fun getEventDataForDate(date: CalendarUiModel.Date, eventDataList: List<EventData>): EventData? {
    val dateString = date.date.format(DateTimeFormatter.ISO_DATE)
    return eventDataList.find { it.eventDate == dateString }
}

fun isDateInEventDataList(date: CalendarUiModel.Date, eventDataList: List<EventData>): Boolean {
    val dateString = date.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    return eventDataList.any { it.eventDate == dateString }
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
    onCancelButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = date,
            onValueChange = onDateChange,
            label = { Text(text = "날짜만 입력하세요") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = eventname,
            onValueChange = onNameChange,
            label = { Text(text = "일정이름") },
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Button(
                onClick = {
                    if (isInputValid(
                            date = date,
                            eventname = eventname,
                            registrant = registrant,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) {
                        onSaveButtonClick()
                    }
                }, modifier = Modifier.weight(1f)
            ) {
                Text(text = "일정 등록 완료")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onCancelButtonClick, modifier = Modifier.weight(1f)
            ) {
                Text(text = "취소")
            }
        }
    }
}

private fun isInputValid(
    date: String,
    eventname: String,
    registrant: String,
    startTime: String,
    endTime: String,
): Boolean {
    return date.isNotBlank() && eventname.isNotBlank() && registrant.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank()
}

///////////////////////////
/////////////////////////// 일정 확인하기
data class EventData(
    val eventDate: String,
    val eventName: String,
    val registrant: String,
    val startTime: String,
    val endTime: String,
)

@Composable
fun EventCard(eventData: EventData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "날짜: ${eventData.eventDate}일")
            Text(text = "일정이름: ${eventData.eventName}")
            Text(text = "등록자: ${eventData.registrant}")
            Text(text = "시작시간: ${eventData.startTime}")
            Text(text = "종료시간: ${eventData.endTime}")
        }
    }
}