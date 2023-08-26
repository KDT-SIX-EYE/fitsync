package com.example.fitsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ScheduleCheckActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val db = Firebase.firestore
            TimeButton()
        }
    }
}

@Composable
fun ScheduleScreen() {
    Row {
        Box {
            TimeButton()
        }
    }
}

@Composable
fun TimeButton() {
    Column {
        var selectedTime by remember {
            mutableStateOf(0) // 기본 시간을 선택합니다.
        }
        val startTime = 7
        val endTime = 23
        val timeOptions = mutableListOf<Int>()

        for (hour in startTime until endTime) {
            timeOptions.add(hour)
        }
        timeOptions.forEach { timeOption ->
            Text(
                text = "$timeOption ~ ${timeOption + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedTime = timeOption
                    }
                    .padding(8.dp)
            )
        }
    }
}