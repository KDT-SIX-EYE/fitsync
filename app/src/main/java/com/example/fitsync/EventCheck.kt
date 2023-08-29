//package com.example.fitsync
//
//import android.content.ContentValues.TAG
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.google.firebase.FirebaseApp
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import java.time.LocalDate
//import java.time.YearMonth
//
//class EventCheckActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            // Firestore 초기화
//            val db = Firebase.firestore
//            // 이벤트 데이터 리스트를 가질 mutableStateOf
//            val  eventDataList = remember { mutableStateListOf<EventData>() }
//            // Firestore에서 데이터 조회
//            db.collection("EventData")
//                .get()
//                .addOnSuccessListener { querySnapshot ->
//                    val seenEventDates = HashSet<String>() // 이미 나온 날짜들을 저장할 Set
//                    val newEventDataList = mutableListOf<EventData>()
//                    for (document in querySnapshot) {
//                        val eventDate = document.get("eventDate").toString()
//                        if (eventDate !in seenEventDates) { // 중복이 아닌 경우만 추가
//                            seenEventDates.add(eventDate)
//                            val eventName = document.get("eventName").toString()
//                            val registrant = document.get("registrant").toString()
//                            val startTime = document.get("startTime").toString()
//                            val endTime = document.get("endTime").toString()
//                            newEventDataList.add(
//                                EventData(
//                                    eventDate,
//                                    eventName,
//                                    registrant,
//                                    startTime,
//                                    endTime
//                                )
//                            )
//                        }
//                    }
//                    eventDataList.addAll(newEventDataList)
//                }
//                .addOnFailureListener { exception ->
//                    Log.w(TAG, "Error getting documents.", exception)
//                }
//            val initialDate = LocalDate.now()
//            val distinctEventDataList = eventDataList.distinctBy { it.eventDate }
//            val calendarDataSource = CalendarDataSource()
//            var calendarUiModel by remember {
//                mutableStateOf(
//                    calendarDataSource.getData(
//                        lastSelectedDate = initialDate
//                    )
//                )
//            }
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(8.dp)
//                    .verticalScroll(rememberScrollState())
//            ) {
//                CalendarWindow(
//                    data = calendarUiModel,
//                    onClickedDate = { /*TODO*/ },
//                    onPrevClickListener = { startDate ->
//                        val finalStartDate = startDate.minusDays(1)
//                        val newCalendarUiModel = calendarDataSource.getData(
//                            startDate = finalStartDate,
//                            lastSelectedDate = calendarUiModel.selectedDate.date
//                        )
//                        calendarUiModel = newCalendarUiModel
//                    },
//                    onNextClickListener = { endDate ->
//                        val finalStartDate = endDate.plusDays(2)
//                        val newCalendarUiModel = calendarDataSource.getData(
//                            startDate = finalStartDate,
//                            lastSelectedDate = calendarUiModel.selectedDate.date
//                        )
//                        calendarUiModel = newCalendarUiModel
//                    },
//                    onDateClickListener = { date -> }
//                )
//                Text(
//                    text = "일정 목록",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
//                distinctEventDataList.forEach { eventData ->
//                    EventCard(eventData)
//                }
//            }
//        }
//    }
//}
//
//
//data class EventData(
//    val eventDate: String,
//    val eventName: String,
//    val registrant: String,
//    val startTime: String,
//    val endTime: String,
//)
//
//@Composable
//fun EventCard(eventData: EventData) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(text = "날짜: ${eventData.eventDate}일")
//            Text(text = "일정이름: ${eventData.eventName}")
//            Text(text = "등록자: ${eventData.registrant}")
//            Text(text = "시작시간: ${eventData.startTime}")
//            Text(text = "종료시간: ${eventData.endTime}")
//        }
//    }
//}
//
//
//@Composable
//fun CalendarWindow(
//    data: CalendarUiModel,
//    onClickedDate: () -> Unit,
//    onPrevClickListener: (LocalDate) -> Unit,
//    onNextClickListener: (LocalDate) -> Unit,
//    onDateClickListener: (CalendarUiModel.Date) -> Unit,
//) {
//    val context = LocalContext.current
//    FirebaseApp.initializeApp(context)
//    Column {
//        var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
//        Header(data = data,
//            onPrevClickListener = {
//                onPrevClickListener(data.startDate.date)
//            },
//            onNextClickListener = {
//                onNextClickListener(data.endDate.date)
//            },
//            onMinusMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
//            onPlusMonth = { currentYearMonth = currentYearMonth.plusMonths(1) })
//
//        Content(
//            currentYearMonth = currentYearMonth,
//            data = data,
//            onDateClickListener = onDateClickListener,
//            onClickedDate = onClickedDate,
//        )
//    }
//}
