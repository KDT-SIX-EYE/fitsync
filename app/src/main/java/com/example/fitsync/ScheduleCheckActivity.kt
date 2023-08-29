package com.example.fitsync

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitsync.ui.theme.PurpleGrey40
import com.example.fitsync.ui.theme.PurpleGrey80
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale


class ScheduleCheckActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val db = Firebase.firestore
            FinalScreen2(db = db)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalScreen2(db: FirebaseFirestore) {
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
                            val intent = Intent(context, CalendarActivity::class.java)
                            context.startActivity(intent)
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
                            val intent = Intent(context, ScheduleActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_schedule_24),
                                contentDescription = "스케쥴 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "스케줄",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
                            val intent = Intent(context, BookActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_call_24),
                                contentDescription = "예약 액티비티로 이동"
                            )
                        }
                        Text(
                            text = "예약",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = {
//                            val intent = Intent(context, UsersActivity::class.java)
//                            context.startActivity(intent)
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
//                            val intent = Intent(context, MessengerActivity::class.java)
//                            context.startActivity(intent)
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
                ScheduleCheckScreen(db)
            }
        }
    }
}

@Composable
fun ScheduleCheckScreen(db: FirebaseFirestore) {
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
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "예약된 회원현황",
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Column {
                Box(
                    modifier = Modifier
                        .width(78.dp)
                        .height(23.dp)
                        .background(PurpleGrey80)
                ) {
                    Text(
                        text = "예약됨",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(78.dp)
                        .height(23.dp)
                        .background(Color.White)
                        .border(1.dp, Color.Black), // 테두리 추가
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "예약없음",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(400.dp)
                    .height(45.dp)
                    .background(Color(0xFFF781F3))
                    .border(1.dp, Color.Black), // 테두리 추가
                contentAlignment = Alignment.Center
            )
            {
                Text(
                    text = "▲ 날짜를 클릭하여 예약현황을 확인하세요 ▲",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
    TimeButton(db, clickedDate, timeListOpen)
}

@Composable
fun TimeButton(db: FirebaseFirestore, clickedDate: Int, timeListOpen: Boolean) {
    Row {
        if (timeListOpen) {
            Column {
                var selectTime by remember {
                    mutableStateOf(0) // 기본 시간을 선택합니다.
                }
                val startTime = 7
                val endTime = 23
                val timeOptions = mutableListOf<Int>()

                for (hour in startTime until endTime) {
                    timeOptions.add(hour)
                }

                var scheduledTImeList by remember { mutableStateOf(listOf<String>()) }
                var scheduledInfoList by remember { mutableStateOf(listOf<Triple<String, String, String>>()) }
                if (scheduledTImeList.isEmpty()) {
                    val tempList = mutableListOf<String>()
                    val tripelList = mutableListOf<Triple<String, String, String>>()
                    db.collection("schedule").document("$clickedDate").collection("Time").get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                val scheduledTime = document.get("Selected Time").toString()
                                val membername = document.get("Member Name").toString()
                                val trainername = document.get("Trainer Name").toString()
                                tempList.add(scheduledTime)
                                tripelList.add(Triple(scheduledTime, membername, trainername))
                                Log.d(TAG, "${document.id} => ${document.data}")
                            }
                            scheduledTImeList = tempList
                            scheduledInfoList = tripelList
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "$it")
                        }
                }

                var selectedButtonTime by remember { mutableStateOf(-1) }
                for (timeOption in timeOptions) {
                    val isSelectedAndInAffList =
                        scheduledTImeList.contains(timeOption.toString())
                    val buttonBackgroundColor = if (isSelectedAndInAffList) {       // 버튼 배경색
                        PurpleGrey80// scheduledTImeList에 포함되어 있으면 빨간색
                    } else {
                        Color.Transparent // 포함되어 있지 않으면 파란색
                    }

                    val buttonContentColor = if (isSelectedAndInAffList) {          //버튼 글자색
                        Color.White // scheduledTImeList에 포함되어 있으면 글자색 흰색
                    } else {
                        Color.Black // 포함되어 있지 않으면 글자색 검은색
                    }

                    Row {
                        var time_in_Scedule by remember {
                            mutableStateOf(false)
                        }
                        Column(modifier = Modifier.width(20.dp)) {
                            Text(text = "$timeOption")

                        }
                        Button(
                            onClick = {
                                selectTime = timeOption
                                selectedButtonTime = selectTime
                                time_in_Scedule = !time_in_Scedule// 선택된 버튼 시간 저장
                            },
                            modifier = Modifier.border(0.dp, Color.Black),
                            colors = ButtonDefaults.buttonColors(
                                buttonBackgroundColor,              // 버튼 배경색
                                contentColor = buttonContentColor // 버튼 내용의 글자색
                            ),
                            shape = RectangleShape
                        ) {
                        }

                        // 오른쪽에 선택된 버튼 정보 표시
                        if (time_in_Scedule) {
                            if (selectedButtonTime == timeOption) {
                                val matchingTriples =
                                    scheduledInfoList.filter { it.first == timeOption.toString() }
                                if (matchingTriples.isNotEmpty()) {
                                    LazyRow {
                                        item {
                                            for (matchingTriple in matchingTriples) {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(5.dp)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(PurpleGrey80)
                                                ) {
                                                    Column {

                                                        Text(
                                                            text = "회원 : ${matchingTriple.second}",
                                                            color = Color.Black,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                                        )
                                                        Text(
                                                            text = "트레이너 : ${matchingTriple.third}",
                                                            color = Color.Black,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                                        )

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
        }
    }
}

@Composable
fun CalendarWindow3(
    data: CalendarUiModel,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
    onTimeListOpen: () -> Unit,
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
fun Content3(
    currentYearMonth: YearMonth,
    data: CalendarUiModel,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
    onTimeListOpen: () -> Unit,
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
    onTimeListOpen: () -> Unit,
) {
    val textColor =
        if (date.date.year == currentYearMonth.year && date.date.month == currentYearMonth.month) {
            if (date.date.dayOfWeek == DayOfWeek.SUNDAY) {
                Color.Red
            } else if (date.date.dayOfWeek == DayOfWeek.SATURDAY) {
                Color.Blue
            } else {
                Color.Black
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