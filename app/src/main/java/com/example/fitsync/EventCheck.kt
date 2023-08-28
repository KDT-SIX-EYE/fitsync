package com.example.fitsync

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.YearMonth

class EventCheckActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Firestore 초기화
            val db = Firebase.firestore

            // 이벤트 데이터 리스트를 가질 mutableStateOf
            val eventDataList = remember { mutableStateListOf<String>() }

            // Firestore에서 데이터 조회
            db.collection("EventData")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val eventDate = document.get("eventDate").toString()
                        val eventName = document.get("eventName").toString()
                        val registrant = document.get("registrant").toString()
                        val startTime = document.get("startTime").toString()
                        val endTime = document.get("endTime").toString()
                        eventDataList.add(eventDate)
                        eventDataList.add(eventName)
                        eventDataList.add(registrant)
                        eventDataList.add(startTime)
                        eventDataList.add(endTime)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                for (eventData in eventDataList) {
                    Text(text = eventData, modifier = Modifier.padding(4.dp))
                }
            }
            Text(
                text = "이벤트 데이터 목록",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}


//            LazyColumn {
//                items(eventDataList) { eventData ->
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp)
//                    ) {
//                        Text("날짜: ${eventData["eventDate"]}")
//                        Text("제목: ${eventData["eventName"]}")
//                        Text("등록자: ${eventData["registrant"]}")
//                        Text("시작시간: ${eventData["startTime"]}")
//                        Text("종료시간: ${eventData["endTime"]}")
//                        Spacer(modifier = Modifier.height(8.dp))
//                    }
//                }
//            }

//@Composable
//fun GetData() {
//    db.collection("users")
//        .get()
//        .addOnSuccessListener { result ->
//            for (document in result) {
//                Log.d(TAG, "${document.id} => ${document.data}")
//            }
//        }
//        .addOnFailureListener { exception ->
//            Log.w(TAG, "Error getting documents.", exception)
//        }
//}


// setcontent 내부
//            val context = LocalContext.current
//            val events by remember { mutableStateOf(mutableListOf<EventData>()) }
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//            ) {
//                Text(
//                    text = "이벤트 목록",
//                    style = MaterialTheme.typography.labelSmall,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
//                GetData
//
//                LazyColumn {
//                    items(events) { event ->
//
//                        Spacer(modifier = Modifier.height(16.dp))
//                    }


//    @Composable
//    fun EventCard(event: EventData) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp)
//            ) {
//                Text(text = "일자: ${event.eventDate}")
//                Text(text = "이름: ${event.eventName}")
//                Text(text = "등록자: ${event.registrant}")
//                Text(text = "시작시간: ${event.startTime}")
//                Text(text = "종료시간: ${event.endTime}")
//            }
//        }
//    }


//@Composable
//fun EventCheckContent(database: DatabaseReference) {
//    var eventData by remember { mutableStateOf(EventData()) }
//
//    Column {
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = {
//            // Firebase Realtime Database에 데이터 저장
//            database.child("events").push().setValue(eventData.toMap())
//        }) {
//            Text(text = "저장")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//    }
//}

//@Composable
//fun EventListItem(event: EventData) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth()
//        ) {
//            Text(text = "일정 날짜: ${event.eventDate}")
//            Text(text = "일정 이름: ${event.eventName}")
//            Text(text = "등록자: ${event.registrant}")
//            Text(text = "시작 시간: ${event.startTime}")
//            Text(text = "종료 시간: ${event.endTime}")
//        }
//    }
//}
//setContent 내 입력
// 데이터 가져오기
//            val db = Firebase.firestore
//            val eventsCollection = db.collection("events")
//
//            eventsCollection.get().addOnSuccessListener { result ->
//                    val events = result.documents.mapNotNull { document ->
//                        val eventDate = document.getString("eventDate")
//                        val eventName = document.getString("eventName")
//                        val registrant = document.getString("registrant")
//                        val startTime = document.getString("startTime")
//                        val endTime = document.getString("endTime")
//                        if (eventDate != null && eventName != null && registrant != null && startTime != null && endTime != null) {
//                            EventData(eventDate, eventName, registrant, startTime, endTime)
//                        } else {
//                            null
//                        }
//                    }
//
//                    val eventCheckContent = findViewById<ComposeView>(R.id.eventCheckContent)
//                    eventCheckContent.setContent {
//                        EventCheckContent(events)
//                    }
//                }
//
//                .addOnFailureListener { exception ->
//                    // Error handling
//                }


//@Composable
//fun EventCheckScreen() {
//    val context = LocalContext.current
//    val db = Firebase.firestore
//
//    val events = remember { mutableStateListOf<EventData>() } // 가져온 이벤트 데이터를 저장할 변수
//
//    Column {
//        Text(text = "저장된 이벤트 목록")
//        for (event in events) {
//            Text(text = "이벤트 날짜: ${event.eventDate}")
//            Text(text = "이벤트 이름: ${event.eventName}")
//            Text(text = "등록자: ${event.registrant}")
//            Text(text = "시작 시간: ${event.startTime}")
//            Text(text = "종료 시간: ${event.endTime}")
//            // 이벤트 간의 간격을 위한 간격 조정
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//    }
//}

@Composable
fun CalendarWindow2(
    data: CalendarUiModel,
    onClickedDate: () -> Unit,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
) {
    val context = LocalContext.current
    FirebaseApp.initializeApp(context)
    Column {
        var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
        Header(data = data,
            onPrevClickListener = {
                onPrevClickListener(data.startDate.date)
            },
            onNextClickListener = {
                onNextClickListener(data.endDate.date)
            },
            onMinusMonth = { currentYearMonth = currentYearMonth.minusMonths(1) },
            onPlusMonth = { currentYearMonth = currentYearMonth.plusMonths(1) })

        Content(
            currentYearMonth = currentYearMonth,
            data = data,
            onDateClickListener = onDateClickListener,
            onClickedDate = onClickedDate
        )
    }
}
