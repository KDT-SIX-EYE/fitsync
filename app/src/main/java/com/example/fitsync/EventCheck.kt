package com.example.fitsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class EventCheckActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventCheckScreen()
        }
    }
}

@Composable
fun EventCheckScreen() {
    val context = LocalContext.current
    val db = Firebase.firestore

    val events = remember { mutableStateListOf<EventData>() } // 가져온 이벤트 데이터를 저장할 변수

    // 화면이 활성화된 동안만 데이터를 가져오도록 함
    LaunchedEffect(Unit) {
        val querySnapshot = db.collection("events").get().await() // 비동기 호출
        val eventDataList = querySnapshot.toObjects(EventData::class.java)
        events.addAll(eventDataList)
    }

    Column {
        Text(text = "저장된 이벤트 목록")
        for (event in events) {
            Text(text = "이벤트 날짜: ${event.eventDate}")
            Text(text = "이벤트 이름: ${event.eventName}")
            Text(text = "등록자: ${event.registrant}")
            Text(text = "시작 시간: ${event.startTime}")
            Text(text = "종료 시간: ${event.endTime}")
            // 이벤트 간의 간격을 위한 간격 조정
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}