package com.example.fitsync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext

class CalendarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                val context = LocalContext.current
                Button(onClick = { val intent = Intent(context, ScheduleCheckActivity::class.java)
                    context.startActivity(intent) }) {
                    Text(text = "예약 확인")
                }
                Button(onClick = {
                    val intent = Intent(context, ScheduleManagement::class.java)
                    context.startActivity(intent)
                }) {
                    Text(text = "예약")
                }
            }

        }
    }
}